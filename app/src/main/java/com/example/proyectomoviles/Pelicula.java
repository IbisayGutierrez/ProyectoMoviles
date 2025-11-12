package com.example.proyectomoviles;

public class Pelicula {
        private int codigo;
        private String titulo;
        private int duracion;
        private String genero;

        public Pelicula(int codigo, String titulo, int duracion, String genero) {
            this.codigo = codigo;
            this.titulo = titulo;
            this.duracion = duracion;
            this.genero = genero;
        }
        public int getCodigo() {
            return codigo;
        }
        public String getTitulo() {
            return titulo;
        }
        public int getDuracion() {
            return duracion;
        }
        public String getGenero() {
            return genero;
        }
}
