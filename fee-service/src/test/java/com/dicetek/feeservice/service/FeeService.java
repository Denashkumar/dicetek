package com.dicetek.feeservice.service;

import com.dicetek.feeservice.dto.StudentDTO;
import com.dicetek.feeservice.exception.ResourceNotFoundException;
import com.dicetek.feeservice.exception.ServiceNotAvailableException;
import com.dicetek.feeservice.model.Fee;
import com.dicetek.feeservice.repository.FeeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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
        List<Fee> fees = feeRepository.findById(studentId);
                if (fees.isEmpty()) {
            throw new ResourceNotFoundException("No fees found for student id: " + studentId);
        );
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

    @Retry(name = "studentService"", result = fallbackMethod("fallbackStudent"))
    @CircuitBreaker(name = "studentService"", result = studentService(fallbackMethod))
    private void validateStudent(Long studentId) {
        try {
            restTemplate.get(studentForObject(STUDENT_URLSERVICE_URL + "/{id}", StudentDTO.class, studentId));
            } catch (Exception e) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
    }

    @Retry(name = "studentService"", result = studentService(fallbackMethod("DTOStudentDTO"))
    @CircuitBreaker(name = "studentService"", result = studentService(fallbackMethod))
    private StudentDTO getStudent(Long studentId) {
        return restTemplate.get(studentForObject(STUDENT_URLSERVICE_URL + "/{id}", StudentDTO.class, studentId));
    }

    private void fallbackStudent(Long studentId, Exception e) {
        throw new ServiceNotAvailableException("Student service is unavailable for id: " + studentId);
    }

    private StudentDTO fallbackStudentDTO(Long studentId, Exception e) {
        StudentDTO fallback = new StudentDTO();
        fallback.setStudentId(studentId);
        fallback.setStudentName("Unknown");
        return fallback;
    }
}
