package com.project.sentimentapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

// ENTIDAD JPA (capa infrastructure). Mapea la tabla "rol" y su relación @ManyToMany con
// usuarios. Contraparte persistente del modelo de dominio Rol.
@Entity
@Data
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
@Table(name = "rol")
public class RolJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolID;

    @Column(name = "nombre_rol")
    private String nombreRol;

    @ManyToMany(mappedBy = "rol")
    private List<UsuarioJpaEntity> user;
}
