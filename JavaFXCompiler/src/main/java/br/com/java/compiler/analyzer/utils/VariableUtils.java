package br.com.java.compiler.analyzer.utils;

import java.util.Optional;
import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe com m�todos capazes de manipular as vari�veis.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class VariableUtils {

	/**
	 * M�todo que pode ser usado para buscar uma vari�vel
	 * pelo token.
	 * 
	 * @param tokenIdentifier Token que deseja buscar na lista de vari�veis
	 * @param variables Lista de vari�veis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public static Variable findVariable(Token tokenIdentifier, Stack<Variable> variables) throws SemanticError {
		Optional<Variable> variavel = variables.stream()
											   .filter(v -> v.getIdentifier().getLexeme().equals(tokenIdentifier.getLexeme()))
											   .findFirst();
		
		if (!variavel.isPresent()) {
			throw new SemanticError("A vari�vel %s n�o foi declarada".formatted(tokenIdentifier.getLexeme()));
		}
		
		return variavel.get();
	}
}
