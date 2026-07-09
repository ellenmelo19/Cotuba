package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GeradorDeEbookService {

    private final RepositorioDeCapitulos repositorioDeCapitulos;
    private final RepositorioDeMetadadosEbook repositorioDeMetadadosEbook;
    private final RenderizadorDeCapitulos renderizadorDeCapitulos;
    private final PreparadorArquivoDeSaida preparadorArquivoDeSaida;
    private final Instance<GeradorDeEbook> geradoresDeEbook;

    @Inject
    public GeradorDeEbookService(RepositorioDeCapitulos repositorioDeCapitulos,
                                RepositorioDeMetadadosEbook repositorioDeMetadadosEbook,
                                RenderizadorDeCapitulos renderizadorDeCapitulos,
                                PreparadorArquivoDeSaida preparadorArquivoDeSaida,
                                @Any Instance<GeradorDeEbook> geradoresDeEbook) {
        this.repositorioDeCapitulos = repositorioDeCapitulos;
        this.repositorioDeMetadadosEbook = repositorioDeMetadadosEbook;
        this.renderizadorDeCapitulos = renderizadorDeCapitulos;
        this.preparadorArquivoDeSaida = preparadorArquivoDeSaida;
        this.geradoresDeEbook = geradoresDeEbook;
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

        geradoresDeEbook
                .select(new FormatoGeradorFilter(ebook.getFormato()))
                .get()
                .gerar(ebook);
    }
}
