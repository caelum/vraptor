package br.com.caelum.vraptor.interceptor.download;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author filipesabella
 */
public interface Download {

	public abstract void write(HttpServletResponse response) throws IOException;

}