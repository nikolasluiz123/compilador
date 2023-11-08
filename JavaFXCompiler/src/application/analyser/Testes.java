package application.analyser;

import application.reader.FileBufferedReader;

public class Testes {

	public static void main(String[] args) {
		try {
			FileBufferedReader reader = new FileBufferedReader("codigo_fonte.txt");
			String codigo = reader.readFile();

			Lexico lexico = new Lexico(codigo);
			Sintatico sintatico = new Sintatico();
			sintatico.parse(lexico, new Semantico());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
