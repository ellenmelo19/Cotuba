package br.com.unipds;

import java.nio.file.Path;

public record CapituloHtml(Path arquivoMarkdown, String titulo, String html) {
}
