package br.com.unipds;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.AreaBreakType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GeradorDePdf {

    private final RenderizadorDeMarkdown renderizadorDeMarkdown;

    public GeradorDePdf(RenderizadorDeMarkdown renderizadorDeMarkdown) {
        this.renderizadorDeMarkdown = renderizadorDeMarkdown;
    }

    public void gerar(Path diretorioDosMD, Path arquivoDeSaida) {
        try (var writer = new PdfWriter(Files.newOutputStream(arquivoDeSaida));
             var pdf = new PdfDocument(writer);
             var pdfDocument = new Document(pdf)) {

            //TODO: definir título e autor para o livro
            pdf.getDocumentInfo().setTitle("Livro");
            pdf.getDocumentInfo().setAuthor("Autor");

            List<CapituloHtml> capitulos = renderizadorDeMarkdown.renderizar(diretorioDosMD);

            for (int i = 0; i < capitulos.size(); i++) {
                adicionarCapitulo(pdf, pdfDocument, capitulos.get(i));

                if (i < capitulos.size() - 1) {
                    pdfDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar PDF: " + arquivoDeSaida.toAbsolutePath(), ex);
        }
    }

    private void adicionarCapitulo(PdfDocument pdf, Document pdfDocument, CapituloHtml capitulo) {
        try {
            List<IElement> elementos = HtmlConverter.convertToElements(capitulo.html());

            if (pdf.getNumberOfPages() == 0) {
                pdf.addNewPage();
            }

            PdfOutline rootOutline = pdf.getOutlines(false);
            if (rootOutline == null) {
                pdf.initializeOutlines();
                rootOutline = pdf.getOutlines(false);
            }

            PdfOutline chapterOutline = rootOutline.addOutline(capitulo.titulo());
            chapterOutline.addDestination(PdfExplicitDestination.createFit(pdf.getLastPage()));

            for (IElement element : elementos) {
                pdfDocument.add((IBlockElement) element);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao adicionar capítulo do arquivo " + capitulo.arquivoMarkdown(), ex);
        }
    }
}
