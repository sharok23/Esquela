package com.sharok.esquela.controller;

import com.sharok.esquela.contract.request.StudentRequest;
import com.sharok.esquela.contract.response.StudentResponse;
import com.sharok.esquela.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/student")
public class StudentController {
    private final StudentService studentService;

    @PreAuthorize("hasAnyAuthority('Teacher', 'Manager')")
    @PostMapping()
    public StudentResponse addStudent(@Valid @RequestBody StudentRequest request) {
        return studentService.addStudent(request);
    }

    @PreAuthorize("hasAnyAuthority('Teacher', 'Manager')")
    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PreAuthorize("hasAnyAuthority('Teacher', 'Manager','Student')")
    @PutMapping("/{id}")
    public StudentResponse updateStudentById(
            @PathVariable Long id, @RequestBody StudentRequest request) {
        return studentService.updateStudentById(id, request);
    }

    @PreAuthorize("hasAnyAuthority('Manager')")
    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable Long id) {
        return studentService.deleteStudentById(id);
    }
}
