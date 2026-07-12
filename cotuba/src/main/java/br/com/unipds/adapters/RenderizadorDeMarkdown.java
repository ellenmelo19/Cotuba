package br.com.unipds.adapters;

import br.com.unipds.application.Plugin;
import br.com.unipds.application.RenderizadorDeCapitulos;
import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.CapituloEmMarkdown;
import jakarta.enterprise.context.ApplicationScoped;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;
import java.util.ServiceLoader;

@ApplicationScoped
public class RenderizadorDeMarkdown implements RenderizadorDeCapitulos {

    @Override
    public List<Capitulo> renderizar(List<CapituloEmMarkdown> capitulosEmMarkdown) {
        return capitulosEmMarkdown.stream()
                .map(this::renderizar)
                .toList();
    }

    @Override
    public Capitulo renderizar(CapituloEmMarkdown capituloEmMarkdown) {
        Node document = parsearMarkdown(capituloEmMarkdown);

        try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String titulo = encontrarTitulo(document);
            String html = renderer.render(document);

            // Capítulos são imutáveis: o HTML processado pelos plugins entra na criação do objeto
            for (Plugin plugin : ServiceLoader.load(Plugin.class)) {
                html = plugin.aposRenderizacao(html);
            }

            return new Capitulo(titulo, html, capituloEmMarkdown.arquivoMarkdown());
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Erro ao renderizar para HTML o arquivo " + capituloEmMarkdown.arquivoMarkdown(), ex);
        }
    }

    private Node parsearMarkdown(CapituloEmMarkdown capituloEmMarkdown) {
        Parser parser = Parser.builder().build();

        try {
            return parser.parse(capituloEmMarkdown.conteudoMarkdown());
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Erro ao fazer parse do arquivo " + capituloEmMarkdown.arquivoMarkdown(), ex);
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
