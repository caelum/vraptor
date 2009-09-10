package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Validation implementation for Logic Result
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
public class ValidationLogicResult implements LogicResult {

	private final Proxifier proxifier;
	private final LogicResult delegate;
	private final List<Message> errors;

	public ValidationLogicResult(LogicResult delegate, Proxifier proxifier, List<Message> errors) {
		this.delegate = delegate;
		this.proxifier = proxifier;
		this.errors = errors;
	}
	public <T> T forwardTo(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				superMethod.invoke(delegate.forwardTo(type), args);
				throw new ValidationError(errors);
			}
		});
	}

	public <T> T redirectTo(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				superMethod.invoke(delegate.redirectTo(type), args);
				throw new ValidationError(errors);
			}
		});
	}

}
