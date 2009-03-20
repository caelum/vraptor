package br.com.caelum.vraptor.ioc;

public interface Container {

	<T> T withA(Class<T> type);

	void start();

	void stop();

}
