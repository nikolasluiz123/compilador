package br.com.java.compiler.analyzer.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe para representar uma expressão booleana
 * 
 * @author Nikolas Luiz Schmitt
 */
public class BooleanExpression {

	private List<Token> operators;
	private List<Token> values;

	public List<Token> getValues() {
		return values;
	}

	public void addValue(Token value) {
		if (this.values == null) {
			this.values = new ArrayList<>();
		}

		this.values.add(value);
	}

	public List<Token> getOperators() {
		return operators;
	}

	public void addOperator(Token operator) {
		if (this.operators == null) {
			this.operators = new ArrayList<>();
		}

		this.operators.add(operator);
	}
}
