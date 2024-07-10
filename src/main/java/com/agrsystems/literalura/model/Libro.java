package com.agrsystems.literalura.model;


import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

//Tabla libros
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(unique = true)
    private String titulo;
    @ManyToOne()
    private Autor autor;

    //@ElementCollection(fetch = FetchType.EAGER)
    private List<String> idiomas;
    private Integer numeroDeDescargas;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros, Autor autor) {
        this.titulo = datosLibros.titulo();
        this.autor = autor;
        this.idiomas = datosLibros.idiomas();
        this.numeroDeDescargas = datosLibros.numeroDeDescargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public Integer getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Integer numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    @Override
    public String toString() {
        return "-----------Libro ["+ id + "]------------"+ '\n' +
                " Titulo='" + titulo + '\n' +
                " Autor=" + autor.getNombre() + '\n' +
                " Idiomas=" + idiomas.get(0) + '\n' +
                " Numero De Descargas=" + numeroDeDescargas + '\n' +
                "-------------------------------------------------";
    }
}
