package com.prince.materialportal.controller;

import com.prince.materialportal.dto.ApiResponse;
import com.prince.materialportal.dto.DashboardStatsResponse;
import com.prince.materialportal.dto.MaterialCreateRequest;
import com.prince.materialportal.dto.MaterialUpdateRequest;
import com.prince.materialportal.entity.Material;
import com.prince.materialportal.service.MaterialService;
import com.prince.materialportal.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty/materials")
@RequiredArgsConstructor
@Tag(name = "Faculty Management", description = "Endpoints for faculty staff and admins to manage study materials")
public class FacultyMaterialController {

    private final MaterialService materialService;

    @PostMapping
    @Operation(summary = "Create a new study material record")
    public ResponseEntity<ApiResponse<Material>> createMaterial(@Valid @RequestBody MaterialCreateRequest request) {
        Material created = materialService.createMaterial(request);
        return ResponseEntity.status(201).body(ResponseBuilder.buildCreated("Material created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing study material record")
    public ResponseEntity<ApiResponse<Material>> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MaterialUpdateRequest request
    ) {
        Material updated = materialService.updateMaterial(id, request);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Material updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a study material record by ID")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Material deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get all study materials for faculty dashboard table")
    public ResponseEntity<ApiResponse<List<Material>>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Materials retrieved successfully", materials));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific study material by ID")
    public ResponseEntity<ApiResponse<Material>> getMaterialById(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Material retrieved successfully", material));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get faculty portal summary statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse stats = materialService.getDashboardStats();
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Portal statistics retrieved", stats));
    }
}
