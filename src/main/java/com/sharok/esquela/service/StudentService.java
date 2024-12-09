package com.sharok.esquela.service;

import com.sharok.esquela.constant.Gender;
import com.sharok.esquela.contract.request.StudentRequest;
import com.sharok.esquela.contract.response.StudentResponse;
import com.sharok.esquela.exception.StudentNotFoundException;
import com.sharok.esquela.kafka.StudentKafkaProducer;
import com.sharok.esquela.model.Student;
import com.sharok.esquela.repository.StudentRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final StudentKafkaProducer studentKafkaProducer;

    public StudentResponse addStudent(StudentRequest request) {
        Student student = modelMapper.map(request, Student.class);
        student.setUploadedAt(LocalDateTime.now());
        Student savedStudent = studentRepository.save(student);
        studentKafkaProducer.produce("Student added", savedStudent);
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    public StudentResponse getStudentById(Long id) {
        Student student =
                studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        return modelMapper.map(student, StudentResponse.class);
    }

    public StudentResponse updateStudentById(Long id, StudentRequest request) {
        Student student =
                studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        Student updateStudent =
                Student.builder()
                        .id(id)
                        .firstName(
                                request.getFirstName() != null
                                        ? request.getFirstName()
                                        : student.getFirstName())
                        .middleName(
                                request.getMiddleName() != null
                                        ? request.getMiddleName()
                                        : student.getMiddleName())
                        .lastName(
                                request.getLastName() != null
                                        ? request.getLastName()
                                        : student.getLastName())
                        .dob(request.getDob() != null ? request.getDob() : student.getDob())
                        .gender(
                                Gender.valueOf(
                                        request.getGender() != null
                                                ? request.getGender()
                                                : String.valueOf(student.getGender())))
                        .address(
                                request.getAddress() != null
                                        ? request.getAddress()
                                        : student.getAddress())
                        .phoneNumber(
                                request.getPhoneNumber() != null
                                        ? request.getPhoneNumber()
                                        : student.getPhoneNumber())
                        .uploadedAt(student.getUploadedAt())
                        .build();
        Student savedStudent = studentRepository.save(updateStudent);
        studentKafkaProducer.produce("Student updated", savedStudent);
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    public String deleteStudentById(Long id) {
        Student student =
                studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
        studentKafkaProducer.produce("Student deleted", student);
        return "Student deleted with Id: " + id;
    }
}
