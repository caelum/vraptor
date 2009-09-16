package org.vraptor.mydvds.interceptor;

import org.vraptor.mydvds.dao.DaoFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Intercepts
public class TransactionInterceptor implements Interceptor {

    private final DaoFactory daoFactory;

    /**
     * The DaoFactory will be injected.
     */
    public TransactionInterceptor(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Accepts all requests.
     */
    public boolean accepts(ResourceMethod method) {
        return true;
    }

    /**
     * Intercepts the request and starts a database transaction.
     */
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        try {
            // starts the transaction
            daoFactory.beginTransaction();

            // let the execution flow
            stack.next(method, resourceInstance);

            // commits the transaction
            daoFactory.flush();
            daoFactory.commit();
        } finally {
            if (daoFactory.hasTransaction()) {
                daoFactory.rollback();
            }
        }
    }
}