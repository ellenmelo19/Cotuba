package br.com.unipds;

import java.nio.file.Path;
import java.util.List;

public interface RepositorioDeCapitulos {

    List<CapituloEmMarkdown> buscarPorDiretorio(Path diretorioDosMD);
}
