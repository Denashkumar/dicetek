package com.dicetek.studentservice.controller;

import com.dicetek.studentservice.model.Student;
import com.dicetek.studentservice.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllStudents_ShouldReturnStudents() throws Exception {
        Student student = new Student();
        student.setStudentId(1L);
        student.setStudentName("John Doe");
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].studentName").value("John Doe"));
    }

    @Test
    void createStudent_ValidStudent_ShouldReturnCreated() throws Exception {
        Student student = new Student();
        student.setStudentId(1L);
        student.setStudentName("John Doe");
        student.setGrade("10");
        student.setMobileNumber("1234567890");
        student.setSchoolName("Springfield High");
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.studentName").value("John Doe"));
    }
}
