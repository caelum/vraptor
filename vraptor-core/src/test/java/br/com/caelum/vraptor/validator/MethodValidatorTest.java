package br.com.caelum.vraptor.validator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.MessageInterpolator;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.method.MethodValidator;
import org.junit.Assert;
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
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.util.test.MockValidator;

/**
 * A simple class to test JSR303Validator and HibernateValidator3 components.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
public class MethodValidatorTest {

    @Mock
    private Localization localization;
    @Mock
    private InterceptorStack stack;

    private MethodValidatorInterceptor interceptor;
    private ParameterNameProvider provider;
    private Validator validator;
    private MethodValidator methodValidator;
    private MessageInterpolator interpolator;

    @Before
    public void setup() {
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
    }

    @Test
    public void validateM0()
        throws Exception {
        Method m0 = MyController.class.getMethod("m0", String.class);

        MethodInfo info = new DefaultMethodInfo();
        info.setParameters(new Object[] { null });
        info.setResourceMethod(new DefaultResourceMethod(new DefaultResourceClass(MyController.class), m0));

        interceptor = new MethodValidatorInterceptor(provider, localization, interpolator, validator, info,
                methodValidator);

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getResourceMethod(), controller);

        Assert.assertTrue(validator.getErrors().size() == 1);
        Set<String> messages = extractMessages(validator.getErrors());
        Assert.assertTrue(messages.contains("may not be null"));
    }

    @Test
    public void validateM1()
        throws Exception {
        Method m1 = MyController.class.getMethod("m1", String.class, Customer.class);

        MethodInfo info = new DefaultMethodInfo();
        info.setParameters(new Object[] { null, new Customer(null, null) });
        info.setResourceMethod(new DefaultResourceMethod(new DefaultResourceClass(MyController.class), m1));

        interceptor = new MethodValidatorInterceptor(provider, localization, interpolator, validator, info,
                methodValidator);

        MyController controller = new MyController();
        interceptor.intercept(stack, info.getResourceMethod(), controller);

        Assert.assertTrue(validator.getErrors().size() == 3);
        Set<String> messages = extractMessages(validator.getErrors());
        Assert.assertTrue(messages.contains("may not be null"));
    }

    private Set<String> extractMessages(List<Message> messages) {
        Set<String> out = new HashSet<String>();
        for (Message m : messages) {
            out.add(m.getMessage());
        }
        return out;
    }

    /**
     * Customer for using in bean validator tests.
     */
    public class Customer {

        @javax.validation.constraints.NotNull
        public Integer id;

        @javax.validation.constraints.NotNull
        public String name;

        public Customer(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public class MyController {

        public void m0(@NotNull String email) {

        }

        public void m1(@NotNull String email, @NotNull @Valid Customer customer) {

        }
    }
}
