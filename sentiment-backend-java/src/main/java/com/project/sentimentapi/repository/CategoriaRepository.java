package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByUsuarioOrderByNombreCategoriaAsc(User usuario);

    List<Categoria> findByUsuario(User usuario);

    Optional<Categoria> findByNombreCategoriaAndUsuario(String nombreCategoria, User usuario);

    Optional<Categoria> findByNombreCategoriaIgnoreCaseAndUsuario(String nombreCategoria, User usuario);

    long countByUsuario(User usuario);
}
