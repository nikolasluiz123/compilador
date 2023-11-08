package application;

import java.io.File;

import application.reader.FileBufferedReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private MenuItem menuSair;
	
	@FXML
	private TextArea textAreaCodigo;

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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Stage getStageFromMenuItemActionEvent(ActionEvent event) {
		return ((Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());
	}
}
