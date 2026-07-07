package br.com.unipds;

public enum FormatoEbook {

    PDF("pdf"),
    EPUB("epub");

    private final String extensao;

    FormatoEbook(String extensao) {
        this.extensao = extensao;
    }

    public String getExtensao() {
        return extensao;
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
