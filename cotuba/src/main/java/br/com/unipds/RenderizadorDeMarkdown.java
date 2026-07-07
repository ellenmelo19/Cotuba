package br.com.unipds;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

public class RenderizadorDeMarkdown {

    public List<CapituloHtml> renderizar(Path diretorioDosMD) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");

        try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
            List<Path> arquivosMD = streamMDs
                    .filter(matcher::matches)
                    .sorted()
                    .toList();

            if (arquivosMD.isEmpty()) {
                throw new IllegalStateException("Não foram encontrados capítulos (arquivos .md) no diretório: "
                        + diretorioDosMD.toAbsolutePath());
            }

            return arquivosMD.stream()
                    .map(this::renderizarArquivo)
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(), ex);
        }
    }

    private CapituloHtml renderizarArquivo(Path arquivoMD) {
        Node document = parsearMarkdown(arquivoMD);
        String titulo = encontrarTitulo(document);

        try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = renderer.render(document);
            return new CapituloHtml(arquivoMD, titulo, html);
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
        }
    }

    private Node parsearMarkdown(Path arquivoMD) {
        Parser parser = Parser.builder().build();

        try {
            return parser.parseReader(Files.newBufferedReader(arquivoMD));
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
        }
    }

    private String encontrarTitulo(Node document) {
        var visitor = new TituloDoCapituloVisitor();
        document.accept(visitor);
        return visitor.getTitulo();
    }

    private static class TituloDoCapituloVisitor extends AbstractVisitor {

        private String titulo = "Capítulo";

        @Override
        public void visit(Heading heading) {
            if (heading.getLevel() == 1 && heading.getFirstChild() instanceof Text text) {
                titulo = text.getLiteral();
                return;
            }

            visitChildren(heading);
        }

        public String getTitulo() {
            return titulo;
        }
    }
}
