package br.com.unipds.adapters;

import br.com.unipds.application.GeradorDeEbook;
import br.com.unipds.application.FormatoGerador;
import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.FormatoEbook;
import br.com.unipds.domain.Ebook;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
@FormatoGerador(FormatoEbook.HTML)
public class GeradorDeSite implements GeradorDeEbook {

    @Override
    public void gerar(Ebook ebook) {
        Path diretorioDoSite = ebook.getArquivoDeSaida();

        try {
            Files.createDirectories(diretorioDoSite);
            criarPaginasDosCapitulos(ebook, diretorioDoSite);
            criarIndice(ebook, diretorioDoSite);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao gerar site: " + diretorioDoSite.toAbsolutePath(), ex);
        }
    }

    private void criarPaginasDosCapitulos(Ebook ebook, Path diretorioDoSite) throws IOException {
        for (int i = 0; i < ebook.getCapitulos().size(); i++) {
            Capitulo capitulo = ebook.getCapitulos().get(i);
            String nomeArquivo = nomeArquivoDoCapitulo(i);
            String html = """
                    <!doctype html>
                    <html lang="pt-BR">
                    <head>
                      <meta charset="utf-8">
                      <title>%s</title>
                    </head>
                    <body>
                      <nav><a href="index.html">Sumário</a></nav>
                      %s
                    </body>
                    </html>
                    """.formatted(escaparHtml(capitulo.getTitulo()), capitulo.getConteudoHtml());

            Files.writeString(diretorioDoSite.resolve(nomeArquivo), html, StandardCharsets.UTF_8);
        }
    }

    private void criarIndice(Ebook ebook, Path diretorioDoSite) throws IOException {
        var linksDosCapitulos = new StringBuilder();

        for (int i = 0; i < ebook.getCapitulos().size(); i++) {
            Capitulo capitulo = ebook.getCapitulos().get(i);
            linksDosCapitulos.append("      <li><a href=\"")
                    .append(nomeArquivoDoCapitulo(i))
                    .append("\">")
                    .append(escaparHtml(capitulo.getTitulo()))
                    .append("</a></li>\n");
        }

        String html = """
                <!doctype html>
                <html lang="pt-BR">
                <head>
                  <meta charset="utf-8">
                  <title>%s</title>
                </head>
                <body>
                  <h1>%s</h1>
                  <p>Autor: %s</p>
                  <h2>Sumário</h2>
                  <ol>
                %s  </ol>
                </body>
                </html>
                """.formatted(
                escaparHtml(ebook.getTitulo()),
                escaparHtml(ebook.getTitulo()),
                escaparHtml(ebook.getAutor()),
                linksDosCapitulos);

        Files.writeString(diretorioDoSite.resolve("index.html"), html, StandardCharsets.UTF_8);
    }

    private String nomeArquivoDoCapitulo(int indice) {
        return "capitulo-%02d.html".formatted(indice + 1);
    }

    private String escaparHtml(String texto) {
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
