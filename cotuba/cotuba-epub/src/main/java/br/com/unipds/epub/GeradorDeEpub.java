package br.com.unipds.epub;

import br.com.unipds.application.GeradorDeEbook;
import br.com.unipds.application.FormatoGerador;
import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.FormatoEbook;
import br.com.unipds.domain.Ebook;
import jakarta.enterprise.context.ApplicationScoped;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@ApplicationScoped
@FormatoGerador(FormatoEbook.EPUB)
public class GeradorDeEpub implements GeradorDeEbook {

    @Override
    public void gerar(Ebook ebook) {
        try {
            var epub = new Book();

            epub.getMetadata().addTitle(ebook.getTitulo());
            epub.getMetadata().addAuthor(new Author(ebook.getAutor()));

            boolean ehPrimeiroCapitulo = true;

            for (Capitulo capitulo : ebook.getCapitulos()) {
                String epubHtml = """
                          <html xmlns="http://www.w3.org/1999/xhtml">
                            <head>
                              <title>%s</title>
                            </head>
                            <body>
                              %s
                            </body>
                          </html>
                        """.formatted(capitulo.getTitulo(), capitulo.getConteudoHtml());
                var chapter = new Resource(epubHtml.getBytes(StandardCharsets.UTF_8), MediatypeService.XHTML);
                epub.addSection(capitulo.getTitulo(), chapter);

                if (ehPrimeiroCapitulo) {
                    epub.getGuide().addReference(new GuideReference(chapter, "text", "Start Reading"));
                    ehPrimeiroCapitulo = false;
                }
            }

            var epubWriter = new EpubWriter();
            epubWriter.write(epub, Files.newOutputStream(ebook.getArquivoDeSaida()));
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao criar arquivo EPUB: " + ebook.getArquivoDeSaida().toAbsolutePath(), ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar EPUB: " + ebook.getArquivoDeSaida().toAbsolutePath(), ex);
        }
    }
}
