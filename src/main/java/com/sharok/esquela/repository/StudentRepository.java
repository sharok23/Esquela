package com.sharok.esquela.repository;

import com.sharok.esquela.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {}
