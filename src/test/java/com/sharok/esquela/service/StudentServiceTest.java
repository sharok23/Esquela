package com.sharok.esquela.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sharok.esquela.constant.Gender;
import com.sharok.esquela.contract.request.StudentRequest;
import com.sharok.esquela.contract.response.StudentResponse;
import com.sharok.esquela.exception.StudentNotFoundException;
import com.sharok.esquela.kafka.StudentKafkaProducer;
import com.sharok.esquela.model.Student;
import com.sharok.esquela.repository.StudentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    private StudentService studentService;
    @Mock private StudentRepository studentRepository;
    @Mock private ModelMapper modelMapper;
    private StudentRequest request;
    private StudentResponse expectedResponse;
    private Student student;
    @Mock private StudentKafkaProducer studentKafkaProducer;

    @BeforeEach
    void setup() {
        studentService = new StudentService(studentRepository, modelMapper, studentKafkaProducer);
        request =
                new StudentRequest(
                        "John", "Jacob", "Doe", "2012/01/02", "Male", "Kumily", 123456789);
        expectedResponse =
                StudentResponse.builder()
                        .id(1L)
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender("Male")
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();
        student =
                Student.builder()
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender(Gender.Male)
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();
    }

    @Test
    void testAddStudent() {

        Student student =
                Student.builder()
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender(Gender.Male)
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();
        Student savedStudent =
                Student.builder()
                        .id(1L)
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender(Gender.Male)
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();

        when(modelMapper.map(request, Student.class)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(modelMapper.map(savedStudent, StudentResponse.class)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.addStudent(request);
        assertEquals(expectedResponse, actualResponse);
        verify(modelMapper).map(request, Student.class);
        verify(studentRepository).save(any(Student.class));
        verify(modelMapper).map(savedStudent, StudentResponse.class);
    }

    @Test
    void getStudentByIdSuccess() {
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(modelMapper.map(student, StudentResponse.class)).thenReturn(expectedResponse);
        StudentResponse actualResponse = studentService.getStudentById(id);
        assertEquals(expectedResponse, actualResponse);
        verify(studentRepository).findById(id);
        verify(modelMapper).map(student, StudentResponse.class);
    }

    @Test
    void getStudentByIdNotFound() {
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception =
                assertThrows(
                        StudentNotFoundException.class, () -> studentService.getStudentById(id));
        assertEquals("Entity Student not found with Id: " + 1, exception.getMessage());
        verify(studentRepository).findById(id);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldUpdateStudentByIdSuccessfully() {
        Long id = 1L;
        Student updatedStudent =
                Student.builder()
                        .id(id)
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender(Gender.Male)
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();

        Student savedStudent =
                Student.builder()
                        .id(id)
                        .firstName("John")
                        .middleName("Jacob")
                        .lastName("Doe")
                        .dob("2012/01/02")
                        .gender(Gender.Male)
                        .address("Kumily")
                        .phoneNumber(123456789)
                        .build();

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(modelMapper.map(savedStudent, StudentResponse.class)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.updateStudentById(id, request);

        assertEquals(expectedResponse, actualResponse);

        verify(studentRepository).findById(id);
        verify(studentRepository).save(any(Student.class));
        verify(modelMapper).map(savedStudent, StudentResponse.class);
    }

    @Test
    void testDeleteStudentById() {
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        String result = studentService.deleteStudentById(studentId);

        verify(studentRepository).deleteById(studentId);
        assertEquals("Student deleted with Id: " + studentId, result);
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception =
                assertThrows(
                        StudentNotFoundException.class,
                        () -> studentService.updateStudentById(id, request));

        assertEquals("Entity Student not found with Id: 1", exception.getMessage());

        verify(studentRepository).findById(id);
        verifyNoMoreInteractions(studentRepository);
        verifyNoInteractions(modelMapper);
    }
}
