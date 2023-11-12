package application.analyser;

import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.util.Pair;

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
			String resultado = realizarOperacoesVariavel(findVariable(token).get());
			consumerWriteln.accept(resultado);

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
			throw new IllegalArgumentException("A ação da gramática não foi tratada.");
		}
	}

	private String realizarOperacoesVariavel(Variavel variavel) throws SemanticError {
		String resultado = "";
		
		switch (variavel.getTipo().getId()) {
		case Constants.t_int: {
			resultado = realizarOperacoesInteiros(variavel);
			break;
		}
		case Constants.t_double: {
			resultado = realizarOperacoesDecimais(variavel);
			break;
		}
		case Constants.t_string: {
			resultado = realizarOperacoesStrings(variavel);
			break;
		}
		default:
			throw new SemanticError("O tipo da variável não foi tratado para ser exibido com writeln.");
		}
		
		return resultado;
	}

	private Boolean getValueExpressaoBooleana(ExpressaoBooleana expressao) throws SemanticError {
		Boolean resultado = false;
		Integer indexOperador = 0;

		for (int i = 0; i < (expressao.getValores().size() / 2); i++) {
			Integer indexValor = i * 2;
			
			Token valor = expressao.getValores().get(indexValor);
			Token proximoValor = null;
			
			Token operador = null;
			Token proximoOperador = null;
			
			try { operador = expressao.getOperadores().get(indexOperador); } catch (IndexOutOfBoundsException e) { }
			
			try { proximoOperador = expressao.getOperadores().get(indexOperador + 1); } catch (IndexOutOfBoundsException e) { }
			
			try { proximoValor = expressao.getValores().get(indexValor + 1); } catch (IndexOutOfBoundsException e) { }
			
			switch (operador.getId()) {
			case Constants.t_TOKEN_9: {
				resultado = getBooleanMaiorQue(valor, proximoValor);
				indexOperador++;
				break;
			}
			case Constants.t_TOKEN_10: {
				resultado = getBooleanMenorQue(valor, proximoValor);
				indexOperador++;
				break;
			}
			case Constants.t_TOKEN_11: {
				resultado = getBooleanIgual(valor, proximoValor);
				indexOperador++;
				break;
			}
			case Constants.t_TOKEN_12: {
				resultado = getBooleanMaiorIgual(valor, proximoValor);
				indexOperador++;
				break;
			}
			case Constants.t_TOKEN_13: {
				resultado = getBooleanMenorIgual(valor, proximoValor);
				indexOperador++;
				break;
			}
			case Constants.t_TOKEN_7: {
				switch (proximoOperador.getId()) {
				case Constants.t_TOKEN_9: {
					resultado = resultado && getBooleanMaiorQue(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_10: {
					resultado = resultado && getBooleanMenorQue(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_11: {
					resultado = resultado && getBooleanIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_12: {
					resultado = resultado && getBooleanMaiorIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_13: {
					resultado = resultado && getBooleanMenorIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				default:
					throw new SemanticError("Erro ao obter o valor da expressão booleana. Operador booleano inválido ou não tratado.");
				}

				break;
			}
			case Constants.t_TOKEN_8: {
				switch (proximoOperador.getId()) {
				case Constants.t_TOKEN_9: {
					resultado = resultado || getBooleanMaiorQue(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_10: {
					resultado = resultado || getBooleanMenorQue(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_11: {
					resultado = resultado || getBooleanIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_12: {
					resultado = resultado || getBooleanMaiorIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				case Constants.t_TOKEN_13: {
					resultado = resultado || getBooleanMenorIgual(valor, proximoValor);
					indexOperador += 2;
					break;
				}
				default:
					throw new SemanticError("Erro ao obter o valor da expressão booleana. Operador booleano inválido ou não tratado.");
				}
				
				break;
			}
			default:
				throw new SemanticError("Erro ao obter o valor da expressão booleana. Operador booleano inválido ou não tratado.");
			}
		}

		return resultado;
	}
	
	private Pair<Double, Double> parseAndValidateNumberTokens(Token valor, Token proximoValor) throws SemanticError {
		Double numero1 = null;
		Double numero2 = null;
		
		if (tokenIsNumber(valor) && tokenIsNumber(proximoValor)) {
			
			numero1 = Double.parseDouble(valor.getLexeme());
			numero2 = Double.parseDouble(proximoValor.getLexeme());

		} else if (tokenIsIdentificador(valor) && tokenIsIdentificador(proximoValor)) {
			
			numero1 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor).get()));
			numero2 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor).get()));
			
		} else if (tokenIsIdentificador(valor)) {
			
			numero1 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor).get()));
			numero2 = Double.parseDouble(proximoValor.getLexeme());
			
		} else if (tokenIsIdentificador(proximoValor)) {

			numero1 = Double.parseDouble(valor.getLexeme());
			numero2 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor).get()));
			
		} else {
			throw new SemanticError("Erro ao obter o valor da expressão booleana. O operador > deve ser usado apenas com números.");
		}
		
		return new Pair<>(numero1, numero2);
	}

	private boolean tokenIsNumber(Token token) {
		return token.getId() == Constants.t_numeroDecimal || token.getId() == Constants.t_numeroInteiro;
	}
	
	private boolean tokenLexemeIsString(Token token) {
		return token.getId() == Constants.t_texto;
	}
	
	private boolean tokenIsIdentificador(Token token) {
		return token.getId() == t_identificador && 
				this.variaveis.stream().anyMatch(v -> v.getIdentificador().getLexeme().equals(token.getLexeme()));
	}
	
	private Boolean getBooleanMaiorQue(Token valor, Token proximoValor) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(valor, proximoValor);
		return numbers.getKey() > numbers.getValue();
	}
	
	private Boolean getBooleanMenorQue(Token valor, Token proximoValor) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(valor, proximoValor);
		return numbers.getKey() > numbers.getValue();
	}
	

	private Boolean getBooleanMaiorIgual(Token valor, Token proximoValor) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(valor, proximoValor);
		return numbers.getKey() >= numbers.getValue();
	}
	
	private Boolean getBooleanMenorIgual(Token valor, Token proximoValor) throws SemanticError {
		Pair<Double, Double> numbers = parseAndValidateNumberTokens(valor, proximoValor);
		return numbers.getKey() <= numbers.getValue();
	}
	
	private Boolean getBooleanIgual(Token valor, Token proximoValor) throws SemanticError {
		Boolean resultado = false;
		
		if (tokenIsNumber(valor) && tokenIsNumber(proximoValor)) {
			
			resultado = Double.parseDouble(valor.getLexeme()) == Double.parseDouble(proximoValor.getLexeme());
			
		} else if (tokenLexemeIsString(valor) && tokenLexemeIsString(proximoValor)) {
			
			resultado = valor.getLexeme().replace("\"", "").equals(proximoValor.getLexeme().replace("\"", ""));
			
		} else if (tokenIsIdentificador(valor) && tokenIsIdentificador(proximoValor)) {
			
			String valorVariavel1 = realizarOperacoesVariavel(findVariable(valor).get());
			String valorVariavel2 = realizarOperacoesVariavel(findVariable(proximoValor).get());

			resultado = valorVariavel1.equals(valorVariavel2);
		} else if (tokenIsIdentificador(valor)) {
			
			String valorVariavel = realizarOperacoesVariavel(findVariable(valor).get());
			
			resultado = valorVariavel.equals(proximoValor.getLexeme().replace("\"", ""));
			
		} else if (tokenIsIdentificador(proximoValor)) {
			
			String valorVariavel = realizarOperacoesVariavel(findVariable(proximoValor).get());
			
			resultado = valorVariavel.equals(valor.getLexeme().replace("\"", ""));
		} else {
			throw new SemanticError("Erro ao obter o valor da expressão booleana. O operador == deve ser usado apenas com números ou strings.");
		}
		
		return resultado;
	}

	private Optional<Variavel> findVariable(Token tokenIdentifier) throws SemanticError {
		Optional<Variavel> variavel = this.variaveis.stream()
													.filter(v -> v.getIdentificador().getLexeme().equals(tokenIdentifier.getLexeme()))
												    .findFirst();
		
		if (!variavel.isPresent()) {
			throw new SemanticError("A variável %s não foi declarada".formatted(tokenIdentifier.getLexeme()));
		}
		
		return variavel;
	}

	private String realizarOperacoesInteiros(Variavel variavel) throws SemanticError {
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

		return resultado.toString();
	}

	private String realizarOperacoesDecimais(Variavel variavel) throws SemanticError {
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

		return resultado.toString();
	}

	private String realizarOperacoesStrings(Variavel variavel) throws SemanticError {
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

		return resultado;
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
