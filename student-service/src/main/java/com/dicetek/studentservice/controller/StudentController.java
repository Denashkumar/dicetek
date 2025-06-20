package com.dicetek.studentservice.controller;

import com.dicetek.studentservice.model.Student;
import com.dicetek.studentservice.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<EntityModel<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return students.stream()
                .map(student -> EntityModel.of(student,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                                .getStudentById(student.getStudentId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                                .getAllStudents()).withRel("all-students")))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Student>> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.createStudent(student);
        EntityModel<Student> resource = EntityModel.of(savedStudent,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                        .getStudentById(savedStudent.getStudentId())).withSelfRel());
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Student>> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        EntityModel<Student> resource = EntityModel.of(student,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                        .getStudentById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                        .getAllStudents()).withRel("all-students"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Student>> updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        EntityModel<Student> resource = EntityModel.of(updatedStudent,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class)
                        .getStudentById(id)).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
