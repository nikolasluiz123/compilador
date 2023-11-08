package application.analyser;

import java.util.Stack;

public class Semantico implements Constants {

	private static final int ACTION_PEGAR_TIPO_ATRIBUICAO = 1;
	private static final int ACTION_VALIDAR_TIPO_VALOR_ATRIBUICAO = 2;
	
	private Stack<Token> tokens;
	
	public Semantico() {
		this.tokens = new Stack<>();
	}

	public void executeAction(int action, Token token) throws SemanticError {
		switch (action) {
		case ACTION_PEGAR_TIPO_ATRIBUICAO: {
			this.tokens.push(token);
			break;
		}
		case ACTION_VALIDAR_TIPO_VALOR_ATRIBUICAO: {
			validarTipos(token);
			break;
		}
		default:
			throw new IllegalArgumentException("A ação da gramática não foi tratada.");
		}
	}

	private void validarTipos(Token token) throws SemanticError {
		Token tokenTipo = this.tokens.pop();
		String lexemeValor = token.getLexeme();
		
		switch (tokenTipo.getId()) {
		case Constants.t_int: {
			validarTipoInteiro(token, lexemeValor);
			break;
		}
		case Constants.t_double: {
			validarTipoDouble(token, lexemeValor);
			break;
		}
		case Constants.t_string: {
			validarTipoString(token, lexemeValor);
			break;
		}
		case Constants.t_boolean: {
			validarTipoBoolean(token, lexemeValor);
			break;
		}
		default:
			throw new IllegalArgumentException("O tipo da variável não foi tratado.");
		}
	}

	private void validarTipoBoolean(Token token, String lexemeValor) throws SemanticError {
		if (!lexemeValor.equals("true") && !lexemeValor.equals("false")) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	private void validarTipoString(Token token, String lexemeValor) throws SemanticError {
		if(!(lexemeValor.startsWith("\"") && lexemeValor.endsWith("\""))) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	private void validarTipoDouble(Token token, String lexemeValor) throws SemanticError {
		try {
			Double.parseDouble(lexemeValor);
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	private void validarTipoInteiro(Token token, String lexemeValor) throws SemanticError {
		try {
			Integer.parseInt(lexemeValor);
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}
}
