package com.example.chatsimple.modelo;

import java.util.Date;

public class Mensaje {
    private String emisor;
    private String texto;
    private Date fecha;

    public Mensaje() {} // Constructor vacío para Firestore

    public Mensaje(String emisor, String texto, Date fecha) {
        this.emisor = emisor;
        this.texto = texto;
        this.fecha = fecha;
    }

    public String getEmisor() { return emisor; }
    public String getTexto() { return texto; }
    public Date getFecha() { return fecha; }
}
