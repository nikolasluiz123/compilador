package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import application.analyser.Lexico;
import application.analyser.Semantico;
import application.analyser.Sintatico;
import application.reader.FileBufferedReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private TextArea textAreaCodigo;

	@FXML
	private TextArea textAreaConsole;

	private File lastDirectorySaved;

	@FXML
	private void handleMenuSairClick(ActionEvent event) {
		getStageFromMenuItemActionEvent(event).close();
	}

	@FXML
	private void handleMenuImportarClick(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Selecione um arquivo de texto");

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos de Texto (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);

		File selectedFile = fileChooser.showOpenDialog(getStageFromMenuItemActionEvent(event));
		FileBufferedReader reader = new FileBufferedReader(selectedFile.getAbsolutePath());

		try {
			this.textAreaCodigo.setText(reader.readFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleMenuExecutarClick(ActionEvent event) {
		try {
			Lexico lexico = new Lexico(this.textAreaCodigo.getText());
			Sintatico sintatico = new Sintatico();
			sintatico.parse(lexico, new Semantico());
		} catch (Exception e) {
			this.textAreaConsole.setText(e.getMessage());
		}
	}

	@FXML
	private void handleMenuSalvar(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salvar Arquivo");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt"));

		if (this.lastDirectorySaved != null) {
			fileChooser.setInitialDirectory(this.lastDirectorySaved);
		}

		File file = fileChooser.showSaveDialog(getStageFromMenuItemActionEvent(event));

		if (file != null) {
			this.lastDirectorySaved = file.getParentFile();

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(this.textAreaCodigo.getText());
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleMenuNovo(ActionEvent event) {
		this.lastDirectorySaved = null;
		this.textAreaCodigo.setText("");
		this.textAreaCodigo.setText("");
	}

	private Stage getStageFromMenuItemActionEvent(ActionEvent event) {
		return ((Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());
	}
}
