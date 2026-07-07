package br.com.unipds;

import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        int exitCode = new Main().executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int executar(String[] args) {
        CotubaCli cli;

        try {
            cli = new CotubaCli(args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            imprimirAjuda();
            return 1;
        }

        boolean modoVerboso = cli.isModoVerboso();

        try {
            Path diretorioDosMD = cli.getDiretorioDosMD();
            String formato = cli.getFormato();
            Path arquivoDeSaida = cli.getArquivoDeSaida();

            limparArquivoDeSaida(arquivoDeSaida);

            var renderizadorDeMarkdown = new RenderizadorDeMarkdown();
            if ("pdf".equals(formato)) {
                new GeradorDePdf(renderizadorDeMarkdown).gerar(diretorioDosMD, arquivoDeSaida);
            } else if ("epub".equals(formato)) {
                new GeradorDeEpub(renderizadorDeMarkdown).gerar(diretorioDosMD, arquivoDeSaida);
            } else {
                throw new IllegalArgumentException("Formato do ebook inválido: " + formato);
            }

            System.out.println("Arquivo gerado com sucesso: " + arquivoDeSaida);
            return 0;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (modoVerboso) {
                System.err.println();
                ex.printStackTrace();
            }
            return 1;
        }
    }

    private void imprimirAjuda() {
        try {
            new CotubaCli(new String[]{}).imprimirAjuda();
        } catch (ParseException ignored) {
            // Não deve ocorrer sem argumentos.
        }
    }

    private void limparArquivoDeSaida(Path arquivoDeSaida) throws IOException {
        if (Files.isDirectory(arquivoDeSaida)) {
            // deleta arquivos do diretório recursivamente
            Files.walk(arquivoDeSaida).sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
        } else {
            Files.deleteIfExists(arquivoDeSaida);
        }
    }
}
