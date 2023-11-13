package br.com.java.compiler.analyzer.utils;

import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;

/**
 * Classe com métodos para manipular e verificar os tokens.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class TokenUtils {

	public static boolean tokenIsNumber(Token token) {
		return token.getId() == Constants.TOKEN_DOUBLE_NUMBER || token.getId() == Constants.TOKEN_INT_NUMBER;
	}
	
	public static boolean tokenIsString(Token token) {
		return token.getId() == Constants.TOKEN_STRING;
	}
	
	public static boolean tokenIsIdentificador(Token token, Stack<Variable> variables) {
		return token.getId() == Constants.TOKEN_IDENTIFIER && 
				variables.stream().anyMatch(v -> v.getIdentifier().getLexeme().equals(token.getLexeme()));
	}
}
