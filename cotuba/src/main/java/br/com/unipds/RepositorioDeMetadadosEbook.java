package br.com.unipds;

import java.nio.file.Path;

public interface RepositorioDeMetadadosEbook {

    MetadadosEbook buscarPorDiretorio(Path diretorioDoLivro);
}
