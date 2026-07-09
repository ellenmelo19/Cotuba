package br.com.unipds;

import jakarta.enterprise.util.AnnotationLiteral;

public class FormatoGeradorFilter extends AnnotationLiteral<FormatoGerador> implements FormatoGerador {

    private final FormatoEbook formato;

    public FormatoGeradorFilter(FormatoEbook formato) {
        this.formato = formato;
    }

    @Override
    public FormatoEbook value() {
        return formato;
    }
}
