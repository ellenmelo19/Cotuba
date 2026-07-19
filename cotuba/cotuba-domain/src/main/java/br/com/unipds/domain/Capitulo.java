package br.com.unipds.domain;

import java.nio.file.Path;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Capítulo já renderizado e válido: nasce completo, com título e HTML.
 */
@Entity
public final class Capitulo {

    private final String titulo;
    private final String conteudoHtml;
    @Identity
    private final Path arquivoMarkdown;

    public Capitulo(String titulo, String conteudoHtml, Path arquivoMarkdown) {
        this.titulo = titulo;
        this.conteudoHtml = conteudoHtml;
        this.arquivoMarkdown = arquivoMarkdown;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public Path getArquivoMarkdown() {
        return arquivoMarkdown;
    }
}
