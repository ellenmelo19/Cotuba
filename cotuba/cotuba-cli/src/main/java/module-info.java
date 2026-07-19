module br.com.unipds.cli {
    requires br.com.unipds.application;
    requires br.com.unipds.domain;
    requires br.com.unipds.md;
    requires br.com.unipds.pdf;
    requires br.com.unipds.epub;
    requires br.com.unipds.html;
    requires org.apache.commons.cli;
    requires weld.se.shaded;
    requires org.jmolecules.architecture.layered;

    // Monta a aplicação; não exporta pacotes internos
    opens br.com.unipds.cli to weld.se.shaded;
}
