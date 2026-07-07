package br.com.unipds;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeradorDeEpub {

    private final RenderizadorDeMarkdown renderizadorDeMarkdown;

    public GeradorDeEpub(RenderizadorDeMarkdown renderizadorDeMarkdown) {
        this.renderizadorDeMarkdown = renderizadorDeMarkdown;
    }

    public void gerar(Path diretorioDosMD, Path arquivoDeSaida) {
        try {
            var epub = new Book();

            //TODO: definir título e autor para o livro
            epub.getMetadata().addTitle("Livro");
            epub.getMetadata().addAuthor(new Author("Autor"));

            boolean ehPrimeiroCapitulo = true;

            for (CapituloHtml capitulo : renderizadorDeMarkdown.renderizar(diretorioDosMD)) {
                String epubHtml = """
                          <html xmlns="http://www.w3.org/1999/xhtml">
                            <head>
                              <title>%s</title>
                            </head>
                            <body>
                              %s
                            </body>
                          </html>
                        """.formatted(capitulo.titulo(), capitulo.html());
                var chapter = new Resource(epubHtml.getBytes(StandardCharsets.UTF_8), MediatypeService.XHTML);
                epub.addSection(capitulo.titulo(), chapter);

                if (ehPrimeiroCapitulo) {
                    epub.getGuide().addReference(new GuideReference(chapter, "text", "Start Reading"));
                    ehPrimeiroCapitulo = false;
                }
            }

            var epubWriter = new EpubWriter();
            epubWriter.write(epub, Files.newOutputStream(arquivoDeSaida));
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao criar arquivo EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
        }
    }
}
