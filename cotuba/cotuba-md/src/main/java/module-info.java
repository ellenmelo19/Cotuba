module br.com.unipds.md {
    requires br.com.unipds.application;
    requires br.com.unipds.domain;
    requires org.commonmark;
    requires weld.se.shaded;
    requires org.jmolecules.ddd;
    requires org.jmolecules.architecture.layered;

    // Implementações NÃO são exportadas (encapsulamento JPMS)
    // ServiceLoader: SPI de plugins pós-renderização
    uses br.com.unipds.application.PluginAposRenderizacao;

    opens br.com.unipds.md to weld.se.shaded;
}
