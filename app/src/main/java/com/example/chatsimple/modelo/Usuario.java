package com.example.chatsimple.modelo;

public class Usuario {
    private String uid, nombre;
    public Usuario() {}
    public Usuario(String uid, String nombre) { this.uid = uid; this.nombre = nombre; }
    public String getUid() { return uid; }
    public String getNombre() { return nombre; }
}
