package br.com.unipds.adapters;

import br.com.unipds.application.PreparadorArquivoDeSaida;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@ApplicationScoped
public class PreparadorArquivoDeSaidaEmDisco implements PreparadorArquivoDeSaida {

    @Override
    public void preparar(Path arquivoDeSaida) {
        try {
            if (Files.isDirectory(arquivoDeSaida)) {
                // deleta arquivos do diretório recursivamente
                Files.walk(arquivoDeSaida).sorted(Comparator.reverseOrder())
                        .map(Path::toFile).forEach(File::delete);
            } else {
                Files.deleteIfExists(arquivoDeSaida);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao limpar arquivo de saída: " + arquivoDeSaida.toAbsolutePath(), ex);
        }
    }
}
