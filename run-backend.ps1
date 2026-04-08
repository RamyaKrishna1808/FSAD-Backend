Set-Location $PSScriptRoot
Write-Host "Starting LMS backend on http://localhost:8080"
mvn spring-boot:run
