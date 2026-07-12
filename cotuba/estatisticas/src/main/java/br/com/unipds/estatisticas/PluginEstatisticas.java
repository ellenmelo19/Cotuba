package br.com.unipds.estatisticas;

import br.com.unipds.application.PluginAposGeracao;
import br.com.unipds.domain.Capitulo;
import br.com.unipds.domain.Ebook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PluginEstatisticas implements PluginAposGeracao {

    private static final int TOP_PALAVRAS = 15;

    @Override
    public void aposGeracao(Ebook ebook) {
        ContadorDePalavras contador = new ContadorDePalavras();
        int totalPalavras = 0;
        int totalCaracteres = 0;

        for (Capitulo capitulo : ebook.getCapitulos()) {
            String texto = extrairTexto(capitulo.getConteudoHtml());
            totalCaracteres += texto.length();

            for (String palavra : texto.toLowerCase().split("\\W+")) {
                if (palavra.isBlank()) {
                    continue;
                }
                totalPalavras++;
                contador.adicionarPalavra(palavra);
            }
        }

        System.out.println("=== Estatisticas do ebook ===");
        System.out.println("Titulo: " + ebook.getTitulo());
        System.out.println("Autor: " + ebook.getAutor());
        System.out.println("Capitulos: " + ebook.getCapitulos().size());
        System.out.println("Palavras: " + totalPalavras);
        System.out.println("Caracteres: " + totalCaracteres);
        System.out.println("Top " + TOP_PALAVRAS + " palavras mais frequentes:");

        List<Map.Entry<String, Integer>> entradas = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : contador) {
            entradas.add(entrada);
        }
        entradas.stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(TOP_PALAVRAS)
                .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue()));

        System.out.println("=============================");
    }

    private String extrairTexto(String html) {
        return html
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
