package br.com.unipds.application;

import br.com.unipds.domain.Ebook;

/**
 * SPI (Service Provider Interface) de plugins do Cotuba.
 * <p>
 * Implementações externas são descobertas em tempo de execução pelo
 * {@link java.util.ServiceLoader}, a partir de registros em
 * {@code META-INF/services/br.com.unipds.application.Plugin}.
 */
public interface Plugin {

    /**
     * Hook executado logo após a renderização Markdown → HTML de cada capítulo.
     *
     * @param html HTML original do capítulo
     * @return HTML processado (pode ser o mesmo ou uma transformação)
     */
    String aposRenderizacao(String html);

    /**
     * Hook executado após a geração completa do ebook (PDF/EPUB/HTML).
     *
     * @param ebook ebook já gerado
     */
    void aposGeracao(Ebook ebook);
}
