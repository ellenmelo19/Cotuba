package br.com.unipds.application;

import br.com.unipds.domain.Ebook;

/**
 * SPI segregada: hook executado após a geração completa do ebook (PDF/EPUB/HTML).
 * <p>
 * Descoberta via {@link java.util.ServiceLoader} e registro em
 * {@code META-INF/services/br.com.unipds.application.PluginAposGeracao}.
 */
public interface PluginAposGeracao {

    /**
     * @param ebook ebook já gerado
     */
    void aposGeracao(Ebook ebook);
}
