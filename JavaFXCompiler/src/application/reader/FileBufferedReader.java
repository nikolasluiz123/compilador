package application.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringJoiner;

/**
 * Classe para realizar a leitura de um arquivo utilizando
 * {@link BufferedReader}
 * 
 * @author Nikolas Luiz Schmitt
 *
 */
public class FileBufferedReader {

	private String fileName;

	public FileBufferedReader(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Método que realiza a leitura do arquivo e retorna uma lista com os números.
	 * 
	 * @throws Exception Lançará uma exceção ao falhar na conversão do número
	 *                   ou leitura do arquivo.
	 * 
	 * @author Nikolas Luiz Schmitt
	 *
	 */
	public String readFile() throws Exception {
		StringJoiner joiner = new StringJoiner("\r\n");
		FileReader fileReader = new FileReader(this.fileName);

		try (BufferedReader reader = new BufferedReader(fileReader)) {
			String line;

			while ((line = reader.readLine()) != null) {
				joiner.add(line);
			}
		} finally {
			fileReader.close();
		}

		return joiner.toString();
	}
}
