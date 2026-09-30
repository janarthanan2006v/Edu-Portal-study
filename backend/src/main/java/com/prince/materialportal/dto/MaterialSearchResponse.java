package com.prince.materialportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialSearchResponse {
    private String subjectName;
    private String department;
    private String courseYear;
    private String semester;
    private String driveLink;
}
