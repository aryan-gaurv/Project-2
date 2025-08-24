package com.example.authify.repository;

import com.example.authify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepostory extends JpaRepository<UserEntity,Long> {
   Optional< UserEntity> findByEmail(String email);
   Boolean existsByEmail(String email);
   //Optional<UserEntity>findByUserId(String email);
}
