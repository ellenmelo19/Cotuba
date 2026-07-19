package br.com.unipds.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum FormatoEbook {

    PDF("pdf", "book.pdf"),
    EPUB("epub", "book.epub"),
    HTML("html", "site");

    private final String extensao;
    private final String arquivoSaidaPadrao;

    FormatoEbook(String extensao, String arquivoSaidaPadrao) {
        this.extensao = extensao;
        this.arquivoSaidaPadrao = arquivoSaidaPadrao;
    }

    public String getExtensao() {
        return extensao;
    }

    public String getArquivoSaidaPadrao() {
        return arquivoSaidaPadrao;
    }

    public static FormatoEbook from(String valor) {
        String formatoNormalizado = valor.toLowerCase();

        for (FormatoEbook formato : values()) {
            if (formato.extensao.equals(formatoNormalizado)) {
                return formato;
            }
        }

        throw new IllegalArgumentException("Formato do ebook inválido: " + valor);
    }
}
