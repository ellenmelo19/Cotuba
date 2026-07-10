package br.com.unipds;

import java.nio.file.Path;

/**
 * Capítulo já renderizado e válido: nasce completo, com título e HTML.
 */
public final class Capitulo {

    private final String titulo;
    private final String conteudoHtml;
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
