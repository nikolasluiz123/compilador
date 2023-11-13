package br.com.java.compiler.error;

public class LexicalError extends AnalysisError {
	
	private static final long serialVersionUID = 653393340745513742L;

	public LexicalError(String msg, int position) {
		super(msg, position);
	}

	public LexicalError(String msg) {
		super(msg);
	}
}
