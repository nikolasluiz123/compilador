package br.com.java.compiler.error;

public class SemanticError extends AnalysisError {
	
	private static final long serialVersionUID = -5586678691331004740L;

	public SemanticError(String msg, int position) {
		super(msg, position);
	}

	public SemanticError(String msg) {
		super(msg);
	}
}
