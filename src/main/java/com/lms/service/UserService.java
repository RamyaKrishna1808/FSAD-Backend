package com.lms.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.dto.ApiResponse;
import com.lms.dto.ForgotPasswordRequest;
import com.lms.dto.LoginRequest;
import com.lms.dto.RegisterRequest;
import com.lms.dto.ResetPasswordRequest;
import com.lms.dto.UserResponse;
import com.lms.model.Role;
import com.lms.model.User;
import com.lms.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String ADMIN_TEACHER_OTP = "123456";
    private static final long PASSWORD_RESET_OTP_TTL_SECONDS = 10 * 60;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, OtpRecord> passwordResetOtps = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public ResponseEntity<ApiResponse<UserResponse>> register(RegisterRequest request) {
        validateRegisterRequest(request);
        String email = normalizeEmail(request.getEmail());
        Role role = parseRole(request.getRole());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Signup failed: duplicate email={}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email is already registered"));
        }

        User user = new User(null, request.getName().trim(), email, request.getPassword(), role);
        UserResponse response = toResponse(userRepository.save(user));
        log.info("Registered user email={} role={}", response.getEmail(), response.getRole());
        return ResponseEntity.ok(ApiResponse.success("Signup successful", response));
    }

    public ResponseEntity<ApiResponse<UserResponse>> login(LoginRequest request) {
        validateLoginRequest(request);
        Role requestedRole = parseRole(request.getRole());
        String email = normalizeEmail(request.getEmail());
        log.info("Login attempt email={} role={}", email, requestedRole);

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !safeEquals(user.getPassword(), request.getPassword())) {
            log.warn("Login failed: invalid credentials for email={}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        if (user.getRole() != requestedRole) {
            log.warn("Login failed: role mismatch for email={} requestedRole={} actualRole={}",
                    email, requestedRole, user.getRole());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        if (user.getRole() == Role.ADMIN_TEACHER && !ADMIN_TEACHER_OTP.equals(request.getOtp())) {
            log.warn("Login failed: invalid OTP for admin/teacher email={}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        log.info("Login successful email={} role={}", email, user.getRole());
        return ResponseEntity.ok(ApiResponse.success("Login successful", toResponse(user)));
    }

    public ResponseEntity<ApiResponse<Void>> forgotPassword(ForgotPasswordRequest request) {
        validateForgotPasswordRequest(request);
        String email = normalizeEmail(request.getEmail());
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            log.warn("Forgot password failed: email not registered email={}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email is not registered"));
        }

        String otp = generateOtp();
        passwordResetOtps.put(email, new OtpRecord(otp, Instant.now().plusSeconds(PASSWORD_RESET_OTP_TTL_SECONDS)));
        log.info("Generated password reset OTP for email={}", email);

        boolean sent = emailService.sendEmail(email, "Password reset OTP", "Your OTP is: " + otp);
        if (!sent) {
            log.warn("Password reset OTP email could not be sent to email={}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Could not send OTP email"));
        }

        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email", null));
    }

    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(ResetPasswordRequest request) {
        validateResetPasswordRequest(request);
        String email = normalizeEmail(request.getEmail());
        OtpRecord otpRecord = passwordResetOtps.get(email);
        if (otpRecord == null || otpRecord.expiresAt().isBefore(Instant.now()) || !otpRecord.otp().equals(request.getOtp())) {
            log.warn("Reset password failed: invalid or expired OTP for email={}", email);
            passwordResetOtps.remove(email, otpRecord);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid OTP"));
        }

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null) {
            log.warn("Reset password failed: email not registered email={}", email);
            passwordResetOtps.remove(email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email is not registered"));
        }

        user.setPassword(request.getNewPassword());
        UserResponse response = toResponse(userRepository.save(user));
        passwordResetOtps.remove(email);
        log.info("Password reset successful for email={}", email);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", response));
    }

    public User getUser(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User response could not be created");
        }
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Register request is required");
        }
        if (isBlank(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (isBlank(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (isBlank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (isBlank(request.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login request is required");
        }
        if (isBlank(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (isBlank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (isBlank(request.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }
    }

    private void validateForgotPasswordRequest(ForgotPasswordRequest request) {
        if (request == null || isBlank(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
    }

    private void validateResetPasswordRequest(ResetPasswordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset password request is required");
        }
        if (isBlank(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (isBlank(request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP is required");
        }
        if (isBlank(request.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password is required");
        }
    }

    private Role parseRole(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("-", "_");

        if (normalized.equals("TEACHER")
                || normalized.equals("ADMIN")
                || normalized.equals("TEACHER_ADMIN")
                || normalized.equals("ADMIN_TEACHER")
                || normalized.contains("TEACHER")) {
            return Role.ADMIN_TEACHER;
        }
        if (normalized.equals("STUDENT") || normalized.contains("STUDENT")) {
            return Role.STUDENT;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be ADMIN_TEACHER or STUDENT");
    }

    private String generateOtp() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean safeEquals(String storedPassword, String requestedPassword) {
        return storedPassword != null && storedPassword.equals(requestedPassword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record OtpRecord(String otp, Instant expiresAt) {
    }
}

