package com.example.crud;

public class Persona {
    private String Id;
    private String nombre;
    private String apellido;
    private String genero;
    private String fecha;
    private String img;



    public Persona() {
    }

    public Persona(String id, String nombre, String apellido, String genero, String fecha, String img) {
        Id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.genero = genero;
        this.fecha = fecha;
        this.img = img;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
