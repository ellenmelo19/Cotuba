package br.com.unipds.estatisticas;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Conta a frequência de palavras.
 * <p>
 * Usa <strong>composição</strong> (tem um {@link TreeMap}) em vez de herança
 * ({@code extends TreeMap}), para não expor {@code put}, {@code remove},
 * {@code clear} etc. e violar o encapsulamento / LSP.
 * <p>
 * Implementa {@link Iterable} para permitir percorrer as contagens sem
 * vazar a coleção interna.
 */
public class ContadorDePalavras implements Iterable<Map.Entry<String, Integer>> {

    private final Map<String, Integer> palavras = new TreeMap<>();

    public void adicionarPalavra(String palavra) {
        Integer contagem = palavras.get(palavra);
        if (contagem == null) {
            palavras.put(palavra, 1);
        } else {
            palavras.put(palavra, contagem + 1);
        }
    }

    @Override
    public Iterator<Map.Entry<String, Integer>> iterator() {
        return Collections.unmodifiableSet(palavras.entrySet()).iterator();
    }
}
