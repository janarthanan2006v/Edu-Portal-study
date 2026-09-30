package com.prince.materialportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialCreateRequest {

    @NotBlank(message = "Subject name is required.")
    private String subjectName;

    @NotBlank(message = "Department is required.")
    private String department;

    @NotBlank(message = "Course year is required.")
    private String courseYear;

    private String sem1;

    private String sem2;

    @Builder.Default
    private Boolean activeStatus = true;
}
