package br.com.java.compiler.analyzer.representation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import br.com.java.compiler.analyzer.utils.TokenUtils;
import br.com.java.compiler.analyzer.utils.TypeValidatorUtils;
import br.com.java.compiler.analyzer.utils.VariableUtils;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.constant.MessageConstants;
import br.com.java.compiler.error.SemanticError;
import javafx.util.Pair;

/**
 * Classe para representar uma expressão booleana
 * 
 * @author Nikolas Luiz Schmitt
 */
public class BooleanExpression {

	private List<Token> operators;
	private List<Token> values;

	/**
	 * Método usado para transformar uma expressão booleana
	 * em um valor booleano (true ou false).
	 * 
	 * @param variables Lista das variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public Boolean getValue(Stack<Variable> variables) throws SemanticError {
		Boolean result = false;
		Integer indexOperador = 0;

		for (int i = 0; i < (this.getValues().size() / 2); i++) {
			Integer indexValue = i * 2;
			
			Token value = this.getValues().get(indexValue);
			Token nextValue = null;
			
			Token operator = null;
			Token nextOperator = null;
			
			try { operator = this.getOperators().get(indexOperador); } catch (IndexOutOfBoundsException e) { }
			
			try { nextOperator = this.getOperators().get(indexOperador + 1); } catch (IndexOutOfBoundsException e) { }
			
			try { nextValue = this.getValues().get(indexValue + 1); } catch (IndexOutOfBoundsException e) { }
			
			switch (operator.getId()) {
			case Constants.TOKEN_GREATER_THAN: {
				result = getBooleanGreaterThan(value, nextValue, variables);
				indexOperador++;
				break;
			}
			case Constants.TOKEN_LESS_THAN: {
				result = getBooleanLessThan(value, nextValue, variables);
				indexOperador++;
				break;
			}
			case Constants.TOKEN_EQUALS: {
				result = getBooleanEquals(value, nextValue, variables);
				indexOperador++;
				break;
			}
			case Constants.TOKEN_GREATER_EQUALS: {
				result = getBooleanGreaterEquals(value, nextValue, variables);
				indexOperador++;
				break;
			}
			case Constants.TOKEN_LESS_EQUALS: {
				result = getBooleanLessEquals(value, nextValue, variables);
				indexOperador++;
				break;
			}
			case Constants.TOKEN_AND: {
				switch (nextOperator.getId()) {
				case Constants.TOKEN_GREATER_THAN: {
					result = result && getBooleanGreaterThan(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_LESS_THAN: {
					result = result && getBooleanLessThan(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_EQUALS: {
					result = result && getBooleanEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_GREATER_EQUALS: {
					result = result && getBooleanGreaterEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_LESS_EQUALS: {
					result = result && getBooleanLessEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				default:
					throw new SemanticError(MessageConstants.MESSAGE_INVALID_BOOLEAN_OPERATOR_ERROR);
				}

				break;
			}
			case Constants.TOKEN_OR: {
				switch (nextOperator.getId()) {
				case Constants.TOKEN_GREATER_THAN: {
					result = result || getBooleanGreaterThan(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_LESS_THAN: {
					result = result || getBooleanLessThan(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_EQUALS: {
					result = result || getBooleanEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_GREATER_EQUALS: {
					result = result || getBooleanGreaterEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				case Constants.TOKEN_LESS_EQUALS: {
					result = result || getBooleanLessEquals(value, nextValue, variables);
					indexOperador += 2;
					break;
				}
				default:
					throw new SemanticError(MessageConstants.MESSAGE_INVALID_BOOLEAN_OPERATOR_ERROR);
				}
				
				break;
			}
			default:
				throw new SemanticError(MessageConstants.MESSAGE_INVALID_BOOLEAN_OPERATOR_ERROR);
			}
		}

		return result;
	}
	
	/**
	 * Método que realiza a comparação de igualdade entre dois
	 * tokens e retorna um valor booleano.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista das variáveis declaradas
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Boolean getBooleanEquals(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Boolean result = false;
		
		if (TokenUtils.tokenIsNumber(token) && TokenUtils.tokenIsNumber(nextToken)) {
			
			result = Double.parseDouble(token.getLexeme()) == Double.parseDouble(nextToken.getLexeme());
			
		} else if (TokenUtils.tokenIsString(token) && TokenUtils.tokenIsString(nextToken)) {
			
			result = TokenUtils.removeDoubleQuote(token).equals(TokenUtils.removeDoubleQuote(nextToken));
			
		} else if (TokenUtils.tokenIsIdentifier(token, variables) && TokenUtils.tokenIsIdentifier(nextToken, variables)) {
			
			Variable variable1 = VariableUtils.findVariable(token, variables);
			Variable variable2 = VariableUtils.findVariable(nextToken, variables);
			
			String valueVariable1 = variable1.calculateValue(variables);
			String valueVariable2 = variable2.calculateValue(variables);
			
			result = valueVariable1.equals(valueVariable2);
		} else if (TokenUtils.tokenIsIdentifier(token, variables)) {
			
			Variable variable = VariableUtils.findVariable(token, variables);
			String variableValue = variable.calculateValue(variables);
			
			result = variableValue.equals(TokenUtils.removeDoubleQuote(nextToken));
			
		} else if (TokenUtils.tokenIsIdentifier(nextToken, variables)) {
			
			Variable variable = VariableUtils.findVariable(nextToken, variables);
			String variableValue = variable.calculateValue(variables);
			
			result = variableValue.equals(TokenUtils.removeDoubleQuote(token));
		} else {
			throw new SemanticError(MessageConstants.MESSAGE_EQUAILS_OPERATOR_WITH_INVALID_TYPE_ERROR);
		}
		
		return result;
	}
	
	/**
	 * Método que verifica se um token possui um valor maior que outro.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Boolean getBooleanGreaterThan(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(token, nextToken, variables);
		return numbers.getKey() > numbers.getValue();
	}
	
	/**
	 * Método que retorna se um token possui um valor menor que o outro.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Boolean getBooleanLessThan(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(token, nextToken, variables);
		return numbers.getKey() < numbers.getValue();
	}

	/**
	 * Método que retorna se um token possui um valor maior ou igual ao outro.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Boolean getBooleanGreaterEquals(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(token, nextToken, variables);
		return numbers.getKey() >= numbers.getValue();
	}
	
	/**
	 * Método que retorna se um token possui um valor menor ou igual ao outro.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Boolean getBooleanLessEquals(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(token, nextToken, variables);
		return numbers.getKey() <= numbers.getValue();
	}
	
	/**
	 * Método que transforma dois tokens em um par de números. 
	 * 
	 * Se os tokens forem números literais só é feito um parse
	 * e eles são retornados em uma estrutura de {@link Pair}.
	 * 
	 * Se os tokens forem variáveis, o valor é calculado e também
	 * é retornado na estrutura de {@link Pair}.
	 * 
	 * @param token Primeiro token da comparação
	 * @param nextToken Segundo token da comparação
	 * @param variables Lista de variáveis declaradas.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	private Pair<Double, Double> parseAndValidateNumberTokens(Token token, Token nextToken, Stack<Variable> variables) throws SemanticError {
		Double numero1 = null;
		Double numero2 = null;
		
		if (TokenUtils.tokenIsNumber(token) && TokenUtils.tokenIsNumber(nextToken)) {
			
			numero1 = Double.parseDouble(token.getLexeme());
			numero2 = Double.parseDouble(nextToken.getLexeme());

		} else if (TokenUtils.tokenIsIdentifier(token, variables) && TokenUtils.tokenIsIdentifier(nextToken, variables)) {
			Variable variable1 = VariableUtils.findVariable(token, variables);
			Variable variable2 = VariableUtils.findVariable(nextToken, variables);
			
			TypeValidatorUtils.validateNumberVariableType(variable1);
			TypeValidatorUtils.validateNumberVariableType(variable2);
			
			numero1 = Double.parseDouble(variable1.calculateValue(variables));
			numero2 = Double.parseDouble(variable2.calculateValue(variables));
			
		} else if (TokenUtils.tokenIsIdentifier(token, variables)) {
			
			Variable variable = VariableUtils.findVariable(token, variables);
			
			TypeValidatorUtils.validateNumberVariableType(variable);
			
			numero1 = Double.parseDouble(variable.calculateValue(variables));
			numero2 = Double.parseDouble(nextToken.getLexeme());
			
		} else if (TokenUtils.tokenIsIdentifier(nextToken, variables)) {

			Variable variable = VariableUtils.findVariable(nextToken, variables);
			
			TypeValidatorUtils.validateNumberVariableType(variable);
			
			numero1 = Double.parseDouble(token.getLexeme());
			numero2 = Double.parseDouble(variable.calculateValue(variables));
			
		} else {
			throw new SemanticError(MessageConstants.MESSAGE_GREATER_THAN_OPERATOR_WITH_INVALID_TYPE_ERROR);
		}
		
		return new Pair<>(numero1, numero2);
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
