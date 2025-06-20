package com.example.feeservice.controller;

import com.example.feeservice.model.Fee;
import com.example.feeservice.service.FeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeController.class)
class FeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeeService feeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllFees_ShouldReturnFees() throws Exception {
        Fee fee = new Fee();
        fee.setId(1L);
        fee.setStudentId(1L);
        fee.setAmount(500.0);
        when(feeService.getAllFees()).thenReturn(Arrays.asList(fee));

        mockMvc.perform(get("/api/fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(500.0));
    }

    @Test
    void createFee_ValidFee_ShouldReturnCreated() throws Exception {
        Fee fee = new Fee();
        fee.setId(1L);
        fee.setStudentId(1L);
        fee.setAmount(500.0);
        fee.setDueDate(LocalDate.now());
        fee.setStatus("PAID");
        when(feeService.createFee(any(Fee.class))).thenReturn(fee);

        mockMvc.perform(post("/api/fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.0));
    }
}
