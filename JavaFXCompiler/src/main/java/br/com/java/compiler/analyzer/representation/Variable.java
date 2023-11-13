package br.com.java.compiler.analyzer.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe para representar uma variável (tipo + identificador + valores)
 * 
 * @author Nikolas Luiz Schmitt
 */
public class Variable {

	private Token type;
	private Token identifier;
	private List<Token> values;

	public Token getType() {
		return type;
	}

	public void setType(Token type) {
		this.type = type;
	}

	public Token getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Token identifier) {
		this.identifier = identifier;
	}

	public List<Token> getValues() {
		return values;
	}

	public void addValue(Token value) {
		if (this.values == null) {
			this.values = new ArrayList<>();
		}
		
		this.values.add(value);
	}

}
