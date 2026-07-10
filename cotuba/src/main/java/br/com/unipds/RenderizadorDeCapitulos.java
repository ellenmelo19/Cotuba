package br.com.unipds;

import java.util.List;

public interface RenderizadorDeCapitulos {

    List<Capitulo> renderizar(List<CapituloEmMarkdown> capitulosEmMarkdown);

    Capitulo renderizar(CapituloEmMarkdown capituloEmMarkdown);
}
