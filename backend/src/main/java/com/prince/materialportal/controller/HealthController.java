package com.prince.materialportal.controller;

import com.prince.materialportal.dto.ApiResponse;
import com.prince.materialportal.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "System Health", description = "Endpoints for health and diagnostics")
public class HealthController {

    @Value("${spring.application.name:material-portal}")
    private String appName;

    @GetMapping
    @Operation(summary = "System health check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> healthInfo = Map.of(
                "status", "UP",
                "application", appName,
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("System is healthy", healthInfo));
    }
}
