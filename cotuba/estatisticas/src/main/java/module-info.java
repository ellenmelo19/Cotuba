module br.com.unipds.estatisticas {
    requires br.com.unipds.application;

    provides br.com.unipds.application.PluginAposGeracao
            with br.com.unipds.estatisticas.PluginEstatisticas;
}
