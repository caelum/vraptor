package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.VRaptorException;

public class ExpressionEvaluationException extends VRaptorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6746527102486200787L;

	public ExpressionEvaluationException(String msg) {
		super(msg);
	}

	public ExpressionEvaluationException(String msg, Exception cause) {
		super(msg, cause);
	}

}
