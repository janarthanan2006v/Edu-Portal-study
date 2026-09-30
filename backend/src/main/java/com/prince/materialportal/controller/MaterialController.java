package com.prince.materialportal.controller;

import com.prince.materialportal.dto.ApiResponse;
import com.prince.materialportal.dto.MaterialSearchResponse;
import com.prince.materialportal.entity.Material;
import com.prince.materialportal.service.MaterialService;
import com.prince.materialportal.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "Student Materials", description = "Endpoints for students and faculty to search and browse study materials")
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping("/search")
    @Operation(summary = "Search material by subject name and semester (sem1 / sem2)")
    public ResponseEntity<ApiResponse<MaterialSearchResponse>> search(
            @RequestParam String subjectName,
            @RequestParam String semester
    ) {
        MaterialSearchResponse response = materialService.searchMaterial(subjectName, semester);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Material found successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get list of all study materials")
    public ResponseEntity<ApiResponse<List<Material>>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Materials retrieved successfully", materials));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get material details by ID")
    public ResponseEntity<ApiResponse<Material>> getMaterialById(@PathVariable Long id) {
        Material material = materialService.getMaterialById(id);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Material retrieved successfully", material));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get active study materials filtered by department name")
    public ResponseEntity<ApiResponse<List<Material>>> getByDepartment(@PathVariable String department) {
        List<Material> materials = materialService.getMaterialsByDepartment(department);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Materials retrieved successfully", materials));
    }
}
