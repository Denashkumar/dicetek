package com.example.feeservice.controller;

import com.example.feeservice.dto.ReceiptDTO;
import com.example.feeservice.model.Fee;
import com.example.feeservice.service.FeeService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FeeController {
    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping("/fees")
    public List<EntityModel<Fee>> getAllFees() {
        List<Fee> fees = feeService.getAllFees();
        return fees.stream()
                .map(fee -> EntityModel.of(fee,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                                .getFeeById(fee.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                                .getAllFees()).withRel("all-fees")))
                .collect(Collectors.toList());
    }

    @PostMapping("/fees")
    public ResponseEntity<EntityModel<Fee>> createFee(@Valid @RequestBody Fee fee) {
        Fee savedFee = feeService.createFee(fee);
        EntityModel<Fee> resource = EntityModel.of(savedFee,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getFeeById(savedFee.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getReceipt(savedFee.getId())).withRel("receipt"));
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @GetMapping("/fees/{id}")
    public ResponseEntity<EntityModel<Fee>> getFeeById(@PathVariable Long id) {
        Fee fee = feeService.getFeeById(id);
        EntityModel<Fee> resource = EntityModel.of(fee,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getFeeById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getReceipt(id)).withRel("receipt"));
        return ResponseEntity.ok(resource);
    }

    @GetMapping("/fees/student/{studentId}")
    public ResponseEntity<List<EntityModel<Fee>>> getFeesByStudentId(@PathVariable Long studentId) {
        List<Fee> fees = feeService.getFeesByStudentId(studentId);
        List<EntityModel<Fee>> resources = fees.stream()
                .map(fee -> EntityModel.of(fee,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                                .getFeeById(fee.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                                .getReceipt(fee.getId())).withRel("receipt")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/receipts/{feeId}")
    public ResponseEntity<EntityModel<ReceiptDTO>> getReceipt(@PathVariable Long feeId) {
        ReceiptDTO receipt = feeService.getReceipt(feeId);
        EntityModel<ReceiptDTO> resource = EntityModel.of(receipt,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getReceipt(feeId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FeeController.class)
                        .getFeeById(feeId)).withRel("fee"));
        return ResponseEntity.ok(resource);
    }
}
