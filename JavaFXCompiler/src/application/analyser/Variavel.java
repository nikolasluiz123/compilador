package application.analyser;

public class Variavel {

	private Token tipo;
	private Token identificador;
	private Token valor;

	public Token getTipo() {
		return tipo;
	}

	public void setTipo(Token tipo) {
		this.tipo = tipo;
	}

	public Token getIdentificador() {
		return identificador;
	}

	public void setIdentificador(Token identificador) {
		this.identificador = identificador;
	}

	public Token getValor() {
		return valor;
	}

	public void setValor(Token valor) {
		this.valor = valor;
	}

}
