package br.com.unipds.cli;

import br.com.unipds.application.ParametrosCotuba;
import br.com.unipds.domain.FormatoEbook;
import jakarta.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class CotubaCli implements LeitorDeParametrosCli {

    private final Options options;

    public CotubaCli() {
        options = new Options();

        var opcaoDeDiretorioDosMD = new Option("d", "dir", true,
                "Diretório que contém os arquivos md. Default: diretório atual.");
        options.addOption(opcaoDeDiretorioDosMD);

        var opcaoDeFormatoDoEbook = new Option("f", "format", true,
                "Formato de saída do ebook. Pode ser: pdf, epub ou html. Default: pdf");
        options.addOption(opcaoDeFormatoDoEbook);

        var opcaoDeArquivoDeSaida = new Option("o", "output", true,
                "Arquivo de saída do ebook. Default: book.pdf, book.epub ou site.");
        options.addOption(opcaoDeArquivoDeSaida);

        var opcaoModoVerboso = new Option("v", "verbose", false,
                "Habilita modo verboso.");
        options.addOption(opcaoModoVerboso);
    }

    @Override
    public ParametrosCotuba ler(String[] args) throws ParseException {
        CommandLineParser cmdParser = new DefaultParser();
        CommandLine commandLine = cmdParser.parse(options, args);
        FormatoEbook formato = getFormato(commandLine);
        return new ParametrosCotuba(
                getDiretorioDosMD(commandLine),
                formato,
                getArquivoDeSaida(commandLine, formato),
                isModoVerboso(commandLine));
    }

    private Path getDiretorioDosMD(CommandLine commandLine) {
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

    private FormatoEbook getFormato(CommandLine commandLine) {
        String nomeDoFormatoDoEbook = commandLine.getOptionValue("format");

        if (nomeDoFormatoDoEbook != null) {
            return FormatoEbook.from(nomeDoFormatoDoEbook);
        }

        return FormatoEbook.PDF;
    }

    private Path getArquivoDeSaida(CommandLine commandLine, FormatoEbook formato) {
        String nomeDoArquivoDeSaidaDoEbook = commandLine.getOptionValue("output");

        if (nomeDoArquivoDeSaidaDoEbook != null) {
            return Paths.get(nomeDoArquivoDeSaidaDoEbook);
        }

        return Paths.get(formato.getArquivoSaidaPadrao());
    }

    private boolean isModoVerboso(CommandLine commandLine) {
        return commandLine.hasOption("verbose");
    }

    @Override
    public void imprimirAjuda() {
        var ajuda = new HelpFormatter();
        ajuda.printHelp("cotuba", options);
    }
}
