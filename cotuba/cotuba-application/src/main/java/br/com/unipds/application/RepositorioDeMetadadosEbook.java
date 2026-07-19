package br.com.unipds.application;

import br.com.unipds.domain.MetadadosEbook;
import java.nio.file.Path;

public interface RepositorioDeMetadadosEbook {

    MetadadosEbook buscarPorDiretorio(Path diretorioDoLivro);
}
