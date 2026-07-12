package br.com.unipds.tema;

import br.com.unipds.application.PluginAposRenderizacao;

public class PluginTemaCss implements PluginAposRenderizacao {

    @Override
    public String aposRenderizacao(String html) {
        return html + "\n<style>h1 { color: blue; }</style>";
    }
}
