package application.analyser;

public interface Constants extends ScannerConstants, ParserConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int t_TOKEN_2 = 2; //"+"
    int t_TOKEN_3 = 3; //"-"
    int t_TOKEN_4 = 4; //"="
    int t_TOKEN_5 = 5; //"/"
    int t_TOKEN_6 = 6; //"*"
    int t_TOKEN_7 = 7; //"&&"
    int t_TOKEN_8 = 8; //"||"
    int t_TOKEN_9 = 9; //">"
    int t_TOKEN_10 = 10; //"<"
    int t_TOKEN_11 = 11; //"=="
    int t_TOKEN_12 = 12; //"("
    int t_TOKEN_13 = 13; //")"
    int t_TOKEN_14 = 14; //"{"
    int t_TOKEN_15 = 15; //"}"
    int t_identificador = 16;
    int t_numeroInteiro = 17;
    int t_numeroDecimal = 18;
    int t_texto = 19;
    int t_if = 20;
    int t_else = 21;
    int t_switch = 22;
    int t_for = 23;
    int t_while = 24;
    int t_do = 25;
    int t_int = 26;
    int t_double = 27;
    int t_string = 28;
    int t_boolean = 29;
    int t_true = 30;
    int t_false = 31;
    int t_class = 32;
    int t_public = 33;
    int t_protected = 34;
    int t_private = 35;
    int t_void = 36;
    int t_static = 37;

}
