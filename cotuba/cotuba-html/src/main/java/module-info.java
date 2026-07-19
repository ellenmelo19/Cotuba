module br.com.unipds.html {
    requires br.com.unipds.application;
    requires br.com.unipds.domain;
    requires weld.se.shaded;
    requires org.jmolecules.architecture.layered;

    // Implementação de GeradorDeSite NÃO exportada
    opens br.com.unipds.html to weld.se.shaded;
}
