package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
@NoArgsConstructor
@ToString(exclude = {"rol", "sesiones"})
@EqualsAndHashCode(exclude = {"rol", "sesiones"})
public class User {
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

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "User_rol",joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_Id",referencedColumnName = "rol_id"))
    private List<Rol> rol;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<Sesion> sesiones;

    public User(String nombre, String apellido, String contrasena, String correo, List<Rol> rol){
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }
}