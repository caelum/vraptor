package br.com.caelum.vraptor.ioc;

public interface StereotypeHandler {
	public boolean canHandle(Class<?> type);
	public void handle(Class<?> Type);
}
