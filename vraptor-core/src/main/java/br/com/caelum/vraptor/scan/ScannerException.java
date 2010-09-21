package br.com.caelum.vraptor.scan;

/**
 * Unchecked exception used in the scanner package
 * 
 * @author Sérgio Lopes
 */
public class ScannerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ScannerException(String message) {
		super(message);
	}

	public ScannerException(String message, Throwable t) {
		super(message, t);
	}
}
