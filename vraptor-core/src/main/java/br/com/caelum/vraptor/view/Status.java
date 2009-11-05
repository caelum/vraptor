package br.com.caelum.vraptor.view;

import java.io.IOException;

/**
 * Allows typical HEADER only results.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Status {

	public void notFound();

	public void internalServerError(Throwable e) throws IOException;

}
