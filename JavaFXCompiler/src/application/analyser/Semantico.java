package application.analyser;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Semantico implements Constants {

	private static final int ACTION_PEGAR_TIPO_ATRIBUICAO = 1;
	private static final int ACTION_VALIDAR_IDENTIFICADOR_EXISTENTE = 2;
	private static final int ACTION_VALIDAR_TIPO_VALOR_ATRIBUICAO = 3;
	private static final int ACTION_WRITE_LN_VALOR = 4;
	
	private Stack<Variavel> variaveis;
	
	private Consumer<String> consumerWriteln;
	
	public Semantico(Consumer<String> consumerWriteln) {
		this.variaveis = new Stack<>();
		this.consumerWriteln = consumerWriteln;
	}

	public void executeAction(int action, Token token) throws SemanticError {
		switch (action) {
		case ACTION_PEGAR_TIPO_ATRIBUICAO: {
			Variavel variavel = new Variavel();
			variavel.setTipo(token);
			this.variaveis.push(variavel);
			
			break;
		}
		case ACTION_VALIDAR_IDENTIFICADOR_EXISTENTE: {
			boolean exists = this.variaveis.stream().anyMatch(variavelExists(token));
			
			if (!exists) {
				Variavel variavel = this.variaveis.pop();
				variavel.setIdentificador(token);
				this.variaveis.push(variavel);
			} else {
				throw new SemanticError("O identificador %s já foi declarado.".formatted(token.getLexeme()));
			}
			
			break;
		}
		case ACTION_VALIDAR_TIPO_VALOR_ATRIBUICAO: {
			validarTipos(token);
			
			Variavel variavel = this.variaveis.pop();
			variavel.setValor(token);
			this.variaveis.push(variavel);
			
			break;
		}
		case ACTION_WRITE_LN_VALOR: {
			consumerWriteln.accept(token.getLexeme().replace("\"", ""));
			break;
		}
		default:
			throw new IllegalArgumentException("A ação da gramática não foi tratada.");
		}
	}

	private Predicate<? super Variavel> variavelExists(Token token) {
		return t -> {
			if (t.getIdentificador() == null) {
				return false;
			}
			
			return t.getIdentificador().getLexeme().equals(token.getLexeme());
		};
	}

	private void validarTipos(Token token) throws SemanticError {
		Variavel variavel = this.variaveis.peek();
		String lexemeValor = token.getLexeme();
		
		switch (variavel.getTipo().getId()) {
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
