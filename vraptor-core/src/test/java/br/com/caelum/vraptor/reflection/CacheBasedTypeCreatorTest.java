package br.com.caelum.vraptor.reflection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class CacheBasedTypeCreatorTest {

    private Mockery mockery;
    private ResourceMethod method;
    private CacheBasedTypeCreator creator;
    private TypeCreator delegate;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.delegate = mockery.mock(TypeCreator.class);
        this.creator = new CacheBasedTypeCreator(delegate, mockery.mock(ParameterNameProvider.class));

		mockery.checking(new Expectations() {
			{
				allowing(method).getMethod();
				will(returnValue(TypeCreator.class.getMethods()[0]));
			}
		});
    }

    @Test
    public void shouldUseTheSameInstanceIfRequiredTwice() {
        mockery.checking(new Expectations() {
            {
                one(delegate).typeFor(method); will(returnValue(String.class));
            }
        });
        Class<?> firstResult = creator.typeFor(method);
        Class<?> secondResult = creator.typeFor(DefaultResourceMethod.instanceFor(TypeCreator.class, TypeCreator.class.getMethods()[0]));
        Assert.assertEquals(firstResult, secondResult);
        mockery.assertIsSatisfied();
    }

}
