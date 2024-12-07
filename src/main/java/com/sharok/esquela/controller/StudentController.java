package com.sharok.esquela.controller;

import com.sharok.esquela.contract.request.StudentRequest;
import com.sharok.esquela.contract.response.StudentResponse;
import com.sharok.esquela.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/student")
public class StudentController {
    private final StudentService studentService;

    @PostMapping()
    public StudentResponse addStudent(@Valid @RequestBody StudentRequest request) {
        return studentService.addStudent(request);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudentById(
            @PathVariable Long id, @RequestBody StudentRequest request) {
        return studentService.updateStudentById(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable Long id) {
        return studentService.deleteStudentById(id);
    }
}
