package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByCorreo(String correo);
    Optional<User> findByResetToken(String resetToken);
}