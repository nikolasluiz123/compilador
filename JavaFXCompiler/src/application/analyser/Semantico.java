package application.analyser;

import java.util.Iterator;
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
	private static final int ACTION_PEGAR_VALOR_EXPRESSAO_BOOLEANA = 7;
	private static final int ACTION_PEGAR_OPERADOR_EXPRESSAO_BOOLEANA = 8;
	private static final int ACTION_FINALIZAR_EXPRESSAO_BOOLEANA = 9;

	private Stack<Variavel> variaveis;
	private Stack<ExpressaoBooleana> expressoes;
	private ExpressaoBooleana expressao;

	private Consumer<String> consumerWriteln;

	public Semantico(Consumer<String> consumerWriteln) {
		this.variaveis = new Stack<>();
		this.expressoes = new Stack<>();
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
				throw new SemanticError("O identificador %s j� foi declarado.".formatted(token.getLexeme()));
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
					.filter(v -> v.getIdentificador().getLexeme().equals(token.getLexeme())).findFirst().get();

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
				throw new IllegalArgumentException("O tipo da vari�vel n�o foi tratado para ser exibido com writeln.");
			}

			break;
		}
		case ACTION_PEGAR_OPERACAO: {
			Variavel variavel = this.variaveis.peek();
			variavel.addValor(token);
			break;
		}
		case ACTION_PEGAR_VALOR_EXPRESSAO_BOOLEANA: {
			if (this.expressao == null) {
				this.expressao = new ExpressaoBooleana();
			}

			this.expressao.addValor(token);

			break;
		}
		case ACTION_PEGAR_OPERADOR_EXPRESSAO_BOOLEANA: {
			this.expressao.addOperador(token);
			break;
		}
		case ACTION_FINALIZAR_EXPRESSAO_BOOLEANA: {
			this.expressoes.push(this.expressao);
			System.out.println(getValueExpressaoBooleana(this.expressao));
			this.expressao = null;

			break;
		}
		default:
			throw new IllegalArgumentException("A a��o da gram�tica n�o foi tratada.");
		}
	}

	private Boolean getValueExpressaoBooleana(ExpressaoBooleana expressao) throws SemanticError {
		Boolean resultado = false;

		for (int i = 0; i < (expressao.getValores().size() / 2); i++) {
			Integer indexValor = i * 2;
			Token valor = expressao.getValores().get(indexValor);
			Token operador = null;
			Token proximoOperador = null;
			Token proximoValor = null;
			try {
				operador = expressao.getOperadores().get(i);
				proximoValor = expressao.getValores().get(indexValor + 1);
				proximoOperador = expressao.getOperadores().get(i + 1);
			} catch (IndexOutOfBoundsException e) {

			}

			switch (operador.getId()) {
			case Constants.t_TOKEN_10: {
				resultado = getBooleanMaiorQue(valor, proximoValor);
				break;
			}
			case Constants.t_TOKEN_7: {
				switch (proximoOperador.getId()) {
				case Constants.t_TOKEN_10: {
					resultado = resultado && getBooleanMaiorQue(valor, proximoValor);
					break;
				}
				default:
					throw new SemanticError("Erro ao obter o valor da express�o booleana. Operador booleano inv�lido ou n�o tratado.");
				}

				break;
			}
			default:
				throw new SemanticError("Erro ao obter o valor da express�o booleana. Operador booleano inv�lido ou n�o tratado.");
			}
		}

		return resultado;
	}

	private Boolean getBooleanMaiorQue(Token valor, Token proximoValor) throws SemanticError {
		if (valor.getId() == Constants.t_numeroDecimal || valor.getId() == Constants.t_numeroInteiro) {
			Double numero1 = Double.parseDouble(valor.getLexeme());
			Double numero2 = Double.parseDouble(proximoValor.getLexeme());

			return numero1 > numero2;
		} else {
			throw new SemanticError("Erro ao obter o valor da express�o booleana. O operador > deve ser usado apenas com n�meros.");
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
					throw new SemanticError("A opera��o n�o � suportada com o tipo int.");
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
					throw new SemanticError("A opera��o n�o � suportada com o tipo double.");
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
					throw new SemanticError("A opera��o n�o � suportada com o tipo string.");
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
			throw new IllegalArgumentException("O tipo da vari�vel n�o foi tratado.");
		}
	}

	private void validarTipoBoolean(Token token, String lexemeValor) throws SemanticError {
		if (!lexemeValor.equals("true") && !lexemeValor.equals("false")) {
			throw new SemanticError("Tipo incompat�vel na posi��o " + token.getPosition());
		}
	}

	private void validarTipoString(Token token, String lexemeValor) throws SemanticError {
		if (!(lexemeValor.startsWith("\"") && lexemeValor.endsWith("\""))) {
			throw new SemanticError("Tipo incompat�vel na posi��o " + token.getPosition());
		}
	}

	private void validarTipoDouble(Token token, String lexemeValor) throws SemanticError {
		try {
			Double.parseDouble(lexemeValor);
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompat�vel na posi��o " + token.getPosition());
		}
	}

	private void validarTipoInteiro(Token token, String lexemeValor) throws SemanticError {
		try {
			Integer.parseInt(lexemeValor);
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompat�vel na posi��o " + token.getPosition());
		}
	}
}
