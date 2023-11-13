package br.com.java.compiler.constant;

public interface MessageConstants {

	String MESSAGE_SYNTAX_ERROR_POST_TOKEN = "Ocorreu um erro de syntax, verifique o código digitado após o token %s";
	String MESSAGE_SYNTAX_ERROR_IN_TOKEN = "Ocorreu um erro de syntax no token %s";

	String MESSAGE_IDENTIFIER_DECLARED = "O identificador %s já foi declarado.";
	
	String MESSAGE_ACTION_NOT_EXPECTED = "A ação da gramática não foi tratada.";
	
	String MESSAGE_INVALID_BOOLEAN_OPERATOR_ERROR = "Erro ao obter o valor da expressão booleana. Operador booleano inválido ou não tratado.";

	String MESSAGE_EQUAILS_OPERATOR_WITH_INVALID_TYPE_ERROR = "Erro ao obter o valor da expressão booleana. O operador == deve ser usado apenas com números ou strings.";
	String MESSAGE_GREATER_THAN_OPERATOR_WITH_INVALID_TYPE_ERROR = "Erro ao obter o valor da expressão booleana. O operador > deve ser usado apenas com números.";
}
