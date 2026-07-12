package br.com.unipds.tema;

import br.com.unipds.application.Plugin;
import br.com.unipds.domain.Ebook;

public class PluginTemaCss implements Plugin {

    @Override
    public String aposRenderizacao(String html) {
        return html + "\n<style>h1 { color: blue; }</style>";
    }

    @Override
    public void aposGeracao(Ebook ebook) {
    }
}
