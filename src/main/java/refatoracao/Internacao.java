package refatoracao;

public class Internacao {

	private TipoLeito tipoLeito;
	private int qtdeDias;

	public Internacao(TipoLeito tipoLeito, int qtdeDias) {
		this.tipoLeito = tipoLeito;
		this.qtdeDias = qtdeDias;
	}

	public TipoLeito getTipoLeito() {
		return this.tipoLeito;
	}

	public int getQtdeDias() {
		return this.qtdeDias;
	}

}
