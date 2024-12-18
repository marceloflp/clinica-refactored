package refatoracao;

import java.io.IOException;

public interface ProntuarioRepository {
    
    public String imprimaConta();

    public Prontuario carregueProntuario(String arquivoCsv) throws IOException;

    public String salveProntuario() throws IOException;
}
