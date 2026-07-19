package br.com.unipds.application;

import br.com.unipds.domain.Ebook;

/**
 * SPI segregada: hook executado após a geração completa do ebook (PDF/EPUB/HTML).
 * <p>
 * Descoberta via {@link java.util.ServiceLoader}. No JPMS, o módulo consumidor declara
 * {@code uses} e o plugin declara {@code provides ... with ...} no {@code module-info.java}.
 */
public interface PluginAposGeracao {

    /**
     * @param ebook ebook já gerado
     */
    void aposGeracao(Ebook ebook);
}
