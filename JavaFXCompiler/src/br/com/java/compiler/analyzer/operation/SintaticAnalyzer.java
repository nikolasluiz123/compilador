package br.com.java.compiler.analyzer.operation;

import java.util.Stack;

import br.com.java.compiler.analyzer.representation.Token;
import br.com.java.compiler.constant.Constants;
import br.com.java.compiler.constant.MessageConstants;
import br.com.java.compiler.error.AnalysisError;
import br.com.java.compiler.error.SyntaticError;

/**
 * Classe responsável pela análise sintática gerada pelo GALs.
 */
public class SintaticAnalyzer implements Constants {

	private Stack<Integer> stack = new Stack<>();
	private Token currentToken;
	private Token previousToken;
	private LexicalAnalyzer scanner;
	private SemanticAnalyzer semanticAnalyser;

	public void parse(LexicalAnalyzer scanner, SemanticAnalyzer semanticAnalyser) throws AnalysisError {
		this.scanner = scanner;
		this.semanticAnalyser = semanticAnalyser;

		stack.clear();
		stack.push(0);

		currentToken = scanner.nextToken();

		while (!step())
			;
	}

	private boolean step() throws AnalysisError {
		if (currentToken == null) {
			int pos = 0;
			if (previousToken != null)
				pos = previousToken.getPosition() + previousToken.getLexeme().length();

			currentToken = new Token(DOLLAR, "$", pos);
		}

		int token = currentToken.getId();
		int state = ((Integer) stack.peek()).intValue();

		int[] cmd = PARSER_TABLE[state][token - 1];

		switch (cmd[0]) {
		case SHIFT:
			stack.push(cmd[1]);
			previousToken = currentToken;
			currentToken = scanner.nextToken();
			return false;

		case REDUCE:
			int[] prod = PRODUCTIONS[cmd[1]];

			for (int i = 0; i < prod[1]; i++)
				stack.pop();

			int oldState = ((Integer) stack.peek()).intValue();
			stack.push(PARSER_TABLE[oldState][prod[0] - 1][1]);
			return false;

		case ACTION:
			int action = FIRST_SEMANTIC_ACTION + cmd[1] - 1;
			stack.push(PARSER_TABLE[state][action][1]);
			semanticAnalyser.executeAction(cmd[1], previousToken);
			return false;

		case ACCEPT:
			return true;

		case ERROR:
			if (previousToken != null) {
				throw new SyntaticError(MessageConstants.MESSAGE_SYNTAX_ERROR_POST_TOKEN.formatted(previousToken.getLexeme()));
			} else {
				throw new SyntaticError(MessageConstants.MESSAGE_SYNTAX_ERROR_IN_TOKEN.formatted(currentToken.getLexeme()));
			}
		}
		return false;
	}
}
