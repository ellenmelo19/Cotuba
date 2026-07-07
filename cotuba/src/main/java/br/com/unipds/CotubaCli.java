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

    public Path getDiretorioDosMD() {
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

    public String getFormato() {
        String nomeDoFormatoDoEbook = commandLine.getOptionValue("format");

        if (nomeDoFormatoDoEbook != null) {
            return nomeDoFormatoDoEbook.toLowerCase();
        }

        return "pdf";
    }

    public Path getArquivoDeSaida() {
        String nomeDoArquivoDeSaidaDoEbook = commandLine.getOptionValue("output");

        if (nomeDoArquivoDeSaidaDoEbook != null) {
            return Paths.get(nomeDoArquivoDeSaidaDoEbook);
        }

        return Paths.get("book." + getFormato());
    }

    public boolean isModoVerboso() {
        return commandLine.hasOption("verbose");
    }

    public void imprimirAjuda() {
        var ajuda = new HelpFormatter();
        ajuda.printHelp("cotuba", options);
    }
}
