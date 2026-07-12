package br.com.unipds.adapters;

import br.com.unipds.application.RepositorioDeCapitulos;
import br.com.unipds.domain.CapituloEmMarkdown;
import jakarta.enterprise.context.ApplicationScoped;
import org.jmolecules.ddd.annotation.Repository;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
@Repository
public class CapituloRepository implements RepositorioDeCapitulos {

    @Override
    public List<CapituloEmMarkdown> buscarPorDiretorio(Path diretorioDosMD) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.md");

        try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
            List<Path> arquivosMD = streamMDs
                    .filter(arquivo -> matcher.matches(arquivo.getFileName()))
                    .sorted()
                    .toList();

            if (arquivosMD.isEmpty()) {
                throw new IllegalStateException("Não foram encontrados capítulos (arquivos .md) no diretório: "
                        + diretorioDosMD.toAbsolutePath());
            }

            return arquivosMD.stream()
                    .map(this::lerCapituloEmMarkdown)
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(), ex);
        }
    }

    private CapituloEmMarkdown lerCapituloEmMarkdown(Path arquivoMD) {
        try {
            String conteudoMarkdown = removerBom(Files.readString(arquivoMD));
            return new CapituloEmMarkdown(arquivoMD, conteudoMarkdown);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao ler arquivo markdown " + arquivoMD, ex);
        }
    }

    private String removerBom(String conteudo) {
        if (conteudo.startsWith("\uFEFF")) {
            return conteudo.substring(1);
        }

        return conteudo;
    }
}
