package com.example.CRUD.backend.repository;

import com.example.CRUD.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
