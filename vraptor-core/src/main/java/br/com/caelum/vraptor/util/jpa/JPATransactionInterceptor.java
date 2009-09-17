package br.com.caelum.vraptor.util.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An example of JPA Transaction management on VRaptor
 * @author Lucas Cavalcanti
 *
 */
@Intercepts
public class JPATransactionInterceptor implements Interceptor {

	private final EntityManager manager;

	public JPATransactionInterceptor(EntityManager manager) {
		this.manager = manager;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object instance) {
		EntityTransaction transaction = null;
		try {
			transaction = manager.getTransaction();
			stack.next(method, instance);
			transaction.commit();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	public boolean accepts(ResourceMethod method) {
		return true; // Will intercept all requests
	}
}
