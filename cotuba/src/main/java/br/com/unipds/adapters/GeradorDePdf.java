package br.com.unipds.adapters;

import br.com.unipds.application.GeradorDeEbook;
import br.com.unipds.application.FormatoGerador;
import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.FormatoEbook;
import br.com.unipds.domain.Ebook;
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
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Files;
import java.util.List;

@ApplicationScoped
@FormatoGerador(FormatoEbook.PDF)
public class GeradorDePdf implements GeradorDeEbook {

    @Override
    public void gerar(Ebook ebook) {
        try (var writer = new PdfWriter(Files.newOutputStream(ebook.getArquivoDeSaida()));
             var pdf = new PdfDocument(writer);
             var pdfDocument = new Document(pdf)) {

            pdf.getDocumentInfo().setTitle(ebook.getTitulo());
            pdf.getDocumentInfo().setAuthor(ebook.getAutor());

            for (int i = 0; i < ebook.getCapitulos().size(); i++) {
                adicionarCapitulo(pdf, pdfDocument, ebook.getCapitulos().get(i));

                if (i < ebook.getCapitulos().size() - 1) {
                    pdfDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar PDF: " + ebook.getArquivoDeSaida().toAbsolutePath(), ex);
        }
    }

    private void adicionarCapitulo(PdfDocument pdf, Document pdfDocument, Capitulo capitulo) {
        try {
            List<IElement> elementos = HtmlConverter.convertToElements(capitulo.getConteudoHtml());

            if (pdf.getNumberOfPages() == 0) {
                pdf.addNewPage();
            }

            PdfOutline rootOutline = pdf.getOutlines(false);
            if (rootOutline == null) {
                pdf.initializeOutlines();
                rootOutline = pdf.getOutlines(false);
            }

            PdfOutline chapterOutline = rootOutline.addOutline(capitulo.getTitulo());
            chapterOutline.addDestination(PdfExplicitDestination.createFit(pdf.getLastPage()));

            for (IElement element : elementos) {
                pdfDocument.add((IBlockElement) element);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao adicionar capítulo do arquivo " + capitulo.getArquivoMarkdown(), ex);
        }
    }
}
