package br.com.caelum.vraptor.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class Validations {

    private final List<String> errors = new ArrayList<String>();
    private Object lastUsed;

    public <T> void that(T id, Matcher<T> matcher) {
        that(null, id, matcher);
    }

    public <T> void that(String reason, T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            if (reason != null) {
                errors.add(reason);
            } else {
                Description description = new StringDescription();
                description.appendDescriptionOf(matcher);
                errors.add(description.toString());
            }
        }
    }

    public void that(String reason, boolean assertion) {
        if (!assertion) {
            errors.add(reason);
        }
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void and(List<String> errors) {
        this.errors.addAll(errors);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T that(T instance) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance.getClass());
        enhancer.setCallback(new MethodInterceptor(){

            public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return null;
            }
            
        });
        return used((T) enhancer.create());
    }
    public <T> T used(T object) {
        this.lastUsed = object;
        return object;
    }
    
    public <T> void shouldBe(Matcher<T> matcher) {
        
    }

}
