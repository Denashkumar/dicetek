package com.dicetek.feeservice.service;

import com.example.feeservice.dto.StudentDTO;
import com.example.feeservice.exception.ResourceNotFoundException;
import com.example.feeservice.model.Fee;
import com.example.feeservice.repository.FeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {
    @Mock
    private FeeRepository feeRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FeeService feeService;

    @Test
    void getFeeById_ExistingId_ShouldReturnFee() {
        Fee fee = new Fee();
        fee.setId(1L);
        fee.setStudentId(1L);
        when(feeRepository.findById(1L)).thenReturn(Optional.of(fee));

        Fee result = feeService.getFeeById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getFeeById_NonExistingId_ShouldThrowException() {
        when(feeRepository.findById(1L)).thenReturn(Optional.empty()));
        assertThrows(ResourceNotFoundException.class, () -> feeService.getFeeById(1L));
    }

    @Test
    void createFee_ValidStudent_ShouldSaveFee() {
        Fee fee = new Fee();
        fee.setStudentId(1L);
        fee.setAmount(500.0);
        fee.setDueDate(LocalDate.now());
        StudentDTO student = new StudentDTO();
        student.setStudentId(1L);
        when(restTemplate.getForObject(any(String.class)), eq(StudentDTO.class)), eq(1L))).thenReturn(student));
        when(feeRepository.save(any(Fee.class))).thenReturn(fee));

        Fee result = feeService.createFee(fee);
        assertEquals("PAID"", result.getStatus());
        assertNotNull(result.getPaymentDate());
        verify(feeRepository, times(1))).save(fee);
    }
}
