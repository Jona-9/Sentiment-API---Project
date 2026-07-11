package com.project.sentimentapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

// ENTIDAD JPA (capa infrastructure — detalle de persistencia).
// A DIFERENCIA del modelo de dominio Usuario, esta clase SÍ lleva anotaciones de JPA
// (@Entity, @Table, @Column, relaciones) y representa la tabla "usuarios". El
// UsuarioRepositoryAdapter traduce entre esta entidad y el POJO de dominio (patrón Adapter).
// Mantener ambas separadas es lo que respeta el DIP: el dominio no depende de jakarta.persistence.
@Entity
@Data
@Table(name = "usuarios")
@NoArgsConstructor
@ToString(exclude = {"rol", "sesiones"})
@EqualsAndHashCode(exclude = {"rol", "sesiones"})
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioID;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    // CORRECCIÓN: CascadeType.ALL incluye PERSIST y REMOVE sobre los roles,
    // lo que hace que Hibernate intente insertar roles ya existentes (detached)
    // y falle con "Detached entity passed to persist".
    // Con MERGE + REFRESH solo se sincronizan los datos — nunca se crean ni eliminan roles
    // al guardar un usuario.
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "User_rol",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_Id", referencedColumnName = "rol_id")
    )
    private List<RolJpaEntity> rol;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<SesionJpaEntity> sesiones;

    public UsuarioJpaEntity(String nombre, String apellido, String contrasena,
                            String correo, List<RolJpaEntity> rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }
}