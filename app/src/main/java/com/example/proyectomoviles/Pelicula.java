package com.example.proyectomoviles;

public class Pelicula {

    private int codigo;
    private String titulo;
    private int duracion;
    private String genero;
    private byte[] imagen;
    private byte[] audio;
    private String latitud;
    private String longitud;

    public Pelicula(int codigo, String titulo, int duracion, String genero,
                    byte[] imagen, byte[] audio, String latitud, String longitud) {

        this.codigo = codigo;
        this.titulo = titulo;
        this.duracion = duracion;
        this.genero = genero;
        this.imagen = imagen;
        this.audio = audio;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getCodigo() { return codigo; }

    public String getTitulo() { return titulo; }
    public int getDuracion() { return duracion; }
    public String getGenero() { return genero; }

    public byte[] getImagen() { return imagen; }
    public byte[] getAudio() { return audio; }

    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }
}
