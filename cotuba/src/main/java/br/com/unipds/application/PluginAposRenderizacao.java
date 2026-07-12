package br.com.unipds.application;

/**
 * SPI segregada: hook executado logo após a renderização Markdown → HTML de cada capítulo.
 * <p>
 * Descoberta via {@link java.util.ServiceLoader} e registro em
 * {@code META-INF/services/br.com.unipds.application.PluginAposRenderizacao}.
 */
public interface PluginAposRenderizacao {

    /**
     * @param html HTML original do capítulo
     * @return HTML processado (pode ser o mesmo ou uma transformação)
     */
    String aposRenderizacao(String html);
}
