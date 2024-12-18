package refatoracao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

public class ProntuarioController{

    private Prontuario prontuario;

	private Set<Procedimento> procedimentos = new HashSet<>();

    float valorTotalProcedimentos = 0.00f;
	int qtdeProcedimentosBasicos = 0;
	int qtdeProcedimentosComuns = 0;
	int qtdeProcedimentosAvancados = 0;

	public void addProcedimento(Procedimento procedimento) {
		this.procedimentos.add(procedimento);
	}

	public Set<Procedimento> getProcedimentos() {
		return this.procedimentos;
	}

    public ProntuarioController(Prontuario prontuario) {
        this.prontuario = prontuario;
    }

    public String imprimaConta() {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();

		StringBuilder conta = new StringBuilder("----------------------------------------------------------------------------------------------");

		float valorDiarias = 0.0f;

		// Contabilizar as diárias
		if (prontuario.getInternacao() != null) {
			switch (prontuario.getInternacao().getTipoLeito()) {
				case ENFERMARIA:
					if (prontuario.getInternacao().getQtdeDias() <= 3) {
						valorDiarias += 40.00 * prontuario.getInternacao().getQtdeDias(); // Internação Básica
					} else if (prontuario.getInternacao().getQtdeDias() <= 8) {
						valorDiarias += 35.00 * prontuario.getInternacao().getQtdeDias(); // Internação Média
					} else {
						valorDiarias += 30.00 * prontuario.getInternacao().getQtdeDias(); // Internação Grave
					}
					break;
				case APARTAMENTO:
					if (prontuario.getInternacao().getQtdeDias() <= 3) {
						valorDiarias += 100.00 * prontuario.getInternacao().getQtdeDias(); // Internação Básica
					} else if (prontuario.getInternacao().getQtdeDias() <= 8) {
						valorDiarias += 90.00 * prontuario.getInternacao().getQtdeDias();  // Internação Média
					} else {
						valorDiarias += 80.00 * prontuario.getInternacao().getQtdeDias();  // Internação Grave
					}
					break;
			}
		}

        for (Procedimento procedimento : prontuario.getProcedimentos()) {
			switch (procedimento.getTipoProcedimento()) {
				case BASICO:
					qtdeProcedimentosBasicos++;
					valorTotalProcedimentos += 50.00;
					break;

				case COMUM:
					qtdeProcedimentosComuns++;
					valorTotalProcedimentos += 150.00;
					break;

				case AVANCADO:
					qtdeProcedimentosAvancados++;
					valorTotalProcedimentos += 500.00;
					break;
			}
		}

		conta.append("\nA conta do(a) paciente ").append(prontuario.getNomePaciente())
			.append(" tem valor total de __ ").append(formatter.format(valorDiarias + valorTotalProcedimentos))
			.append(" __\n\nConforme os detalhes abaixo:");

   if (prontuario.getInternacao() != null) {
	   conta.append("\n\nValor Total Diárias:\t\t\t").append(formatter.format(valorDiarias));
	   conta.append("\n\t\t\t\t\t").append(prontuario.getInternacao().getQtdeDias())
			 .append(" diária").append(prontuario.getInternacao().getQtdeDias() > 1 ? "s" : "")
			 .append(" em ").append(prontuario.getInternacao().getTipoLeito() == TipoLeito.APARTAMENTO ? "apartamento" : "enfermaria");
   }

   if (prontuario.getProcedimentos().size() > 0) {
	   conta.append("\n\nValor Total Procedimentos:\t\t").append(formatter.format(valorTotalProcedimentos));

	   if (qtdeProcedimentosBasicos > 0) {
		   conta.append("\n\t\t\t\t\t").append(qtdeProcedimentosBasicos)
				.append(" procedimento").append(qtdeProcedimentosBasicos > 1 ? "s" : "")
				.append(" básico").append(qtdeProcedimentosBasicos > 1 ? "s" : "");
	   }

	   if (qtdeProcedimentosComuns > 0) {
		   conta.append("\n\t\t\t\t\t").append(qtdeProcedimentosComuns)
				.append(" procedimento").append(qtdeProcedimentosComuns > 1 ? "s" : "")
				.append(" comu").append(qtdeProcedimentosComuns > 1 ? "ns" : "m");
	   }

	   if (qtdeProcedimentosAvancados > 0) {
		   conta.append("\n\t\t\t\t\t").append(qtdeProcedimentosAvancados)
				.append(" procedimento").append(qtdeProcedimentosAvancados > 1 ? "s" : "")
				.append(" avançado").append(qtdeProcedimentosAvancados > 1 ? "s" : "");
	   }
   }

   // Finalizar a conta
    conta.append("\n\nVolte sempre, a casa é sua!")
		.append("\n----------------------------------------------------------------------------------------------");

    return conta.toString();
	}

    public Prontuario carregueProntuario(String arquivoCsv) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoCsv))) {
            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (cabecalho) {
                    cabecalho = false;
                    continue; // Ignora o cabeçalho
                }

                String[] dados = linha.split(",");
                if (dados.length < 4) {
                    continue; // Ignora linhas mal formatadas
                }

                // Nome do paciente
                String nomePaciente = dados[0].trim();
                prontuario.setNomePaciente(nomePaciente);

                // Tipo de Leito
                TipoLeito tipoLeito = null;
                if (dados[1] != null && !dados[1].trim().isEmpty()) {
                    try {
                        tipoLeito = TipoLeito.valueOf(dados[1].trim());
                    } catch (IllegalArgumentException e) {
                        // Tipo de leito inválido
                    }
                }

                // Quantidade de dias de internação
                int qtdeDiasInternacao = -1;
                if (dados[2] != null && !dados[2].trim().isEmpty()) {
                    try {
                        qtdeDiasInternacao = Integer.parseInt(dados[2].trim());
                    } catch (NumberFormatException e) {
                        // Quantidade de dias inválida
                    }
                }

                // Tipo de Procedimento
                TipoProcedimento tipoProcedimento = null;
                if (dados[3] != null && !dados[3].trim().isEmpty()) {
                    try {
                        tipoProcedimento = TipoProcedimento.valueOf(dados[3].trim());
                    } catch (IllegalArgumentException e) {
                        // Tipo de procedimento inválido
                    }
                }

                // Quantidade de procedimentos
                int qtdeProcedimentos = -1;
                if (dados.length == 5 && dados[4] != null && !dados[4].trim().isEmpty()) {
                    try {
                        qtdeProcedimentos = Integer.parseInt(dados[4].trim());
                    } catch (NumberFormatException e) {
                        // Quantidade de procedimentos inválida
                    }
                }

                // Configura a internação, se houver
                if (tipoLeito != null && qtdeDiasInternacao > 0) {
                    prontuario.setInternacao(new Internacao(tipoLeito, qtdeDiasInternacao));
                }

                // Adiciona os procedimentos
                if (tipoProcedimento != null && qtdeProcedimentos > 0) {
                    for (int i = 0; i < qtdeProcedimentos; i++) {
                        prontuario.addProcedimento(new Procedimento(tipoProcedimento));
                    }
                }
            }
        }

        return prontuario;
    }
    
}
