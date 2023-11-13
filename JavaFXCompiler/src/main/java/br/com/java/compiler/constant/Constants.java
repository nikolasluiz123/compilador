package br.com.java.compiler.constant;

public interface Constants extends ScannerConstants, ParserConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int TOKEN_SUM = 2;
    int TOKEN_SUBTRACTION = 3; 
    int TOKEN_ATRIBUITION = 4;
    int TOKEN_DIVISION = 5; 
    int TOKEN_MULTIPLICATION = 6;
    int TOKEN_AND = 7; 
    int TOKEN_OR = 8;
    int TOKEN_GREATER_THAN = 9; 
    int TOKEN_LESS_THAN = 10; 
    int TOKEN_EQUALS = 11;
    int TOKEN_GREATER_EQUALS = 12;
    int TOKEN_LESS_EQUALS = 13;
    int TOKEN_OPEN_PARENTHESIS = 14;
    int TOKEN_CLOSE_PARENTHESIS = 15;
    int TOKEN_OPEN_KEY = 16;
    int TOKEN_CLOSE_KEY = 17;
    int TOKEN_IDENTIFIER = 18;
    int TOKEN_INT_NUMBER = 19;
    int TOKEN_DOUBLE_NUMBER = 20;
    int TOKEN_STRING = 21;
    int TOKEN_IF = 22;
    int TOKEN_INT_TYPE = 23;
    int TOKEN_DOUBLE_TYPE = 24;
    int TOKEN_STRING_TYPE = 25;
    int TOKEN_BOOLEAN_TYPE = 26;
    int TOKEN_TRUE = 27;
    int TOKEN_FALSE = 28;
    int TOKEN_WRITELN = 29;

}
