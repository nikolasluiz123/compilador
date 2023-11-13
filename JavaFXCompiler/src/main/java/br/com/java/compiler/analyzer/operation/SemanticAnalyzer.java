package br.com.java.compiler.analyzer.operation;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

import br.com.java.compiler.analyzer.representation.BooleanExpression;
import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.analyzer.representation.Variable;
import br.com.java.compiler.analyzer.utils.BooleanExpressionUtils;
import br.com.java.compiler.analyzer.utils.TypeValidatorUtils;
import br.com.java.compiler.analyzer.utils.VariableUtils;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.constant.MessageConstants;
import br.com.java.compiler.error.SemanticError;

/**
 * Classe responsável pela análise semântica da linguagem.
 * 
 * @author Nikolas Luiz Schmitt
 */
public class SemanticAnalyzer implements Constants {

	private static final int ACTION_GET_TYPE_ATRIBUITION = 1;
	private static final int ACTION_VALIDATE_IDENTIFIER_EXISTS = 2;
	private static final int ACTION_VALIDATE_ATRIBUTION_VALUE_TYPE = 3;
	private static final int ACTION_WRITE_LN_WITH_VALUE = 4;
	private static final int ACTION_WRITE_LN_WITH_IDENTIFIER = 5;
	private static final int ACTION_GET_OPERATION = 6;
	private static final int ACTION_GET_VALUE_BOOLEAN_EXPRESSION = 7;
	private static final int ACTION_GET_OPERATOR_BOOLEAN_EXPRESSION = 8;
	private static final int ACTION_FINISH_BOOLEAN_EXPRESSION = 9;

	private Stack<Variable> variables;
	
	private Stack<BooleanExpression> booleanExpressions;
	private BooleanExpression booleanExpression;

	private Consumer<String> consumerWriteln;

	public SemanticAnalyzer(Consumer<String> consumerWriteln) {
		this.variables = new Stack<>();
		this.booleanExpressions = new Stack<>();
		this.consumerWriteln = consumerWriteln;
	}

	public void executeAction(int action, Token token) throws SemanticError {
		switch (action) {
		case ACTION_GET_TYPE_ATRIBUITION: {
			Variable variable = new Variable();
			variable.setType(token);
			this.variables.push(variable);

			break;
		}
		case ACTION_VALIDATE_IDENTIFIER_EXISTS: {
			boolean exists = this.variables.stream().anyMatch(variableExistsFilter(token));

			if (!exists) {
				Variable variable = this.variables.pop();
				variable.setIdentifier(token);
				this.variables.push(variable);
			} else {
				throw new SemanticError(MessageConstants.MESSAGE_IDENTIFIER_DECLARED.formatted(token.getLexeme()));
			}

			break;
		}
		case ACTION_VALIDATE_ATRIBUTION_VALUE_TYPE: {
			TypeValidatorUtils.validateType(token, this.variables);

			Variable variable = this.variables.pop();
			variable.addValue(token);
			this.variables.push(variable);

			break;
		}
		case ACTION_WRITE_LN_WITH_VALUE: {
			boolean run = true;
			
			if (this.booleanExpression != null) {
				run = BooleanExpressionUtils.getValueBooleanExpression(this.booleanExpression, this.variables);
			}
			
			if (run) {
				this.consumerWriteln.accept(token.getLexeme().replace("\"", ""));
			}
			break;
		}
		case ACTION_WRITE_LN_WITH_IDENTIFIER: {
			String result = VariableUtils.calculateVariableValue(token, this.variables);
			consumerWriteln.accept(result);

			break;
		}
		case ACTION_GET_OPERATION: {
			Variable variable = this.variables.peek();
			variable.addValue(token);
			break;
		}
		case ACTION_GET_VALUE_BOOLEAN_EXPRESSION: {
			if (this.booleanExpression == null) {
				this.booleanExpression = new BooleanExpression();
			}

			this.booleanExpression.addValue(token);

			break;
		}
		case ACTION_GET_OPERATOR_BOOLEAN_EXPRESSION: {
			this.booleanExpression.addOperator(token);
			break;
		}
		case ACTION_FINISH_BOOLEAN_EXPRESSION: {
			if (this.booleanExpression != null) {
				this.booleanExpressions.push(this.booleanExpression);
				this.booleanExpression = null;
			}

			break;
		}
		default:
			throw new IllegalArgumentException(MessageConstants.MESSAGE_ACTION_NOT_EXPECTED);
		}
	}

	private Predicate<? super Variable> variableExistsFilter(Token token) {
		return t -> {
			if (t.getIdentifier() == null) {
				return false;
			}

			return t.getIdentifier().getLexeme().equals(token.getLexeme());
		};
	}
}
