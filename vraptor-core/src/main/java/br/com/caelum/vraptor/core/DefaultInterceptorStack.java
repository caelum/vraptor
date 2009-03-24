package br.com.caelum.vraptor.core;

import java.util.Iterator;
import java.util.LinkedList;

import br.com.caelum.vraptor.Interceptor;

public class DefaultInterceptorStack implements InterceptorStack {

	private final LinkedList<InterceptorHandler> interceptors = new LinkedList<InterceptorHandler>();

	/**
	 * we do not use an iterator so an interceptor can hack the code to add new
	 * interceptors on the fly
	 */
	private int nextInterceptor = 0;

	public void add(Interceptor interceptor) {
		this.interceptors.add(new InstantiatedInterceptorHandler(interceptor));
	}

	public void next() {
		if(nextInterceptor==interceptors.size()) {
			return;
		}
		InterceptorHandler handler = interceptors.get(nextInterceptor++);
		handler.execute(this);
	}

}
