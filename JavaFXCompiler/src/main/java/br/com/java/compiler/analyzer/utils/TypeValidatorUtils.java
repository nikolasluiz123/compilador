package br.com.java.compiler.analyzer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.error.SemanticError;
import javafx.util.Pair;

/**
 * Classe com métodos para validar o tipo dos valores
 * atribuídos para as variáveis.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class TypeValidatorUtils {

	/**
	 * Método para validar o tipo do token que está sendo atribuído
	 * para uma variável.
	 * 
	 * @param token Token a ser validado
	 * @param variables Lista de variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static void validateType(Token token, Stack<Variable> variables) throws SemanticError {
		Variable variavel = variables.peek();

		switch (variavel.getType().getId()) {
		case Constants.TOKEN_INT_TYPE: {
			validateTypeInt(token, variables);
			break;
		}
		case Constants.TOKEN_DOUBLE_TYPE: {
			validateTypeDouble(token, variables);
			break;
		}
		case Constants.TOKEN_STRING_TYPE: {
			validateTypeString(token, variables);
			break;
		}
		case Constants.TOKEN_BOOLEAN_TYPE: {
			validateTypeBoolean(token);
			break;
		}
		default:
			throw new IllegalArgumentException("O tipo da variável não foi tratado.");
		}
	}
	
	/**
	 * Método para validar o tipo booleano.
	 * 
	 * @param token Token a ser validado
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private static void validateTypeBoolean(Token token) throws SemanticError {
		if (!token.getLexeme().equals("true") && !token.getLexeme().equals("false")) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	/**
	 * Método para validar o tipo string.
	 * 
	 * @param token Token a ser validado
	 * @param variables Lista de variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private static void validateTypeString(Token token, Stack<Variable> variables) throws SemanticError {
		if (token.getId() == Constants.TOKEN_IDENTIFIER) {
			Variable variavel = VariableUtils.findVariable(token, variables);

			if (variavel.getType().getId() != Constants.TOKEN_STRING_TYPE) {
				throw new SemanticError("Tipo incompatível na posição %s. A variável %s deve ser do tipo string.".formatted(token.getId(), 
																															variavel.getIdentifier().getLexeme()));
			}
		} else if (token.getId() != Constants.TOKEN_STRING) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	/**
	 * Método para validar o tipo double
	 * 
	 * @param token Token a ser validado
	 * @param variables Lista de variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private static void validateTypeDouble(Token token, Stack<Variable> variables) throws SemanticError {
		try {
			if (TokenUtils.tokenIsIdentifier(token, variables)) {
				Variable variable = VariableUtils.findVariable(token, variables);
				Double.parseDouble(variable.calculateWithDecimals(variables));
			} else {
				Double.parseDouble(token.getLexeme());
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	/**
	 * Método para validar o tipo inteiro.
	 * 
	 * @param token Token a ser validado
	 * @param variables Lista de variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private static void validateTypeInt(Token token, Stack<Variable> variables) throws SemanticError {
		try {
			if (TokenUtils.tokenIsIdentifier(token, variables)) {
				Variable variable = VariableUtils.findVariable(token, variables);
				Integer.parseInt(variable.calculateWithIntegers(variables));
			} else {
				Integer.parseInt(token.getLexeme());
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}
	
	/**
	 * Método para validar o tipo de uma variável. O tipo é válido se estiver
	 * na lista de types passada como parâmetro.
	 * 
	 * @param variable Variável que deseja validar
	 * @param types Lista de tipos válidos
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static void validateVariableType(Variable variable, List<Pair<Integer, String>> types) throws SemanticError {
		boolean isValid = types.stream().anyMatch(t -> t.getKey() == variable.getType().getId());
		
		if (!isValid) {
			List<String> typeNames = types.stream().map(t -> t.getValue()).collect(Collectors.toList());
			throw new SemanticError("A variável %s deve ser do(s) tipo(s) %s para realizar a operação.".formatted(variable.getIdentifier().getLexeme(), typeNames));
		}
	}
	
	/**
	 * Método para validar se a variável é do tipo numérico (inteiro ou decimal).
	 * 
	 * @param variable Variável que deseja validar
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static void validateNumberVariableType(Variable variable) throws SemanticError {
		List<Pair<Integer, String>> typesToValidate = new ArrayList<>();
		typesToValidate.add(new Pair<>(Constants.TOKEN_INT_TYPE, "int"));
		typesToValidate.add(new Pair<>(Constants.TOKEN_DOUBLE_TYPE, "double"));
		
		validateVariableType(variable, typesToValidate);
	}
}
