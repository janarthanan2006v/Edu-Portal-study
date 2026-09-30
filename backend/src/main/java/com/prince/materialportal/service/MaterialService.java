package com.prince.materialportal.service;

import com.prince.materialportal.dto.*;
import com.prince.materialportal.entity.Material;
import com.prince.materialportal.exception.InvalidRequestException;
import com.prince.materialportal.exception.ResourceNotFoundException;
import com.prince.materialportal.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional(readOnly = true)
    public MaterialSearchResponse searchMaterial(String subjectName, String semester) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            throw new InvalidRequestException("Subject name is required.");
        }
        if (semester == null || semester.trim().isEmpty()) {
            throw new InvalidRequestException("Semester is required.");
        }

        String semNormalized = semester.trim();
        if (!semNormalized.equalsIgnoreCase("sem1") && !semNormalized.equalsIgnoreCase("sem2")
                && !semNormalized.equalsIgnoreCase("semester 1") && !semNormalized.equalsIgnoreCase("semester 2")) {
            throw new InvalidRequestException("Invalid semester. Allowed values are sem1 and sem2.");
        }

        String searchKey = subjectName.trim();
        // Exact match first, fallback to contains match
        Material material = materialRepository.findBySubjectNameIgnoreCaseAndActiveStatusTrue(searchKey)
                .orElseGet(() -> {
                    List<Material> partialMatches = materialRepository.findBySubjectNameContainingIgnoreCaseAndActiveStatusTrue(searchKey);
                    if (!partialMatches.isEmpty()) {
                        return partialMatches.get(0);
                    }
                    throw new ResourceNotFoundException("Material not found for the subject: '" + searchKey + "'");
                });

        boolean isSem1 = semNormalized.equalsIgnoreCase("sem1") || semNormalized.equalsIgnoreCase("semester 1");
        String driveLink = isSem1 ? material.getSem1() : material.getSem2();
        String displaySemester = isSem1 ? "Semester 1 (Sem1)" : "Semester 2 (Sem2)";

        if (driveLink == null || driveLink.trim().isEmpty()) {
            throw new ResourceNotFoundException("Study material link is not currently available for " + displaySemester + " of " + material.getSubjectName() + ".");
        }

        return MaterialSearchResponse.builder()
                .subjectName(material.getSubjectName())
                .department(material.getDepartment())
                .courseYear(material.getCourseYear())
                .semester(isSem1 ? "Sem1" : "Sem2")
                .driveLink(driveLink.trim())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Material> getAllMaterials() {
        return materialRepository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study material not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Material> getMaterialsByDepartment(String department) {
        return materialRepository.findByDepartmentIgnoreCaseAndActiveStatusTrue(department);
    }

    @Transactional
    public Material createMaterial(MaterialCreateRequest request) {
        Material material = Material.builder()
                .subjectName(request.getSubjectName().trim())
                .department(request.getDepartment().trim())
                .courseYear(request.getCourseYear().trim())
                .sem1(request.getSem1() != null ? request.getSem1().trim() : null)
                .sem2(request.getSem2() != null ? request.getSem2().trim() : null)
                .activeStatus(request.getActiveStatus() != null ? request.getActiveStatus() : true)
                .build();

        return materialRepository.save(material);
    }

    @Transactional
    public Material updateMaterial(Long id, MaterialUpdateRequest request) {
        Material material = getMaterialById(id);

        material.setSubjectName(request.getSubjectName().trim());
        material.setDepartment(request.getDepartment().trim());
        material.setCourseYear(request.getCourseYear().trim());
        material.setSem1(request.getSem1() != null ? request.getSem1().trim() : null);
        material.setSem2(request.getSem2() != null ? request.getSem2().trim() : null);
        material.setActiveStatus(request.getActiveStatus() != null ? request.getActiveStatus() : true);

        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        Material material = getMaterialById(id);
        materialRepository.delete(material);
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        List<Material> all = materialRepository.findAll();
        long activeCount = all.stream().filter(m -> Boolean.TRUE.equals(m.getActiveStatus())).count();
        long inactiveCount = all.size() - activeCount;

        Map<String, Long> departmentCounts = new HashMap<>();
        for (Material m : all) {
            String dept = m.getDepartment() != null ? m.getDepartment() : "General";
            departmentCounts.put(dept, departmentCounts.getOrDefault(dept, 0L) + 1L);
        }

        return DashboardStatsResponse.builder()
                .totalMaterials(all.size())
                .activeMaterials(activeCount)
                .inactiveMaterials(inactiveCount)
                .departmentCounts(departmentCounts)
                .build();
    }
}
