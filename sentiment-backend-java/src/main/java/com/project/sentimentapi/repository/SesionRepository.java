package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.Sesion;
import com.project.sentimentapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Integer> {
    List<Sesion> findByUsuarioOrderBySesionIdDesc(User usuario);
}