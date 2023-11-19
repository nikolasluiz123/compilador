package br.com.java.compiler.analyzer.representation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import br.com.java.compiler.analyzer.utils.TokenUtils;
import br.com.java.compiler.analyzer.utils.TypeValidatorUtils;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.error.SemanticError;
import javafx.util.Pair;

/**
 * Classe para representar uma variável (tipo + identificador + valores)
 * 
 * @author Nikolas Luiz Schmitt
 */
public class Variable {

	private Token type;
	private Token identifier;
	private List<Token> values;
	
	/**
	 * Método que realiza o cálculo do valor da variável,
	 * retornando o resultado sempre como string.
	 * 
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public String calculateValue(Stack<Variable> variables) throws SemanticError {
		String result = "";

		switch (this.getType().getId()) {
		case Constants.TOKEN_INT_TYPE: {
			result = calculateWithIntegers(variables);
			break;
		}
		case Constants.TOKEN_DOUBLE_TYPE: {
			result = calculateWithDecimals(variables);
			break;
		}
		case Constants.TOKEN_STRING_TYPE: {
			result = calculateWithStrings(variables);
			break;
		}
		default:
			throw new SemanticError("O tipo da variável não foi tratado para ser calculado.");
		}

		return result;
	}

	/**
	 * Método que realiza os cálculos com inteiros.
	 * 
	 * @param variables
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public String calculateWithIntegers(Stack<Variable> variables) throws SemanticError {
		Integer result = null;
		Integer operationId = null;

		for (Token token : this.getValues()) {
			if (token.getId() == Constants.TOKEN_INT_NUMBER) {

				if (result == null) {
					result = Integer.parseInt(token.getLexeme());
				} else if (operationId == Constants.TOKEN_SUM) {
					result += Integer.parseInt(token.getLexeme());
				} else if (operationId == Constants.TOKEN_SUBTRACTION) {
					result -= Integer.parseInt(token.getLexeme());
				} else if (operationId == Constants.TOKEN_DIVISION) {
					result /= Integer.parseInt(token.getLexeme());
				} else if (operationId == Constants.TOKEN_MULTIPLICATION) {
					result *= Integer.parseInt(token.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}

			} else if (TokenUtils.tokenIsIdentifier(token, variables)) {
				TypeValidatorUtils.validateVariableType(this, Arrays.asList(new Pair<>(Constants.TOKEN_INT_TYPE, "int")));
				String variableValue = calculateValue(variables);

				if (result == null) {
					result = Integer.parseInt(variableValue);
				} else if (operationId == Constants.TOKEN_SUM) {
					result += Integer.parseInt(variableValue);
				} else if (operationId == Constants.TOKEN_SUBTRACTION) {
					result -= Integer.parseInt(variableValue);
				} else if (operationId == Constants.TOKEN_DIVISION) {
					result /= Integer.parseInt(variableValue);
				} else if (operationId == Constants.TOKEN_MULTIPLICATION) {
					result *= Integer.parseInt(variableValue);
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}

			} else {
				operationId = token.getId();
			}
		}

		return result.toString();
	}

	/**
	 * Método que realiza os cálculos com decimais.
	 * 
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public String calculateWithDecimals(Stack<Variable> variables) throws SemanticError {
		Double result = null;
		Integer operationId = null;

		for (Token token : this.getValues()) {
			if (TokenUtils.tokenIsNumber(token)) {

				if (result == null) {
					result = Double.parseDouble(token.getLexeme());
				} else if (operationId == Constants.TOKEN_SUM) {
					result += Double.parseDouble(token.getLexeme());
				} else if (operationId == Constants.TOKEN_SUBTRACTION) {
					result -= Double.parseDouble(token.getLexeme());
				} else if (operationId == Constants.TOKEN_DIVISION) {
					result /= Double.parseDouble(token.getLexeme());
				} else if (operationId == Constants.TOKEN_MULTIPLICATION) {
					result *= Double.parseDouble(token.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo double.");
				}

			} else if (TokenUtils.tokenIsIdentifier(token, variables)) {
				TypeValidatorUtils.validateNumberVariableType(this);
				
				String variableResult = calculateValue(variables);

				if (result == null) {
					result = Double.parseDouble(variableResult);
				} else if (operationId == Constants.TOKEN_SUM) {
					result += Double.parseDouble(variableResult);
				} else if (operationId == Constants.TOKEN_SUBTRACTION) {
					result -= Double.parseDouble(variableResult);
				} else if (operationId == Constants.TOKEN_DIVISION) {
					result /= Double.parseDouble(variableResult);
				} else if (operationId == Constants.TOKEN_MULTIPLICATION) {
					result *= Double.parseDouble(variableResult);
				} else {
					throw new SemanticError("A operação não é suportada com o tipo double.");
				}

			} else {
				operationId = token.getId();
			}
		}

		return result.toString();
	}

	/**
	 * Método que realiza os cálculos com strings.
	 * 
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public String calculateWithStrings(Stack<Variable> variables) throws SemanticError {
		String result = null;
		Integer operationId = null;

		for (Token token : this.getValues()) {
			if (TokenUtils.tokenIsString(token)) {

				if (result == null) {
					result = token.getLexeme().replace("\"", "");
				} else if (operationId == Constants.TOKEN_SUM) {
					result += token.getLexeme().replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}

			} else if (TokenUtils.tokenIsIdentifier(token, variables)) {
				TypeValidatorUtils.validateVariableType(this, Arrays.asList(new Pair<>(Constants.TOKEN_STRING_TYPE, "string")));

				String variableResult = calculateValue(variables);

				if (result == null) {
					result = variableResult.replace("\"", "");
				} else if (operationId == Constants.TOKEN_SUM) {
					result += variableResult.replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}

			} else {
				operationId = token.getId();
			}
		}

		return result;
	}
	
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
