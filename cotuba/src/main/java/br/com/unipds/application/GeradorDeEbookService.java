package br.com.unipds.application;

import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.CapituloEmMarkdown;
import br.com.unipds.domain.Ebook;
import br.com.unipds.domain.MetadadosEbook;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jmolecules.ddd.annotation.Service;

import java.util.List;
import java.util.ServiceLoader;

@ApplicationScoped
@Service
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

        for (Plugin plugin : ServiceLoader.load(Plugin.class)) {
            plugin.aposGeracao(ebook);
        }
    }
}
