package com.lms.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String dir = "uploads";

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Path getUploadPath() {
        return Paths.get(dir).toAbsolutePath().normalize();
    }
}
