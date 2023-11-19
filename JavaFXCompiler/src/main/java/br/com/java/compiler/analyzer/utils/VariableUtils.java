package br.com.java.compiler.analyzer.utils;

import java.util.Optional;
import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe com métodos capazes de manipular as variáveis.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class VariableUtils {

	/**
	 * Método que pode ser usado para buscar uma variável
	 * pelo token.
	 * 
	 * @param tokenIdentifier Token que deseja buscar na lista de variáveis
	 * @param variables Lista de variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
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
