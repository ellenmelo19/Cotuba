package br.com.unipds;

import java.nio.file.Path;
import java.util.List;

public interface RepositorioDeCapitulos {

    List<Capitulo> buscarPorDiretorio(Path diretorioDosMD);
}
