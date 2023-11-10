package application.analyser;

import java.util.ArrayList;
import java.util.List;

public class ExpressaoBooleana {

	private List<Token> operadores;
	private List<Token> valores;

	public List<Token> getValores() {
		return valores;
	}

	public void addValor(Token valor) {
		if (this.valores == null) {
			this.valores = new ArrayList<>();
		}

		this.valores.add(valor);
	}
	
	public List<Token> getOperadores() {
		return operadores;
	}

	public void addOperador(Token operador) {
		if (this.operadores == null) {
			this.operadores = new ArrayList<>();
		}

		this.operadores.add(operador);
	}


}
