module JavaFXCompiler {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	
	opens br.com.java.compiler.controller to javafx.graphics, javafx.fxml;
	opens br.com.java.compiler to javafx.graphics, javafx.fxml;
}
