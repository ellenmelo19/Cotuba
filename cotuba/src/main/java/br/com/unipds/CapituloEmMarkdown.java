package br.com.unipds;

import java.nio.file.Path;

/**
 * Insumo bruto de um capítulo: o arquivo Markdown lido do disco e seu conteúdo,
 * ainda sem título nem HTML renderizado.
 */
public record CapituloEmMarkdown(Path arquivoMarkdown, String conteudoMarkdown) {
}
