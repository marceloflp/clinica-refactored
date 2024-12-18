package refatoracao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProntuarioController{

    private Prontuario prontuario;
    private Calculadora calculadora;

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
        this.calculadora = new Calculadora();
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
						valorDiarias = calculadora.multiplicar(40.00f, prontuario.getInternacao().getQtdeDias()); 
					} else if (prontuario.getInternacao().getQtdeDias() <= 8) {
						valorDiarias = calculadora.multiplicar(35.00f, prontuario.getInternacao().getQtdeDias()); 
					} else {
						valorDiarias = calculadora.multiplicar(35.00f, prontuario.getInternacao().getQtdeDias()); 
					}
					break;
				case APARTAMENTO:
					if (prontuario.getInternacao().getQtdeDias() <= 3) {
						valorDiarias = calculadora.multiplicar(100.00f, prontuario.getInternacao().getQtdeDias()); 
					} else if (prontuario.getInternacao().getQtdeDias() <= 8) {
						valorDiarias += 90.00 * prontuario.getInternacao().getQtdeDias();  
					} else {
						valorDiarias = calculadora.multiplicar(80.00f, prontuario.getInternacao().getQtdeDias());  
					}
					break;
			}
		}

        for (Procedimento procedimento : prontuario.getProcedimentos()) {
			switch (procedimento.getTipoProcedimento()) {
				case BASICO:
					qtdeProcedimentosBasicos++;
					valorTotalProcedimentos = calculadora.somar(valorTotalProcedimentos, 50.00f);
					break;

				case COMUM:
					qtdeProcedimentosComuns++;
					valorTotalProcedimentos = calculadora.somar(valorTotalProcedimentos, 150.00f);
					break;

				case AVANCADO:
					qtdeProcedimentosAvancados++;
					valorTotalProcedimentos = calculadora.somar(valorTotalProcedimentos, 500.00f);
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
                    continue; 
                }

                String[] dados = linha.split(",");
                if (dados.length < 4) {
                    continue; 
                }

                String nomePaciente = dados[0].trim();
                prontuario.setNomePaciente(nomePaciente);

                TipoLeito tipoLeito = null;
                if (dados[1] != null && !dados[1].trim().isEmpty()) {
                    try {
                        tipoLeito = TipoLeito.valueOf(dados[1].trim());
                    } catch (IllegalArgumentException e) {
                        
                    }
                }

                int qtdeDiasInternacao = -1;
                if (dados[2] != null && !dados[2].trim().isEmpty()) {
                    try {
                        qtdeDiasInternacao = Integer.parseInt(dados[2].trim());
                    } catch (NumberFormatException e) {
                        
                    }
                }

                TipoProcedimento tipoProcedimento = null;
                if (dados[3] != null && !dados[3].trim().isEmpty()) {
                    try {
                        tipoProcedimento = TipoProcedimento.valueOf(dados[3].trim());
                    } catch (IllegalArgumentException e) {

                    }
                }

                int qtdeProcedimentos = -1;
                if (dados.length == 5 && dados[4] != null && !dados[4].trim().isEmpty()) {
                    try {
                        qtdeProcedimentos = Integer.parseInt(dados[4].trim());
                    } catch (NumberFormatException e) {
                        
                    }
                }

                if (tipoLeito != null && qtdeDiasInternacao > 0) {
                    prontuario.setInternacao(new Internacao(tipoLeito, qtdeDiasInternacao));
                }

                if (tipoProcedimento != null && qtdeProcedimentos > 0) {
                    for (int i = 0; i < qtdeProcedimentos; i++) {
                        prontuario.addProcedimento(new Procedimento(tipoProcedimento));
                    }
                }
            }
        }

        return prontuario;
    }

    public String salveProntuario() throws IOException {
    
        List<String> linhas = new ArrayList<>();
        
        linhas.add("nome_paciente,tipo_leito,qtde_dias_internacao,tipo_procedimento,qtde_procedimentos");

        String linhaInternacao = prontuario.getNomePaciente() + ",";

        if (prontuario.getInternacao() != null) {
            linhaInternacao += prontuario.getInternacao().getTipoLeito() + ","
                    + prontuario.getInternacao().getQtdeDias() + ",,";
            linhas.add(linhaInternacao);
        } else {
            linhaInternacao += ",,,";
            linhas.add(linhaInternacao);
        }

        if (prontuario.getProcedimentos().size() > 0) {
            Map<TipoProcedimento, Long> procedimentosAgrupados = prontuario.getProcedimentos().stream()
                    .collect(Collectors.groupingBy(Procedimento::getTipoProcedimento, Collectors.counting()));
            
            List<TipoProcedimento> procedimentosOrdenados = new ArrayList<>(procedimentosAgrupados.keySet());
            Collections.sort(procedimentosOrdenados);

            for (TipoProcedimento tipo : procedimentosOrdenados) {
                String linhaProcedimento = prontuario.getNomePaciente() + ",,," + tipo + ","
                        + procedimentosAgrupados.get(tipo);
                linhas.add(linhaProcedimento);
            }
        } else {
            linhas.add(prontuario.getNomePaciente() + ",,,,");
        }

        Path path = Paths.get(prontuario.getNomePaciente().replaceAll(" ", "_")
                .concat(String.valueOf(System.currentTimeMillis())).concat(".csv"));

        Files.write(path, linhas);

        return path.toString();
    }
    
}
