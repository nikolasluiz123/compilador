package br.com.java.compiler.analyzer.operation;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.error.LexicalError;

/**
 * Classe responsável pela análise léxica gerada pelo GALs.
 */
public class LexicalAnalyzer implements Constants {
	
	private int position;
	private String input;

	public LexicalAnalyzer() {
		this("");
	}

	public LexicalAnalyzer(String input) {
		setInput(input);
	}

	/**
	 * Método responsável por retornar o próximo Token
	 * previamente definido.
	 * 
	 * @throws LexicalError
	 */
	public Token nextToken() throws LexicalError {
		if (!hasInput())
			return null;

		int start = position;

		int state = 0;
		int lastState = 0;
		int endState = -1;
		int end = -1;

		while (hasInput()) {
			lastState = state;
			state = nextState(nextChar(), state);

			if (state < 0)
				break;

			else {
				if (tokenForState(state) >= 0) {
					endState = state;
					end = position;
				}
			}
		}
		if (endState < 0 || tokenForState(lastState) == -2)
			throw new LexicalError(SCANNER_ERROR[lastState], start);

		position = end;

		int token = tokenForState(endState);

		if (token == 0)
			return nextToken();
		else {
			String lexeme = input.substring(start, end);
			token = lookupToken(token, lexeme);
			return new Token(token, lexeme, start);
		}
	}

	/**
	 * Método responsável por retornar o próximo estado
	 * do autômato.
	 * 
	 * @param c Caractere que deve levar a algum estado do autômato.
	 * @param state Estado anterior.
	 */
	private int nextState(char c, int state) {
		int next = SCANNER_TABLE[state][c];
		return next;
	}

	private int tokenForState(int state) {
		if (state < 0 || state >= TOKEN_STATE.length)
			return -1;

		return TOKEN_STATE[state];
	}

	/**
	 * Método que realiza uma pesquisa binária procurando
	 * pelo token.
	 * 
	 * @param base
	 * @param key
	 * 
	 */
	public int lookupToken(int base, String key) {
		int start = SPECIAL_CASES_INDEXES[base];
		int end = SPECIAL_CASES_INDEXES[base + 1] - 1;

		while (start <= end) {
			int half = (start + end) / 2;
			int comp = SPECIAL_CASES_KEYS[half].compareTo(key);

			if (comp == 0)
				return SPECIAL_CASES_VALUES[half];
			else if (comp < 0)
				start = half + 1;
			else
				end = half - 1;
		}

		return base;
	}
	
	public void setInput(String input) {
		this.input = input;
		setPosition(0);
	}

	public void setPosition(int pos) {
		position = pos;
	}

	private boolean hasInput() {
		return position < input.length();
	}

	private char nextChar() {
		if (hasInput())
			return input.charAt(position++);
		else
			return (char) -1;
	}
}
