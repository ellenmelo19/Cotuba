module br.com.unipds.epub {
    requires br.com.unipds.application;
    requires br.com.unipds.domain;
    requires weld.se.shaded;
    requires org.jmolecules.architecture.layered;
    requires epublib.core;
    requires slf4j.api;

    // Implementação de GeradorDeEpub NÃO exportada
    opens br.com.unipds.epub to weld.se.shaded;
}
