package com.example.proyectomoviles;

public class Cliente {
    private String cedula;
    private String nombre;
    private String telefono;
    private String correo;
    private String contrasena;

    public Cliente(String cedula, String nombre, String telefono, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.contrasena = null;
    }
    public String getCedula() {
        return cedula;
    }
    public String getNombre() {
        return nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getCorreo() {
        return correo;
    }
    public String getContrasena() {
        return contrasena;
    }

}
