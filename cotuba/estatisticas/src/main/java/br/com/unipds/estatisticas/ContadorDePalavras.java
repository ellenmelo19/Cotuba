package br.com.unipds.estatisticas;

import java.util.TreeMap;

public class ContadorDePalavras extends TreeMap<String, Integer> {

    public void adicionarPalavra(String palavra) {
        Integer contagem = get(palavra);
        if (contagem == null) {
            put(palavra, 1);
        } else {
            put(palavra, contagem + 1);
        }
    }
}
