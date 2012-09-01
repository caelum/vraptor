package br.com.caelum.vraptor.validator;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.method.MethodValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockValidator;

/**
 * Test method validator feature.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5
 */
public class MethodValidatorTest {

    @Mock private Localization l10n;
    @Mock private InterceptorStack stack;

    private MethodValidatorInterceptor interceptor;
    private ParameterNameProvider provider;
    private Validator validator;
    private MethodValidator methodValidator;
    private MessageInterpolator interpolator;
    
	private ResourceMethod withConstraint;
	private ResourceMethod withTwoConstraints;
	private ResourceMethod withoutConstraint;
	private ResourceMethod cascadeConstraint;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        Locale.setDefault(Locale.ENGLISH);

        ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
        creator.buildFactory();

        MethodValidatorCreator methodValidatorCreator = new MethodValidatorCreator();
        methodValidatorCreator.init();
        methodValidator = methodValidatorCreator.getInstance();

        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
        interpolatorFactory.createInterpolator();
        interpolator = interpolatorFactory.getInstance();

        provider = new ParanamerNameProvider();
        validator = new MockValidator();
        
        withConstraint = DefaultResourceMethod.instanceFor(MyController.class, MyController.class.getMethod("withConstraint", String.class));
        withTwoConstraints = DefaultResourceMethod.instanceFor(MyController.class, MyController.class.getMethod("withTwoConstraints", String.class, Customer.class));
        withoutConstraint = DefaultResourceMethod.instanceFor(MyController.class, MyController.class.getMethod("withoutConstraint", String.class));
        cascadeConstraint = DefaultResourceMethod.instanceFor(MyController.class, MyController.class.getMethod("cascadeConstraint", Customer.class));
    }
    
    @Test
    public void shouldAcceptIfMethodHasConstraint() {
        interceptor = new MethodValidatorInterceptor(null, null, null, null, null, null);
    	assertThat(interceptor.accepts(withConstraint), is(true));
    	
        interceptor = new MethodValidatorInterceptor(null, null, null, null, null, null);
    	assertThat(interceptor.accepts(withTwoConstraints), is(true));
    	
        interceptor = new MethodValidatorInterceptor(null, null, null, null, null, null);
    	assertThat(interceptor.accepts(cascadeConstraint), is(true));
    }

    @Test
    public void shouldNotAcceptIfMethodHasConstraint() {
        interceptor = new MethodValidatorInterceptor(null, null, null, null, null, null);
    	assertThat(interceptor.accepts(withoutConstraint), is(false));
    }

    @Test
    public void shouldValidateMethodWithConstraint()
        throws Exception {
        MethodInfo info = new DefaultMethodInfo();
        info.setParameters(new Object[] { null });
        info.setResourceMethod(withConstraint);

        interceptor = new MethodValidatorInterceptor(provider, l10n, interpolator, validator, info, methodValidator);
        when(l10n.getLocale()).thenReturn(new Locale("pt", "br"));

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getResourceMethod(), controller);

        assertThat(validator.getErrors(), is(hasSize(1)));
    }

    @Test
    public void shouldUseDefaultLocale()
        throws Exception {
        MethodInfo info = new DefaultMethodInfo();
        info.setParameters(new Object[] { null });
        info.setResourceMethod(withConstraint);

        interceptor = new MethodValidatorInterceptor(provider, l10n, interpolator, validator, info, methodValidator);

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getResourceMethod(), controller);

        assertThat(validator.getErrors(), not(hasSize(0)));
    }

    @Test
    public void shouldValidateMethodWithTwoConstraints()
        throws Exception {
        MethodInfo info = new DefaultMethodInfo();
        info.setParameters(new Object[] { null, new Customer(null, null) });
        info.setResourceMethod(withTwoConstraints);

        interceptor = new MethodValidatorInterceptor(provider, l10n, interpolator, validator, info, methodValidator);
        when(l10n.getLocale()).thenReturn(new Locale("pt", "br"));

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getResourceMethod(), controller);

        assertThat(validator.getErrors(), not(hasSize(0)));
    }
    
    /**
     * Customer for using in bean validator tests.
     */
    public class Customer {

        @NotNull public Integer id;

        @NotNull public String name;

        public Customer(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public class MyController {

        public void withConstraint(@NotNull String email) {

        }

        public void withTwoConstraints(@NotNull String name, @Valid Customer customer) {

        }
        
        public void withoutConstraint(@Foo String foo) {
        	
        }
        
        public void cascadeConstraint(@Valid Customer customer) {

        }
    }
    
	@Target(value = { PARAMETER })
	@Retention(value = RUNTIME)
	@Documented
    public @interface Foo {
    	
    }
}
