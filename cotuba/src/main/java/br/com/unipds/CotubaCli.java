package br.com.unipds;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CotubaCli {

    private final Options options;
    private final CommandLine commandLine;

    public CotubaCli(String[] args) throws ParseException {
        options = new Options();

        var opcaoDeDiretorioDosMD = new Option("d", "dir", true,
                "Diretório que contém os arquivos md. Default: diretório atual.");
        options.addOption(opcaoDeDiretorioDosMD);

        var opcaoDeFormatoDoEbook = new Option("f", "format", true,
                "Formato de saída do ebook. Pode ser: pdf ou epub. Default: pdf");
        options.addOption(opcaoDeFormatoDoEbook);

        var opcaoDeArquivoDeSaida = new Option("o", "output", true,
                "Arquivo de saída do ebook. Default: book.{formato}.");
        options.addOption(opcaoDeArquivoDeSaida);

        var opcaoModoVerboso = new Option("v", "verbose", false,
                "Habilita modo verboso.");
        options.addOption(opcaoModoVerboso);

        CommandLineParser cmdParser = new DefaultParser();
        commandLine = cmdParser.parse(options, args);
    }

    public ParametrosCotuba getParametros() {
        FormatoEbook formato = getFormato();
        return new ParametrosCotuba(getDiretorioDosMD(), formato, getArquivoDeSaida(formato), isModoVerboso());
    }

    private Path getDiretorioDosMD() {
        String nomeDoDiretorioDosMD = commandLine.getOptionValue("dir");

        if (nomeDoDiretorioDosMD != null) {
            Path diretorioDosMD = Paths.get(nomeDoDiretorioDosMD);
            if (!Files.isDirectory(diretorioDosMD)) {
                throw new IllegalArgumentException(nomeDoDiretorioDosMD + " não é um diretório.");
            }
            return diretorioDosMD;
        }

        return Paths.get("");
    }

    private FormatoEbook getFormato() {
        String nomeDoFormatoDoEbook = commandLine.getOptionValue("format");

        if (nomeDoFormatoDoEbook != null) {
            return FormatoEbook.from(nomeDoFormatoDoEbook);
        }

        return FormatoEbook.PDF;
    }

    private Path getArquivoDeSaida(FormatoEbook formato) {
        String nomeDoArquivoDeSaidaDoEbook = commandLine.getOptionValue("output");

        if (nomeDoArquivoDeSaidaDoEbook != null) {
            return Paths.get(nomeDoArquivoDeSaidaDoEbook);
        }

        return Paths.get("book." + formato.getExtensao());
    }

    private boolean isModoVerboso() {
        return commandLine.hasOption("verbose");
    }

    public void imprimirAjuda() {
        var ajuda = new HelpFormatter();
        ajuda.printHelp("cotuba", options);
    }
}
