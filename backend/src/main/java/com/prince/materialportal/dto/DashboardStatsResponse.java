package com.prince.materialportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalMaterials;
    private long activeMaterials;
    private long inactiveMaterials;
    private Map<String, Long> departmentCounts;
}
