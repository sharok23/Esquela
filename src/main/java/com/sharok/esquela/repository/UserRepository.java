package com.sharok.esquela.repository;

import com.sharok.esquela.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {}
