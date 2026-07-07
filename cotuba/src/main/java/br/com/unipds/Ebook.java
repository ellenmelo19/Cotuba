package br.com.unipds;

import java.nio.file.Path;
import java.util.List;

public class Ebook {

    private FormatoEbook formato;
    private Path arquivoDeSaida;
    private List<Capitulo> capitulos;
    private String titulo;
    private String autor;

    public Ebook(FormatoEbook formato, Path arquivoDeSaida, List<Capitulo> capitulos, String titulo, String autor) {
        this.formato = formato;
        this.arquivoDeSaida = arquivoDeSaida;
        this.capitulos = capitulos;
        this.titulo = titulo;
        this.autor = autor;
    }

    public FormatoEbook getFormato() {
        return formato;
    }

    public void setFormato(FormatoEbook formato) {
        this.formato = formato;
    }

    public Path getArquivoDeSaida() {
        return arquivoDeSaida;
    }

    public void setArquivoDeSaida(Path arquivoDeSaida) {
        this.arquivoDeSaida = arquivoDeSaida;
    }

    public List<Capitulo> getCapitulos() {
        return capitulos;
    }

    public void setCapitulos(List<Capitulo> capitulos) {
        this.capitulos = capitulos;
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
