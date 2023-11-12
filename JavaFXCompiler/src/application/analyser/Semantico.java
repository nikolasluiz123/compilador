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
			boolean executar = true;
			
			if (expressao != null) {
				executar = getValueExpressaoBooleana(expressao);
			}
			
			if (executar) {
				consumerWriteln.accept(token.getLexeme().replace("\"", ""));
			}
			break;
		}
		case ACTION_WRITE_LN_IDENTIFICADOR: {
			String resultado = realizarOperacoesVariavel(findVariable(token));
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
			if (this.expressao != null) {
				this.expressoes.push(this.expressao);
				this.expressao = null;
			}

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
			
			numero1 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor)));
			numero2 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor)));
			
		} else if (tokenIsIdentificador(valor)) {
			
			numero1 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor)));
			numero2 = Double.parseDouble(proximoValor.getLexeme());
			
		} else if (tokenIsIdentificador(proximoValor)) {

			numero1 = Double.parseDouble(valor.getLexeme());
			numero2 = Double.parseDouble(realizarOperacoesVariavel(findVariable(valor)));
			
		} else {
			throw new SemanticError("Erro ao obter o valor da expressão booleana. O operador > deve ser usado apenas com números.");
		}
		
		return new Pair<>(numero1, numero2);
	}

	private boolean tokenIsNumber(Token token) {
		return token.getId() == Constants.t_numeroDecimal || token.getId() == Constants.t_numeroInteiro;
	}
	
	private boolean tokenIsString(Token token) {
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
		return numbers.getKey() < numbers.getValue();
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
			
		} else if (tokenIsString(valor) && tokenIsString(proximoValor)) {
			
			resultado = valor.getLexeme().replace("\"", "").equals(proximoValor.getLexeme().replace("\"", ""));
			
		} else if (tokenIsIdentificador(valor) && tokenIsIdentificador(proximoValor)) {
			
			String valorVariavel1 = realizarOperacoesVariavel(findVariable(valor));
			String valorVariavel2 = realizarOperacoesVariavel(findVariable(proximoValor));

			resultado = valorVariavel1.equals(valorVariavel2);
		} else if (tokenIsIdentificador(valor)) {
			
			String valorVariavel = realizarOperacoesVariavel(findVariable(valor));
			
			resultado = valorVariavel.equals(proximoValor.getLexeme().replace("\"", ""));
			
		} else if (tokenIsIdentificador(proximoValor)) {
			
			String valorVariavel = realizarOperacoesVariavel(findVariable(proximoValor));
			
			resultado = valorVariavel.equals(valor.getLexeme().replace("\"", ""));
		} else {
			throw new SemanticError("Erro ao obter o valor da expressão booleana. O operador == deve ser usado apenas com números ou strings.");
		}
		
		return resultado;
	}

	private Variavel findVariable(Token tokenIdentifier) throws SemanticError {
		Optional<Variavel> variavel = this.variaveis.stream()
													.filter(v -> v.getIdentificador().getLexeme().equals(tokenIdentifier.getLexeme()))
												    .findFirst();
		
		if (!variavel.isPresent()) {
			throw new SemanticError("A variável %s não foi declarada".formatted(tokenIdentifier.getLexeme()));
		}
		
		return variavel.get();
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
				
			} else if (valor.getId() == Constants.t_identificador) {
				validarTipoVariavel(valor, Constants.t_int, "int");
				
				Variavel variable = findVariable(valor);
				String resultadoVariavel = realizarOperacoesVariavel(variable);
				
				if (resultado == null) {
					resultado = Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_3) {
					resultado -= Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_5) {
					resultado /= Integer.parseInt(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_6) {
					resultado *= Integer.parseInt(resultadoVariavel);
				} else {
					throw new SemanticError("A operação não é suportada com o tipo int.");
				}
				
			} else {
				idOperacao = valor.getId();
			}
		}

		return resultado.toString();
	}

	private void validarTipoVariavel(Token token, int tipo, String tipoRequerido) throws SemanticError {
		Variavel variavel = findVariable(token);
		
		if (variavel.getTipo().getId() != tipo) {
			throw new SemanticError("A variável %s deve ser do tipo %s para realizar a operação.".formatted(variavel.getIdentificador().getLexeme(), tipoRequerido));
		}
	}

	private String realizarOperacoesDecimais(Variavel variavel) throws SemanticError {
		Double resultado = null;
		Integer idOperacao = null;

		for (Token valor : variavel.getValores()) {
			if (tokenIsNumber(valor)) {
				
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

			} else if (tokenIsIdentificador(valor)) {
				try {
					validarTipoVariavel(valor, Constants.t_int, "int");
				} catch (SemanticError e) {
					validarTipoVariavel(valor, Constants.t_double, "double");
				}

				Variavel variable = findVariable(valor);
				String resultadoVariavel = realizarOperacoesVariavel(variable);
				
				if (resultado == null) {
					resultado = Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_3) {
					resultado -= Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_5) {
					resultado /= Double.parseDouble(resultadoVariavel);
				} else if (idOperacao == Constants.t_TOKEN_6) {
					resultado *= Double.parseDouble(resultadoVariavel);
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
			if (tokenIsString(valor)) {
				
				if (resultado == null) {
					resultado = valor.getLexeme().replace("\"", "");
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += valor.getLexeme().replace("\"", "");
				} else {
					throw new SemanticError("A operação não é suportada com o tipo string.");
				}
				
			} else if (valor.getId() == Constants.t_identificador) {
				validarTipoVariavel(valor, Constants.t_string, "string");
				
				Variavel variable = findVariable(valor);
				String resultadoVariavel = realizarOperacoesVariavel(variable);
				
				if (resultado == null) {
					resultado = resultadoVariavel.replace("\"", "");
				} else if (idOperacao == Constants.t_TOKEN_2) {
					resultado += resultadoVariavel.replace("\"", "");
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
		if (token.getId() == Constants.t_identificador) {
			Variavel variavel = findVariable(token);

			if (variavel.getTipo().getId() != Constants.t_string) {
				throw new SemanticError("Tipo incompatível na posição %s. A variável %s deve ser do tipo string.".formatted(token.getId(), 
																															variavel.getIdentificador().getLexeme()));
			}
		} else if (token.getId() != Constants.t_texto) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	private void validarTipoDouble(Token token, String lexemeValor) throws SemanticError {
		try {
			if (token.getId() == Constants.t_identificador) {
				Double.parseDouble(realizarOperacoesDecimais(findVariable(token)));
			} else {
				Double.parseDouble(lexemeValor);
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}

	private void validarTipoInteiro(Token token, String lexemeValor) throws SemanticError {
		try {
			if (token.getId() == Constants.t_identificador) {
				Integer.parseInt(realizarOperacoesInteiros(findVariable(token)));
			} else {
				Integer.parseInt(lexemeValor);
			}
		} catch (NumberFormatException e) {
			throw new SemanticError("Tipo incompatível na posição " + token.getPosition());
		}
	}
}
