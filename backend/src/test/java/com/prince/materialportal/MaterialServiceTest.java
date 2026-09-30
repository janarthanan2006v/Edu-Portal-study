package com.prince.materialportal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prince.materialportal.dto.LoginRequest;
import com.prince.materialportal.dto.MaterialCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MaterialServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getFacultyToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("psvpec.2025.staffs@gmail.com", "password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("token").asText();
    }

    private String getStudentToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("psvpec.2025.students@gmail.com", "password");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("token").asText();
    }

    @Test
    @DisplayName("Student should search and find study materials")
    void testStudentSearchMaterial() throws Exception {
        String token = getStudentToken();

        mockMvc.perform(get("/api/materials/search")
                        .header("Authorization", "Bearer " + token)
                        .param("subjectName", "Java Programming")
                        .param("semester", "sem1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectName").value("Java Programming"))
                .andExpect(jsonPath("$.data.driveLink").isNotEmpty());
    }

    @Test
    @DisplayName("Faculty should create and list materials")
    void testFacultyCrudMaterial() throws Exception {
        String token = getFacultyToken();

        MaterialCreateRequest createRequest = MaterialCreateRequest.builder()
                .subjectName("Compiler Design")
                .department("Computer Science & Engineering")
                .courseYear("3rd Year")
                .sem1("https://drive.google.com/test-sem1")
                .sem2("https://drive.google.com/test-sem2")
                .activeStatus(true)
                .build();

        mockMvc.perform(post("/api/faculty/materials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subjectName").value("Compiler Design"));

        mockMvc.perform(get("/api/faculty/materials")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
