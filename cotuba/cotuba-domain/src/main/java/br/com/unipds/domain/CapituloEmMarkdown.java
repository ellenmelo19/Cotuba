package br.com.unipds.domain;

import java.nio.file.Path;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Insumo bruto de um capítulo: o arquivo Markdown lido do disco e seu conteúdo,
 * ainda sem título nem HTML renderizado.
 */
@ValueObject
public record CapituloEmMarkdown(Path arquivoMarkdown, String conteudoMarkdown) {
}
