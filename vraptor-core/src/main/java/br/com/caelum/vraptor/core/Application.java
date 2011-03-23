package br.com.caelum.vraptor.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Vraptor application context independent of a filter or servlet.
 * 
 * @author guilherme silveira
 * @author jose donizetti
 */
public interface Application {

	/**
	 * Stops this vraptor application
	 */
	public abstract void stop();

	/**
	 * Processes a request
	 */
	public abstract void parse(final HttpServletRequest baseRequest,
			final HttpServletResponse baseResponse, FilterChain chain)
			throws IOException, ServletException;

	/**
	 * Restarts this application
	 */
	public abstract void restart();

	/**
	 * Starts the application
	 */
	public abstract void start();

}