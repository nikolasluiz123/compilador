package application.analyser;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Semantico implements Constants {

	private static final int ACTION_PEGAR_TIPO_ATRIBUICAO = 1;
	private static final int ACTION_VALIDAR_IDENTIFICADOR_EXISTENTE = 2;
	private static final int ACTION_VALIDAR_TIPO_VALOR_ATRIBUICAO = 3;
	private static final int ACTION_WRITE_LN_VALOR = 4;
	private static final int ACTION_WRITE_LN_IDENTIFICADOR = 5;
	private static final int ACTION_PEGAR_OPERACAO = 6;

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
			variavel.addValor(token);
			this.variaveis.push(variavel);

			break;
		}
		case ACTION_WRITE_LN_VALOR: {
			consumerWriteln.accept(token.getLexeme().replace("\"", ""));
			break;
		}
		case ACTION_WRITE_LN_IDENTIFICADOR: {
			Variavel variavel = this.variaveis.stream()
											  .filter(v -> v.getIdentificador().getLexeme().equals(token.getLexeme()))
											  .findFirst().get();

			switch (variavel.getTipo().getId()) {
			case Constants.t_int: {
				realizarOperacoesInteiros(variavel);
				break;
			}
			case Constants.t_double: {
				realizarOperacoesDecimais(variavel);
				break;
			}
			case Constants.t_string: {
				realizarOperacoesStrings(variavel);
				break;
			}
			default:
				throw new IllegalArgumentException("O tipo da variável não foi tratado para ser exibido com writeln.");
			}

			break;
		}
		case ACTION_PEGAR_OPERACAO: {
			Variavel variavel = this.variaveis.peek();
			variavel.addValor(token);
			break;
		}
		default:
			throw new IllegalArgumentException("A ação da gramática não foi tratada.");
		}
	}

	private void realizarOperacoesInteiros(Variavel variavel) throws SemanticError {
		Integer resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValores()) {
			if (valor.getId() == Constants.t_numeroInteiro) {
				if (resultado == null) {
					resultado = Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_3) {
					resultado -= Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_5) {
					resultado /= Integer.parseInt(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_6) {
					resultado *= Integer.parseInt(valor.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}
			} else {
				idOperacao = valor.getId();
			}
		}

		consumerWriteln.accept(resultado.toString());
	}
	
	private void realizarOperacoesDecimais(Variavel variavel) throws SemanticError {
		Double resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValores()) {
			if (valor.getId() == Constants.t_numeroDecimal) {
				if (resultado == null) {
					resultado = Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_3) {
					resultado -= Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_5) {
					resultado /= Double.parseDouble(valor.getLexeme());
				} else if (idOperacao == Constants.t_TOKEN_6) {
					resultado *= Double.parseDouble(valor.getLexeme());
				} else {
					throw new SemanticError("A operação não é suportada com o tipo double.");
				}
			} else {
				idOperacao = valor.getId();
			}
		}

		consumerWriteln.accept(resultado.toString());
	}
	
	private void realizarOperacoesStrings(Variavel variavel) throws SemanticError {
		String resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValores()) {
			if (valor.getId() == Constants.t_texto) {
				if (resultado == null) {
					resultado = valor.getLexeme().replace("\"", "");
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += valor.getLexeme().replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}
			} else {
				idOperacao = valor.getId();
			}
		}

		consumerWriteln.accept(resultado.toString());
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
		if (!(lexemeValor.startsWith("\"") && lexemeValor.endsWith("\""))) {
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
