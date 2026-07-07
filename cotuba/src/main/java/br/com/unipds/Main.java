package br.com.unipds;

import org.apache.commons.cli.ParseException;

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

        ParametrosCotuba parametros;
        try {
            parametros = cli.getParametros();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }

        try {
            new GeradorDeEbookService().gerar(parametros);
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

    private void imprimirAjuda() {
        try {
            new CotubaCli(new String[]{}).imprimirAjuda();
        } catch (ParseException ignored) {
            // Não deve ocorrer sem argumentos.
        }
    }
}
