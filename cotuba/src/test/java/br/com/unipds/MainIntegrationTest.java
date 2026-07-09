package br.com.unipds;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MainIntegrationTest {

    @TempDir
    Path diretorioDosMd;

    private Path arquivoMd;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() throws Exception {
        System.setErr(new PrintStream(errContent));

        arquivoMd = diretorioDosMd.resolve("01-introducao.md");
        Files.writeString(arquivoMd, "# Capítulo Teste\n\nEste é um conteúdo de um arquivo Markdown.");
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Deve gerar o arquivo PDF corretamente e conter o texto do Markdown.")
    void deveGerarPdfComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "pdf",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(arquivoSaida.toFile()))) {
            String textoDaPagina = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            assertThat(textoDaPagina)
                    .contains("Capítulo Teste")
                    .contains("Este é um conteúdo de um arquivo Markdown.");
        }
    }

    @Test
    @DisplayName("Deve usar metadados do arquivo ebook.properties ao gerar PDF")
    void deveUsarMetadadosDoEbookProperties() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");
        Files.writeString(diretorioDosMd.resolve("ebook.properties"), """
                titulo=Livro Personalizado
                autor=Autora Teste
                """);

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "pdf",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(arquivoSaida.toFile()))) {
            assertThat(pdfDoc.getDocumentInfo().getTitle()).isEqualTo("Livro Personalizado");
            assertThat(pdfDoc.getDocumentInfo().getAuthor()).isEqualTo("Autora Teste");
        }
    }

    @Test
    @DisplayName("Deve gerar o arquivo EPUB corretamente e conter o HTML renderizado")
    void deveGerarEpubComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.epub");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "epub",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        EpubReader epubReader = new EpubReader();
        Book epubLido = epubReader.readEpub(Files.newInputStream(arquivoSaida));

        byte[] dadosDoHtml = epubLido.getSpine().getResource(0).getData();
        String htmlDoCapitulo = new String(dadosDoHtml);

        assertThat(htmlDoCapitulo)
                .contains("<h1>Capítulo Teste</h1>")
                .contains("<p>Este é um conteúdo de um arquivo Markdown.</p>");
    }

    @Test
    @DisplayName("Deve gerar um site com index e uma página HTML por capítulo")
    void deveGerarSiteComSucesso() throws Exception {
        Path diretorioDeSaida = diretorioDosMd.resolve("site");
        Files.writeString(diretorioDosMd.resolve("ebook.properties"), """
                titulo=Livro Site
                autor=Autora Site
                """);

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "html",
                "-o", diretorioDeSaida.toString()
        });

        assertThat(exitCode).isEqualTo(0);
        assertThat(diretorioDeSaida.resolve("index.html")).exists().isRegularFile();
        assertThat(diretorioDeSaida.resolve("capitulo-01.html")).exists().isRegularFile();

        assertThat(Files.readString(diretorioDeSaida.resolve("index.html")))
                .contains("Livro Site")
                .contains("Autora Site")
                .contains("capitulo-01.html")
                .contains("Capítulo Teste");

        assertThat(Files.readString(diretorioDeSaida.resolve("capitulo-01.html")))
                .contains("<h1>Capítulo Teste</h1>")
                .contains("<p>Este é um conteúdo de um arquivo Markdown.</p>");
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso o formato seja inválido")
    void deveFalharEEncerrarQuandoFormatoEhInvalido() {
        Path arquivoSaida = diretorioDosMd.resolve("saida.mobi");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "mobi",
                "-o", arquivoSaida.toString()
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Formato do ebook inválido: mobi");
        assertThat(arquivoSaida).doesNotExist();
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso diretório não tenha arquivos .md")
    void deveFalharEEncerrarQuandoNaoHaArquivosMd() throws Exception {
        Files.deleteIfExists(arquivoMd);
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = new Main().executar(new String[]{
                "-d", diretorioDosMd.toString(),
                "-f", "pdf",
                "-o", arquivoSaida.toString(),
                "-v"
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Não foram encontrados capítulos");
    }

    @Test
    @DisplayName("Deve acionar a ajuda do CLI e encerrar em caso de argumento desconhecido")
    void deveAcionarAjudaEEncerrarAoPassarArgumentoInvalido() {
        int exitCode = new Main().executar(new String[]{
                "-x"
        });

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Unrecognized option: -x");
    }
}
