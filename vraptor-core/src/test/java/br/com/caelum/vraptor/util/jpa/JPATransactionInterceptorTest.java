package br.com.caelum.vraptor.util.jpa;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class JPATransactionInterceptorTest {

    @Mock private EntityManager entityManager;
    @Mock private InterceptorStack stack;
    @Mock private ResourceMethod method;
    @Mock private EntityTransaction transaction;
    private Object instance;
	@Mock private Validator validator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldStartAndCommitTransaction() throws Exception {
        JPATransactionInterceptor interceptor = new JPATransactionInterceptor(entityManager, validator);

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);

        interceptor.intercept(stack, method, instance);

        InOrder callOrder = inOrder(entityManager, transaction, stack);
        callOrder.verify(entityManager).getTransaction();
        callOrder.verify(transaction).begin();
        callOrder.verify(stack).next(method, instance);
        callOrder.verify(transaction).commit();
    }

    @Test
    public void shouldRollbackTransactionIfStillActiveWhenExecutionFinishes() throws Exception {
        JPATransactionInterceptor interceptor = new JPATransactionInterceptor(entityManager, validator);

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);

        interceptor.intercept(stack, method, instance);

        verify(transaction).rollback();
    }

}
