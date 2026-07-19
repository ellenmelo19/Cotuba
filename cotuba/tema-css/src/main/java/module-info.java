module br.com.unipds.tema {
    requires br.com.unipds.application;

    provides br.com.unipds.application.PluginAposRenderizacao
            with br.com.unipds.tema.PluginTemaCss;
}
