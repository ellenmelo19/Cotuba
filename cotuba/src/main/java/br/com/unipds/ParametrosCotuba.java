package br.com.unipds;

import java.nio.file.Path;

public class ParametrosCotuba {

    private Path diretorioDosMD;
    private FormatoEbook formato;
    private Path arquivoDeSaida;
    private boolean modoVerboso;

    public ParametrosCotuba(Path diretorioDosMD, FormatoEbook formato, Path arquivoDeSaida, boolean modoVerboso) {
        this.diretorioDosMD = diretorioDosMD;
        this.formato = formato;
        this.arquivoDeSaida = arquivoDeSaida;
        this.modoVerboso = modoVerboso;
    }

    public Path getDiretorioDosMD() {
        return diretorioDosMD;
    }

    public void setDiretorioDosMD(Path diretorioDosMD) {
        this.diretorioDosMD = diretorioDosMD;
    }

    public FormatoEbook getFormato() {
        return formato;
    }

    public void setFormato(FormatoEbook formato) {
        this.formato = formato;
    }

    public Path getArquivoDeSaida() {
        return arquivoDeSaida;
    }

    public void setArquivoDeSaida(Path arquivoDeSaida) {
        this.arquivoDeSaida = arquivoDeSaida;
    }

    public boolean isModoVerboso() {
        return modoVerboso;
    }

    public void setModoVerboso(boolean modoVerboso) {
        this.modoVerboso = modoVerboso;
    }
}
