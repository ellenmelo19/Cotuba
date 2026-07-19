package br.com.unipds.application;

/**
 * SPI segregada: hook executado logo após a renderização Markdown → HTML de cada capítulo.
 * <p>
 * Descoberta via {@link java.util.ServiceLoader}. No JPMS, o módulo consumidor declara
 * {@code uses} e o plugin declara {@code provides ... with ...} no {@code module-info.java}.
 */
public interface PluginAposRenderizacao {

    /**
     * @param html HTML original do capítulo
     * @return HTML processado (pode ser o mesmo ou uma transformação)
     */
    String aposRenderizacao(String html);
}
