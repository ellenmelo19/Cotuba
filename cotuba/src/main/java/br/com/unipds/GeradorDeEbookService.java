package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@ApplicationScoped
public class GeradorDeEbookService {

    private final RepositorioDeCapitulos repositorioDeCapitulos;
    private final RepositorioDeMetadadosEbook repositorioDeMetadadosEbook;
    private final RenderizadorDeCapitulos renderizadorDeCapitulos;
    private final PreparadorArquivoDeSaida preparadorArquivoDeSaida;
    private final GeradorDeEbook geradorDePdf;
    private final GeradorDeEbook geradorDeEpub;

    @Inject
    public GeradorDeEbookService(RepositorioDeCapitulos repositorioDeCapitulos,
                                RepositorioDeMetadadosEbook repositorioDeMetadadosEbook,
                                RenderizadorDeCapitulos renderizadorDeCapitulos,
                                PreparadorArquivoDeSaida preparadorArquivoDeSaida,
                                @Named("pdf") GeradorDeEbook geradorDePdf,
                                @Named("epub") GeradorDeEbook geradorDeEpub) {
        this.repositorioDeCapitulos = repositorioDeCapitulos;
        this.repositorioDeMetadadosEbook = repositorioDeMetadadosEbook;
        this.renderizadorDeCapitulos = renderizadorDeCapitulos;
        this.preparadorArquivoDeSaida = preparadorArquivoDeSaida;
        this.geradorDePdf = geradorDePdf;
        this.geradorDeEpub = geradorDeEpub;
    }

    public void gerar(ParametrosCotuba parametros) {
        preparadorArquivoDeSaida.preparar(parametros.getArquivoDeSaida());

        List<Capitulo> capitulos = repositorioDeCapitulos.buscarPorDiretorio(parametros.getDiretorioDosMD());
        renderizadorDeCapitulos.renderizar(capitulos);

        MetadadosEbook metadados = repositorioDeMetadadosEbook.buscarPorDiretorio(parametros.getDiretorioDosMD());
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
}
