package com.sharok.esquela.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharok.esquela.contract.request.StudentRequest;
import com.sharok.esquela.contract.response.StudentResponse;
import com.sharok.esquela.exception.StudentNotFoundException;
import com.sharok.esquela.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private StudentService studentService;
    private StudentResponse expectedResponse;
    private StudentRequest studentRequest;

    @BeforeEach
    void setup() {
        studentRequest =
                new StudentRequest(
                        "John", "Jacob", "Doe", "2012/01/02", "Male", "Kumily", 123456789);
        expectedResponse =
                StudentResponse.builder()
                        .id(1L)
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender("Male")
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();
    }

    @Test
    void AddStudentSuccess() throws Exception {
        when(studentService.addStudent(any(StudentRequest.class))).thenReturn(expectedResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(studentRequest);
        mockMvc.perform(
                        post("/v1/student")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(studentService, times(1)).addStudent(any(StudentRequest.class));
    }

    @Test
    void getStudentByIdSuccess() throws Exception {
        Long studentId = 1L;
        when(studentService.getStudentById(studentId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/v1/student/{id}", studentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));

        verify(studentService, times(1)).getStudentById(studentId);
    }

    @Test
    void updateStudentByIdSuccess() throws Exception {
        Long studentId = 1L;
        when(studentService.updateStudentById(eq(studentId), any(StudentRequest.class)))
                .thenReturn(expectedResponse);
        String requestJson = new ObjectMapper().writeValueAsString(studentRequest);

        mockMvc.perform(
                        put("/v1/student/{id}", studentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));

        verify(studentService, times(1))
                .updateStudentById(eq(studentId), any(StudentRequest.class));
    }

    @Test
    void deleteStudentByIdSuccess() throws Exception {
        Long studentId = 1L;
        String expectedMessage = "Student deleted with Id: " + studentId;
        when(studentService.deleteStudentById(studentId)).thenReturn(expectedMessage);

        mockMvc.perform(
                        delete("/v1/student/{id}", studentId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        verify(studentService, times(1)).deleteStudentById(studentId);
    }

    @Test
    void deleteStudentByIdFailure() throws Exception {
        Long studentId = 2L;

        when(studentService.deleteStudentById(studentId))
                .thenThrow(new StudentNotFoundException(studentId));

        mockMvc.perform(
                        delete("/v1/student/{id}", studentId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).deleteStudentById(studentId);
    }
}
