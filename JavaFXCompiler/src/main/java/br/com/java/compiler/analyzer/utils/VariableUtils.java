package br.com.java.compiler.analyzer.utils;

import java.util.Optional;
import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe com métodos capazes de manipular as variáveis.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class VariableUtils {

	public static String calculateVariableValue(Token token, Stack<Variable> variables) throws SemanticError {
		Variable variable = VariableUtils.findVariable(token, variables);
		
		String resultado = "";

		switch (variable.getType().getId()) {
		case Constants.TOKEN_INT_TYPE: {
			resultado = realizarOperacoesInteiros(variable, variables);
			break;
		}
		case Constants.TOKEN_DOUBLE_TYPE: {
			resultado = realizarOperacoesDecimais(variable, variables);
			break;
		}
		case Constants.TOKEN_STRING_TYPE: {
			resultado = realizarOperacoesStrings(variable, variables);
			break;
		}
		default:
			throw new SemanticError("O tipo da variável não foi tratado para ser exibido com writeln.");
		}

		return resultado;
	}

	public static String realizarOperacoesInteiros(Variable variavel, Stack<Variable> variables) throws SemanticError {
		Integer resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValues()) {
			if (valor.getId() == Constants.TOKEN_INT_NUMBER) {

				if (resultado == null) {
					resultado = Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_SUBTRACTION) {
					resultado -= Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_DIVISION) {
					resultado /= Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_MULTIPLICATION) {
					resultado *= Integer.parseInt(valor.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}

			} else if (valor.getId() == Constants.TOKEN_IDENTIFIER) {
				validarTipoVariavel(valor, Constants.TOKEN_INT_TYPE, "int", variables);

				String resultadoVariavel = calculateVariableValue(valor, variables);

				if (resultado == null) {
					resultado = Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_SUBTRACTION) {
					resultado -= Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_DIVISION) {
					resultado /= Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_MULTIPLICATION) {
					resultado *= Integer.parseInt(resultadoVariavel);
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}

			} else {
				idOperacao = valor.getId();
			}
		}

		return resultado.toString();
	}

	public static String realizarOperacoesDecimais(Variable variavel, Stack<Variable> variables) throws SemanticError {
		Double resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValues()) {
			if (TokenUtils.tokenIsNumber(valor)) {

				if (resultado == null) {
					resultado = Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_SUBTRACTION) {
					resultado -= Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_DIVISION) {
					resultado /= Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.TOKEN_MULTIPLICATION) {
					resultado *= Double.parseDouble(valor.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo double.");
				}

			} else if (TokenUtils.tokenIsIdentificador(valor, variables)) {
				try {
					validarTipoVariavel(valor, Constants.TOKEN_INT_TYPE, "int", variables);
				} catch (SemanticError e) {
					validarTipoVariavel(valor, Constants.TOKEN_DOUBLE_TYPE, "double", variables);
				}

				String resultadoVariavel = calculateVariableValue(valor, variables);

				if (resultado == null) {
					resultado = Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_SUBTRACTION) {
					resultado -= Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_DIVISION) {
					resultado /= Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.TOKEN_MULTIPLICATION) {
					resultado *= Double.parseDouble(resultadoVariavel);
				} else {
					throw new SemanticError("A operação não é suportada com o tipo double.");
				}

			} else {
				idOperacao = valor.getId();
			}
		}

		return resultado.toString();
	}

	public static String realizarOperacoesStrings(Variable variavel, Stack<Variable> variables) throws SemanticError {
		String resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValues()) {
			if (TokenUtils.tokenIsString(valor)) {

				if (resultado == null) {
					resultado = valor.getLexeme().replace("\"", "");
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += valor.getLexeme().replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}

			} else if (valor.getId() == Constants.TOKEN_IDENTIFIER) {
				validarTipoVariavel(valor, Constants.TOKEN_STRING_TYPE, "string", variables);

				String resultadoVariavel = calculateVariableValue(valor, variables);

				if (resultado == null) {
					resultado = resultadoVariavel.replace("\"", "");
				} else if (idOperacao == Constants.TOKEN_SUM) {
					resultado += resultadoVariavel.replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}

			} else {
				idOperacao = valor.getId();
			}
		}

		return resultado;
	}

	public static void validarTipoVariavel(Token token, int tipo, String tipoRequerido, Stack<Variable> variables) throws SemanticError {
		Variable variavel = findVariable(token, variables);
		
		if (variavel.getType().getId() != tipo) {
			throw new SemanticError("A variável %s deve ser do tipo %s para realizar a operação.".formatted(variavel.getIdentifier().getLexeme(), tipoRequerido));
		}
	}
	
	public static Variable findVariable(Token tokenIdentifier, Stack<Variable> variables) throws SemanticError {
		Optional<Variable> variavel = variables.stream()
											   .filter(v -> v.getIdentifier().getLexeme().equals(tokenIdentifier.getLexeme()))
											   .findFirst();
		
		if (!variavel.isPresent()) {
			throw new SemanticError("A variável %s não foi declarada".formatted(tokenIdentifier.getLexeme()));
		}
		
		return variavel.get();
	}
}
