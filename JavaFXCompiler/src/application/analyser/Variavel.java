package application.analyser;

import java.util.ArrayList;
import java.util.List;

public class Variavel {

	private Token tipo;
	private Token identificador;
	private List<Token> valores;

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

	public List<Token> getValores() {
		return valores;
	}

	public void addValor(Token valor) {
		if (this.valores == null) {
			this.valores = new ArrayList<>();
		}
		
		this.valores.add(valor);
	}

}
