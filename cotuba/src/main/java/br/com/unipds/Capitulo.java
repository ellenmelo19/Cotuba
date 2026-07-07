package br.com.unipds;

import java.nio.file.Path;

public class Capitulo {

    private String titulo;
    private String conteudoMarkdown;
    private Path arquivoMarkdown;
    private String conteudoHtml;

    public Capitulo(String titulo, String conteudoMarkdown, Path arquivoMarkdown, String conteudoHtml) {
        this.titulo = titulo;
        this.conteudoMarkdown = conteudoMarkdown;
        this.arquivoMarkdown = arquivoMarkdown;
        this.conteudoHtml = conteudoHtml;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudoMarkdown() {
        return conteudoMarkdown;
    }

    public void setConteudoMarkdown(String conteudoMarkdown) {
        this.conteudoMarkdown = conteudoMarkdown;
    }

    public Path getArquivoMarkdown() {
        return arquivoMarkdown;
    }

    public void setArquivoMarkdown(Path arquivoMarkdown) {
        this.arquivoMarkdown = arquivoMarkdown;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = conteudoHtml;
    }
}
