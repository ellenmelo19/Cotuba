package br.com.unipds;

import jakarta.enterprise.context.ApplicationScoped;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

@ApplicationScoped
public class RenderizadorDeMarkdown implements RenderizadorDeCapitulos {

    @Override
    public void renderizar(List<Capitulo> capitulos) {
        for (Capitulo capitulo : capitulos) {
            renderizar(capitulo);
        }
    }

    @Override
    public void renderizar(Capitulo capitulo) {
        Node document = parsearMarkdown(capitulo);

        try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            capitulo.setTitulo(encontrarTitulo(document));
            capitulo.setConteudoHtml(renderer.render(document));
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + capitulo.getArquivoMarkdown(), ex);
        }
    }

    private Node parsearMarkdown(Capitulo capitulo) {
        Parser parser = Parser.builder().build();

        try {
            return parser.parse(capitulo.getConteudoMarkdown());
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao fazer parse do arquivo " + capitulo.getArquivoMarkdown(), ex);
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
