package com.project.sentimentapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// ENTIDAD JPA (capa infrastructure). Mapea la tabla "categoria" y sus relaciones con
// usuario (dueño) y productos. Contraparte persistente del modelo de dominio Categoria.
@Entity
@Data
@NoArgsConstructor
@Table(name = "categoria")
public class CategoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoJpaEntity> productos = new ArrayList<>();

    public CategoriaJpaEntity(String nombreCategoria, String descripcion, UsuarioJpaEntity usuario) {
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.usuario = usuario;
    }
}
