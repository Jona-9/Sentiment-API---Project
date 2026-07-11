package com.project.sentimentapi.domain.model;

// MODELO DE DOMINIO (Clean Architecture — capa domain).
// POJO puro: no tiene @Entity ni ninguna anotación de JPA/Spring. Representa el
// concepto de negocio "Usuario" independientemente de cómo se persista.
// Principio DIP: el dominio NO depende de la base de datos; la traducción a la
// entidad JPA (UsuarioJpaEntity) ocurre en un adapter de infrastructure.
// Nota de seguridad: solo guarda passwordHash (BCrypt), nunca la contraseña en claro.
public class Usuario {

    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String passwordHash;

    public Usuario() {}

    public Usuario(Integer id, String nombre, String apellido, String email, String passwordHash) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Usuario(String nombre, String apellido, String email, String passwordHash) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}