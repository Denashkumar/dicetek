package com.example.feeservice.service;

import com.example.feeservice.dto.ReceiptDTO;
import com.example.feeservice.dto.StudentDTO;
import com.example.feeservice.exception.ResourceNotFoundException;
import com.example.feeservice.exception.ServiceUnavailableException;
import com.example.feeservice.model.Fee;
import com.example.feeservice.repository.FeeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeeService {
    private final FeeRepository feeRepository;
    private final RestTemplate restTemplate;
    private static final String STUDENT_SERVICE_URL = "http://localhost:8081/api/students";

    public FeeService(FeeRepository feeRepository, RestTemplate restTemplate) {
        this.feeRepository = feeRepository;
        this.restTemplate = restTemplate;
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public Fee createFee(Fee fee) {
        validateStudent(fee.getStudentId());
        fee.setStatus("PAID");
        fee.setPaymentDate(LocalDate.now());
        return feeRepository.save(fee);
    }

    public Fee getFeeById(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee not found with id: " + id));
    }

    public List<Fee> getFeesByStudentId(Long studentId) {
        validateStudent(studentId);
        List<Fee> fees = feeRepository.findByStudentId(studentId);
        if (fees.isEmpty()) {
            throw new ResourceNotFoundException("No fees found for student id: " + studentId);
        }
        return fees;
    }

    public ReceiptDTO getReceipt(Long feeId) {
        Fee fee = getFeeById(feeId);
        StudentDTO student = getStudent(fee.getStudentId());
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setFeeId(fee.getId());
        receipt.setStudentId(fee.getStudentId());
        receipt.setStudentName(student.getStudentName());
        receipt.setAmount(fee.getAmount());
        receipt.setPaymentDate(fee.getPaymentDate());
        receipt.setStatus(fee.getStatus());
        return receipt;
    }

    @Retry(name = "studentService", fallbackMethod = "fallbackStudent")
    @CircuitBreaker(name = "studentService", fallbackMethod = "fallbackStudent")
    private void validateStudent(Long studentId) {
        try {
            restTemplate.getForObject(STUDENT_SERVICE_URL + "/{id}", StudentDTO.class, studentId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
    }

    @Retry(name = "studentService", fallbackMethod = "fallbackStudentDTO")
    @CircuitBreaker(name = "studentService", fallbackMethod = "fallbackStudentDTO")
    private StudentDTO getStudent(Long studentId) {
        return restTemplate.getForObject(STUDENT_SERVICE_URL + "/{id}", StudentDTO.class, studentId);
    }

    private void fallbackStudent(Long studentId, Exception e) {
        throw new ServiceUnavailableException("Student service is unavailable for id: " + studentId);
    }

    private StudentDTO fallbackStudentDTO(Long studentId, Exception e) {
        StudentDTO fallback = new StudentDTO();
        fallback.setStudentId(studentId);
        fallback.setStudentName("Unknown");
        return fallback;
    }
}
