package br.com.unipds.cli;

import br.com.unipds.application.GeradorDeEbookService;
import br.com.unipds.application.ParametrosCotuba;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) {
        int exitCode = new Main().executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    public int executar(String[] args) {
        try (SeContainer container = SeContainerInitializer.newInstance().initialize()) {
            LeitorDeParametrosCli leitorCli = container.select(LeitorDeParametrosCli.class).get();
            ParametrosCotuba parametros = lerParametros(args, leitorCli);

            if (parametros == null) {
                return 1;
            }

            try {
                container.select(GeradorDeEbookService.class).get().gerar(parametros);
                System.out.println("Arquivo gerado com sucesso: " + parametros.getArquivoDeSaida());
                return 0;
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                if (parametros.isModoVerboso()) {
                    System.err.println();
                    ex.printStackTrace();
                }
                return 1;
            }
        }
    }

    private ParametrosCotuba lerParametros(String[] args, LeitorDeParametrosCli leitorCli) {
        try {
            return leitorCli.ler(args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            leitorCli.imprimirAjuda();
            return null;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
}
