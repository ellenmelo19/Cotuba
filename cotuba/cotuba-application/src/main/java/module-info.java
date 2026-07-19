module br.com.unipds.application {
    requires transitive br.com.unipds.domain;
    requires weld.se.shaded;
    requires org.jmolecules.ddd;
    requires org.jmolecules.architecture.layered;

    exports br.com.unipds.application;

    // ServiceLoader: SPI de plugins pós-geração
    uses br.com.unipds.application.PluginAposGeracao;

    // Weld/CDI precisa de reflection nos beans
    opens br.com.unipds.application to weld.se.shaded;
}
