package refatoracao;

import java.io.IOException;
import java.util.Set;

public class Prontuario {

	private ProntuarioController prontuarioController;

    private String nomePaciente;
	private Internacao internacao;


	public Prontuario(String nomePaciente) {
		this.nomePaciente = nomePaciente;
		this.prontuarioController = new ProntuarioController(this);
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setInternacao(Internacao internacao) {
		this.internacao = internacao;
	}

	public Internacao getInternacao() {
		return this.internacao;
	}

	public void addProcedimento(Procedimento procedimento) {
		prontuarioController.addProcedimento(procedimento);
	}

	public Set<Procedimento> getProcedimentos() {
		return prontuarioController.getProcedimentos();
	}

    public String imprimaConta() {
		return prontuarioController.imprimaConta();
		
	}

	public Prontuario carregueProntuario(String arquivoCsv) throws IOException {
		return prontuarioController.carregueProntuario(arquivoCsv);
	}
}
