package br.com.java.compiler.analyzer.utils;

import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe com métodos para validar o tipo dos valores
 * atribuídos para as variáveis.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class TypeValidatorUtils {

	public static void validateType(Token token, Stack<Variable> variables) throws SemanticError {
		Variable variavel = variables.peek();
		String lexemeValor = token.getLexeme();

		switch (variavel.getType().getId()) {
		case Constants.TOKEN_INT_TYPE: {
			validateTypeInt(token, lexemeValor, variables);
			break;
		}
		case Constants.TOKEN_DOUBLE_TYPE: {
			validateTypeDouble(token, lexemeValor, variables);
			break;
		}
		case Constants.TOKEN_STRING_TYPE: {
			validateTypeString(token, lexemeValor, variables);
			break;
		}
		case Constants.TOKEN_BOOLEAN_TYPE: {
			validateTypeBoolean(token, lexemeValor);
			break;
		}
		default:
			throw new IllegalArgumentException("O tipo da variável não foi tratado.");
		}
	}

	public static void validateTypeBoolean(Token token, String lexemeValor) throws SemanticError {
		if (!lexemeValor.equals("true") && !lexemeValor.equals("false")) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	public static void validateTypeString(Token token, String lexemeValor, Stack<Variable> variables) throws SemanticError {
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

	public static void validateTypeDouble(Token token, String lexemeValor, Stack<Variable> variables) throws SemanticError {
		try {
			if (token.getId() == Constants.TOKEN_IDENTIFIER) {
				Variable variable = VariableUtils.findVariable(token, variables);
				Double.parseDouble(VariableUtils.realizarOperacoesDecimais(variable, variables));
			} else {
				Double.parseDouble(lexemeValor);
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	public static void validateTypeInt(Token token, String lexemeValor, Stack<Variable> variables) throws SemanticError {
		try {
			if (token.getId() == Constants.TOKEN_IDENTIFIER) {
				Variable variable = VariableUtils.findVariable(token, variables);
				Integer.parseInt(VariableUtils.realizarOperacoesInteiros(variable, variables));
			} else {
				Integer.parseInt(lexemeValor);
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}
}
