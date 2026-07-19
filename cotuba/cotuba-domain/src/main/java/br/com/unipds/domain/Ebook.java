package br.com.unipds.domain;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Aggregate Root do ebook. Imutável: só existe quando todas as peças estão prontas.
 */
@AggregateRoot
public final class Ebook {

    private final FormatoEbook formato;
    @Identity
    private final Path arquivoDeSaida;
    private final List<Capitulo> capitulos;
    private final String titulo;
    private final String autor;

    private Ebook(FormatoEbook formato, Path arquivoDeSaida, List<Capitulo> capitulos, String titulo, String autor) {
        this.formato = Objects.requireNonNull(formato, "formato é obrigatório");
        this.arquivoDeSaida = Objects.requireNonNull(arquivoDeSaida, "arquivoDeSaida é obrigatório");
        this.capitulos = List.copyOf(Objects.requireNonNull(capitulos, "capitulos é obrigatório"));
        this.titulo = Objects.requireNonNull(titulo, "titulo é obrigatório");
        this.autor = Objects.requireNonNull(autor, "autor é obrigatório");
    }

    public static Builder builder() {
        return new Builder();
    }

    public FormatoEbook getFormato() {
        return formato;
    }

    public Path getArquivoDeSaida() {
        return arquivoDeSaida;
    }

    public List<Capitulo> getCapitulos() {
        return capitulos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public static final class Builder {

        private FormatoEbook formato;
        private Path arquivoDeSaida;
        private List<Capitulo> capitulos;
        private String titulo;
        private String autor;

        private Builder() {
        }

        public Builder comFormato(FormatoEbook formato) {
            this.formato = formato;
            return this;
        }

        public Builder comArquivoDeSaida(Path arquivoDeSaida) {
            this.arquivoDeSaida = arquivoDeSaida;
            return this;
        }

        public Builder comCapitulos(List<Capitulo> capitulos) {
            this.capitulos = capitulos;
            return this;
        }

        public Builder comTitulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder comAutor(String autor) {
            this.autor = autor;
            return this;
        }

        public Ebook build() {
            return new Ebook(formato, arquivoDeSaida, capitulos, titulo, autor);
        }
    }
}
