package com.sharok.esquela.exception;

import com.sharok.esquela.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends EntityNotFoundException {
    public StudentNotFoundException(Long id) {
        super(Student.class.getSimpleName(), id);
    }
}
