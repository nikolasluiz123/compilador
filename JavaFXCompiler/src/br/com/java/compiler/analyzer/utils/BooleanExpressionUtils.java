package br.com.java.compiler.analyzer.utils;

import java.util.Stack;

import br.com.java.compiler.analyzer.representation.BooleanExpression;
import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.constant.MessageConstants;
import br.com.java.compiler.error.SemanticError;
import javafx.util.Pair;

/**
 * Classe que contém métodos para manipular as expressões
 * booleanas do código.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class BooleanExpressionUtils {

	public static Boolean getValueBooleanExpression(BooleanExpression expressao, Stack<Variable> variables) throws SemanticError {
		Boolean result = false;
		Integer indexOperador = 0;

		for (int i = 0; i < (expressao.getValues().size() / 2); i++) {
			Integer indexValue = i * 2;
			
			Token value = expressao.getValues().get(indexValue);
			Token nextValue = null;
			
			Token operator = null;
			Token nextOperator = null;
			
			try { operator = expressao.getOperators().get(indexOperador); } catch (IndexOutOfBoundsException e) { }
			
			try { nextOperator = expressao.getOperators().get(indexOperador + 1); } catch (IndexOutOfBoundsException e) { }
			
			try { nextValue = expressao.getValues().get(indexValue + 1); } catch (IndexOutOfBoundsException e) { }
			
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
	
	private static Boolean getBooleanEquals(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Boolean result = false;
		
		if (TokenUtils.tokenIsNumber(value) && TokenUtils.tokenIsNumber(nextValue)) {
			
			result = Double.parseDouble(value.getLexeme()) == Double.parseDouble(nextValue.getLexeme());
			
		} else if (TokenUtils.tokenIsString(value) && TokenUtils.tokenIsString(nextValue)) {
			
			result = value.getLexeme().replace("\"", "").equals(nextValue.getLexeme().replace("\"", ""));
			
		} else if (TokenUtils.tokenIsIdentificador(value, variables) && TokenUtils.tokenIsIdentificador(nextValue, variables)) {
			
			String valorVariavel1 = VariableUtils.calculateVariableValue(value, variables);
			String valorVariavel2 = VariableUtils.calculateVariableValue(nextValue, variables);

			result = valorVariavel1.equals(valorVariavel2);
		} else if (TokenUtils.tokenIsIdentificador(value, variables)) {
			
			String valorVariavel = VariableUtils.calculateVariableValue(value, variables);
			
			result = valorVariavel.equals(nextValue.getLexeme().replace("\"", ""));
			
		} else if (TokenUtils.tokenIsIdentificador(nextValue, variables)) {
			
			String valorVariavel = VariableUtils.calculateVariableValue(nextValue, variables);
			
			result = valorVariavel.equals(value.getLexeme().replace("\"", ""));
		} else {
			throw new SemanticError(MessageConstants.MESSAGE_EQUAILS_OPERATOR_WITH_INVALID_TYPE_ERROR);
		}
		
		return result;
	}
	
	private static Boolean getBooleanGreaterThan(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(value, nextValue, variables);
		return numbers.getKey() > numbers.getValue();
	}
	
	private static Boolean getBooleanLessThan(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(value, nextValue, variables);
		return numbers.getKey() < numbers.getValue();
	}

	private static Boolean getBooleanGreaterEquals(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(value, nextValue, variables);
		return numbers.getKey() >= numbers.getValue();
	}
	
	private static Boolean getBooleanLessEquals(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(value, nextValue, variables);
		return numbers.getKey() <= numbers.getValue();
	}
	
	private static Pair<Double, Double> parseAndValidateNumberTokens(Token value, Token nextValue, Stack<Variable> variables) throws SemanticError {
		Double numero1 = null;
		Double numero2 = null;
		
		if (TokenUtils.tokenIsNumber(value) && TokenUtils.tokenIsNumber(nextValue)) {
			
			numero1 = Double.parseDouble(value.getLexeme());
			numero2 = Double.parseDouble(nextValue.getLexeme());

		} else if (TokenUtils.tokenIsIdentificador(value, variables) && 
				TokenUtils.tokenIsIdentificador(nextValue, variables)) {
			
			numero1 = Double.parseDouble(VariableUtils.calculateVariableValue(value, variables));
			numero2 = Double.parseDouble(VariableUtils.calculateVariableValue(value, variables));
			
		} else if (TokenUtils.tokenIsIdentificador(value, variables)) {
			
			numero1 = Double.parseDouble(VariableUtils.calculateVariableValue(value, variables));
			numero2 = Double.parseDouble(nextValue.getLexeme());
			
		} else if (TokenUtils.tokenIsIdentificador(nextValue, variables)) {

			numero1 = Double.parseDouble(value.getLexeme());
			numero2 = Double.parseDouble(VariableUtils.calculateVariableValue(nextValue, variables));
			
		} else {
			throw new SemanticError(MessageConstants.MESSAGE_GREATER_THAN_OPERATOR_WITH_INVALID_TYPE_ERROR);
		}
		
		return new Pair<>(numero1, numero2);
	}
}
