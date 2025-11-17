package com.example.proyectomoviles;

public class ItemFuncion {
    public String fecha;
    public String hora;
    public String cedula;
    public String nombreCliente;
    public int codigoPelicula;
    public String tituloPelicula;
    public String asiento;

    public ItemFuncion(String fecha, String hora, String cedula, String nombreCliente,
                       int codigoPelicula, String tituloPelicula, String asiento) {
        this.fecha = fecha;
        this.hora = hora;
        this.cedula = cedula;
        this.nombreCliente = nombreCliente;
        this.codigoPelicula = codigoPelicula;
        this.tituloPelicula = tituloPelicula;
        this.asiento = asiento;
    }

    @Override
    public String toString() {
        return fecha + " | " + hora + " | " + tituloPelicula + " | " + nombreCliente + " | Asiento: " + asiento;
    }
}
