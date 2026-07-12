package br.com.unipds.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class MetadadosEbook {

    private String titulo;
    private String autor;

    public MetadadosEbook(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
