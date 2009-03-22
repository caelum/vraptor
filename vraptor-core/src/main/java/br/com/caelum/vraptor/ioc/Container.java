package br.com.caelum.vraptor.ioc;

import javax.servlet.ServletRequest;

public interface Container {

	<T> T withA(Class<T> type);

	void start();

	void stop();

	Request forRequest(ServletRequest request);

}
