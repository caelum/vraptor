
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Default page result implementation.
 *
 * @author Guilherme Silveira
 */
public class ValidationPageResult implements PageResult {


    private final List<Message> errors;
	private final PageResult delegate;
	private final Proxifier proxifier;

	public ValidationPageResult(PageResult delegate, Proxifier proxifier, List<Message> errors) {
		this.proxifier = proxifier;
		this.delegate = delegate;
		this.errors = errors;
    }

    public void forward() {
    	delegate.forward();
    	throwException();
    }

	private void throwException() throws ValidationError {
		throw new ValidationError(errors);
	}

    public void include() {
    	delegate.include();
    	throwException();
    }

    public void redirect(String url) {
    	delegate.redirect(url);
    	throwException();
    }

	public void forward(String url) {
        delegate.forward(url);
        throwException();
	}

	public <T> T of(final Class<T> controllerType) {
		return proxifier.proxify(controllerType, new MethodInvocation<T>() {
            public T intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
            	try {
					method.invoke(delegate.of(controllerType), args);
				} catch (Exception e) {
					throw new ResultException(e);
				}
            	throw new ValidationError(errors);
            }
        });
	}

}
