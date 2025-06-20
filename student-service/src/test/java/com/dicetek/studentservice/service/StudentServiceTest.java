package com.dicetek.studentservice.service;

import com.dicetek.studentservice.exception.StudentNotFoundException;
import com.dicetek.studentservice.model.Student;
import com.dicetek.studentservice.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getStudentById_ExistingId_ShouldReturnStudent() {
        Student student = new Student();
        student.setStudentId(1L);
        student.setStudentName("John Doe");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1L);
        assertEquals(1L, result.getStudentId());
        assertEquals("John Doe", result.getStudentName());
    }

    @Test
    void getStudentById_NonExistingId_ShouldThrowException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(1L));
    }

    @Test
    void createStudent_ShouldSaveAndReturnStudent() {
        Student student = new Student();
        student.setStudentId(1L);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.createStudent(student);
        assertEquals(1L, result.getStudentId());
        verify(studentRepository, times(1)).save(student);
    }
}
