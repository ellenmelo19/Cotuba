package br.com.unipds;

import org.apache.commons.cli.ParseException;

public interface LeitorDeParametrosCli {

    ParametrosCotuba ler(String[] args) throws ParseException;

    void imprimirAjuda();
}
