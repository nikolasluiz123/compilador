package br.com.java.compiler.constant;

public interface MessageConstants {

	String MESSAGE_SYNTAX_ERROR_POST_TOKEN = "Ocorreu um erro de syntax, verifique o c�digo digitado ap�s o token %s";
	String MESSAGE_SYNTAX_ERROR_IN_TOKEN = "Ocorreu um erro de syntax no token %s";

	String MESSAGE_IDENTIFIER_DECLARED = "O identificador %s j� foi declarado.";
	
	String MESSAGE_ACTION_NOT_EXPECTED = "A a��o da gram�tica n�o foi tratada.";
	
	String MESSAGE_INVALID_BOOLEAN_OPERATOR_ERROR = "Erro ao obter o valor da express�o booleana. Operador booleano inv�lido ou n�o tratado.";

	String MESSAGE_EQUAILS_OPERATOR_WITH_INVALID_TYPE_ERROR = "Erro ao obter o valor da express�o booleana. O operador == deve ser usado apenas com n�meros ou strings.";
	String MESSAGE_GREATER_THAN_OPERATOR_WITH_INVALID_TYPE_ERROR = "Erro ao obter o valor da express�o booleana. O operador > deve ser usado apenas com n�meros.";
}
