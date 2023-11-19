package br.com.java.compiler.analyzer.utils;

import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.constant.MessageConstants;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe com métodos para manipular e verificar os tokens.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class TokenUtils {

	/**
	 * Método que retorna se o token é um número (inteiro ou decimal)
	 * 
	 * @param token
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static boolean tokenIsNumber(Token token) {
		return token.getId() == Constants.TOKEN_DOUBLE_NUMBER || token.getId() == Constants.TOKEN_INT_NUMBER;
	}
	
	/**
	 * Método que retorna se o token é uma string
	 * 
	 * @param token
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static boolean tokenIsString(Token token) {
		return token.getId() == Constants.TOKEN_STRING;
	}
	
	/**
	 * Método que retorna se o token é um identificador. Se for, já
	 * valida se foi declarado.
	 * 
	 * @param token
	 * @param variables
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static boolean tokenIsIdentifier(Token token, Stack<Variable> variables) throws SemanticError {
		if (token.getId() != Constants.TOKEN_IDENTIFIER) {
			return false;
		}
		
		if(!variables.stream().anyMatch(v -> v.getIdentifier().getLexeme().equals(token.getLexeme()))) {
			throw new SemanticError(MessageConstants.MESSAGE_VARIBLE_NOT_DECLARED.formatted(token.getLexeme()));
		}
		
		return true;
	}
	
	/**
	 * Método para remover as aspas da string literal.
	 * 
	 * @param token
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static String removeDoubleQuote(Token token) {
		return token.getLexeme().replace("\"", "");
	}
}
