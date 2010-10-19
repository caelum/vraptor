package br.com.caelum.vraptor.core;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static br.com.caelum.vraptor.view.Results.status;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

/**
 * An abstract result that implements all shortcut methods in the
 * recommended way
 *
 * @author Lucas Cavalcanti
 * @since 3.1.2
 */
public abstract class AbstractResult implements Result {

	public void forwardTo(String uri) {
		use(page()).forwardTo(uri);
	}

	public void redirectTo(String uri) {
		use(page()).redirectTo(uri);
	}

	public <T> T forwardTo(Class<T> controller) {
		return use(logic()).forwardTo(controller);
	}

	public <T> T redirectTo(Class<T> controller) {
		return use(logic()).redirectTo(controller);
	}

	public <T> T of(Class<T> controller) {
		return use(page()).of(controller);
	}

	@SuppressWarnings("unchecked")
	public <T> T redirectTo(T controller) {
		return (T) redirectTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T forwardTo(T controller) {
		return (T) forwardTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T of(T controller) {
		return (T) of(controller.getClass());
	}

	public void nothing() {
		use(Results.nothing());
	}

	public void notFound() {
		use(status()).notFound();
	}

	public void permanentlyRedirectTo(String uri) {
		use(status()).movedPermanentlyTo(uri);
	}

	public <T> T permanentlyRedirectTo(Class<T> controller) {
		return use(status()).movedPermanentlyTo(controller);
	}

	@SuppressWarnings("unchecked")
	public <T> T permanentlyRedirectTo(T controller) {
		return (T) use(status()).movedPermanentlyTo(controller.getClass());
	}

}