module br.com.unipds.pdf {
    requires br.com.unipds.application;
    requires br.com.unipds.domain;
    requires weld.se.shaded;
    requires org.jmolecules.architecture.layered;

    // iText (módulos automáticos derivados do nome do JAR)
    requires kernel;
    requires io;
    requires layout;
    requires html2pdf;
    requires commons;
    requires styled.xml.parser;
    requires svg;
    requires forms;
    requires pdfa;
    requires bouncy.castle.connector;

    // Implementação de GeradorDePdf NÃO exportada
    opens br.com.unipds.pdf to weld.se.shaded;
}
