package br.com.unipds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class GeradorDeEbookService {

    private final CapituloRepository capituloRepository;
    private final MetadadosEbookRepository metadadosEbookRepository;
    private final RenderizadorDeMarkdown renderizadorDeMarkdown;
    private final GeradorDePdf geradorDePdf;
    private final GeradorDeEpub geradorDeEpub;

    public GeradorDeEbookService() {
        this(new CapituloRepository(),
                new MetadadosEbookRepository(),
                new RenderizadorDeMarkdown(),
                new GeradorDePdf(),
                new GeradorDeEpub());
    }

    public GeradorDeEbookService(CapituloRepository capituloRepository,
                                MetadadosEbookRepository metadadosEbookRepository,
                                RenderizadorDeMarkdown renderizadorDeMarkdown,
                                GeradorDePdf geradorDePdf,
                                GeradorDeEpub geradorDeEpub) {
        this.capituloRepository = capituloRepository;
        this.metadadosEbookRepository = metadadosEbookRepository;
        this.renderizadorDeMarkdown = renderizadorDeMarkdown;
        this.geradorDePdf = geradorDePdf;
        this.geradorDeEpub = geradorDeEpub;
    }

    public void gerar(ParametrosCotuba parametros) {
        limparArquivoDeSaida(parametros.getArquivoDeSaida());

        List<Capitulo> capitulos = capituloRepository.buscarPorDiretorio(parametros.getDiretorioDosMD());
        renderizadorDeMarkdown.renderizar(capitulos);

        MetadadosEbook metadados = metadadosEbookRepository.buscarPorDiretorio(parametros.getDiretorioDosMD());
        var ebook = new Ebook(
                parametros.getFormato(),
                parametros.getArquivoDeSaida(),
                capitulos,
                metadados.getTitulo(),
                metadados.getAutor());

        if (FormatoEbook.PDF.equals(ebook.getFormato())) {
            geradorDePdf.gerar(ebook);
        } else if (FormatoEbook.EPUB.equals(ebook.getFormato())) {
            geradorDeEpub.gerar(ebook);
        } else {
            throw new IllegalArgumentException("Formato do ebook inválido: " + ebook.getFormato());
        }
    }

    private void limparArquivoDeSaida(Path arquivoDeSaida) {
        try {
            if (Files.isDirectory(arquivoDeSaida)) {
                // deleta arquivos do diretório recursivamente
                Files.walk(arquivoDeSaida).sorted(Comparator.reverseOrder())
                        .map(Path::toFile).forEach(File::delete);
            } else {
                Files.deleteIfExists(arquivoDeSaida);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao limpar arquivo de saída: " + arquivoDeSaida.toAbsolutePath(), ex);
        }
    }
}
