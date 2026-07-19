package br.com.unipds.application;

import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.CapituloEmMarkdown;
import java.util.List;

public interface RenderizadorDeCapitulos {

    List<Capitulo> renderizar(List<CapituloEmMarkdown> capitulosEmMarkdown);

    Capitulo renderizar(CapituloEmMarkdown capituloEmMarkdown);
}
