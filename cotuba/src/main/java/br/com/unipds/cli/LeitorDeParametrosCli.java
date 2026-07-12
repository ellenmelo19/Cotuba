package br.com.unipds.cli;

import br.com.unipds.application.ParametrosCotuba;
import org.apache.commons.cli.ParseException;

public interface LeitorDeParametrosCli {

    ParametrosCotuba ler(String[] args) throws ParseException;

    void imprimirAjuda();
}
