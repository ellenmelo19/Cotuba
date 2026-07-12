package br.com.unipds.adapters;

import br.com.unipds.application.RepositorioDeMetadadosEbook;
import br.com.unipds.domain.MetadadosEbook;
import jakarta.enterprise.context.ApplicationScoped;
import org.jmolecules.ddd.annotation.Repository;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@ApplicationScoped
@Repository
public class MetadadosEbookRepository implements RepositorioDeMetadadosEbook {

    private static final String NOME_ARQUIVO = "ebook.properties";
    private static final String TITULO_PADRAO = "Livro";
    private static final String AUTOR_PADRAO = "Autor";

    @Override
    public MetadadosEbook buscarPorDiretorio(Path diretorioDoLivro) {
        Path arquivoDeMetadados = diretorioDoLivro.resolve(NOME_ARQUIVO);

        if (!Files.isRegularFile(arquivoDeMetadados)) {
            return new MetadadosEbook(TITULO_PADRAO, AUTOR_PADRAO);
        }

        var properties = new Properties();
        try (StringReader reader = new StringReader(removerBom(Files.readString(arquivoDeMetadados, StandardCharsets.UTF_8)))) {
            properties.load(reader);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao ler metadados do ebook em " + arquivoDeMetadados.toAbsolutePath(), ex);
        }

        String titulo = properties.getProperty("titulo", properties.getProperty("title", TITULO_PADRAO));
        String autor = properties.getProperty("autor", properties.getProperty("author", AUTOR_PADRAO));
        return new MetadadosEbook(titulo, autor);
    }

    private String removerBom(String conteudo) {
        if (conteudo.startsWith("\uFEFF")) {
            return conteudo.substring(1);
        }

        return conteudo;
    }
}
