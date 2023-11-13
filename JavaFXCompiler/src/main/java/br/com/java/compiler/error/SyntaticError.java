package br.com.java.compiler.error;

public class SyntaticError extends AnalysisError {
	
	private static final long serialVersionUID = 8448525522200506427L;

	public SyntaticError(String msg, int position) {
		super(msg, position);
	}

	public SyntaticError(String msg) {
		super(msg);
	}
}
