package br.com.unipds.application;

import br.com.unipds.domain.CapituloEmMarkdown;
import org.jmolecules.ddd.annotation.Repository;
import java.nio.file.Path;
import java.util.List;

@Repository
public interface RepositorioDeCapitulos {

    List<CapituloEmMarkdown> buscarPorDiretorio(Path diretorioDosMD);
}
