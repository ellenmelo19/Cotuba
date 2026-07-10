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

        List<CapituloEmMarkdown> capitulosEmMarkdown =
                repositorioDeCapitulos.buscarPorDiretorio(parametros.getDiretorioDosMD());
        List<Capitulo> capitulos = renderizadorDeCapitulos.renderizar(capitulosEmMarkdown);

        MetadadosEbook metadados = repositorioDeMetadadosEbook.buscarPorDiretorio(parametros.getDiretorioDosMD());

        Ebook ebook = Ebook.builder()
                .comFormato(parametros.getFormato())
                .comArquivoDeSaida(parametros.getArquivoDeSaida())
                .comCapitulos(capitulos)
                .comTitulo(metadados.getTitulo())
                .comAutor(metadados.getAutor())
                .build();

        geradoresDeEbook
                .select(new FormatoGeradorFilter(ebook.getFormato()))
                .get()
                .gerar(ebook);
    }
}
