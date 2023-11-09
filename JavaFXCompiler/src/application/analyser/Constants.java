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
    int t_TOKEN_9 = 9; //"!"
    int t_TOKEN_10 = 10; //">"
    int t_TOKEN_11 = 11; //"<"
    int t_TOKEN_12 = 12; //"=="
    int t_TOKEN_13 = 13; //">="
    int t_TOKEN_14 = 14; //"<="
    int t_TOKEN_15 = 15; //"("
    int t_TOKEN_16 = 16; //")"
    int t_TOKEN_17 = 17; //"{"
    int t_TOKEN_18 = 18; //"}"
    int t_identificador = 19;
    int t_numeroInteiro = 20;
    int t_numeroDecimal = 21;
    int t_texto = 22;
    int t_if = 23;
    int t_else = 24;
    int t_switch = 25;
    int t_for = 26;
    int t_while = 27;
    int t_do = 28;
    int t_int = 29;
    int t_double = 30;
    int t_string = 31;
    int t_boolean = 32;
    int t_true = 33;
    int t_false = 34;
    int t_class = 35;
    int t_public = 36;
    int t_protected = 37;
    int t_private = 38;
    int t_void = 39;
    int t_static = 40;
    int t_writeln = 41;

}
