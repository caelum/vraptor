package br.com.caelum.vraptor.reflection;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.TypeCreator;
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
        this.creator = new CacheBasedTypeCreator(delegate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseTheDelegatedInstance() {
        mockery.checking(new Expectations() {
            {
                one(delegate).typeFor(method); will(returnValue(String.class));
            }
        });
        MatcherAssert.assertThat((Class<String>)creator.typeFor(method), Matchers.is(Matchers.equalTo(String.class)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameInstanceIfRequiredTwice() {
        mockery.checking(new Expectations() {
            {
                one(delegate).typeFor(method); will(returnValue(String.class));
            }
        });
        Class<?> firstResult = creator.typeFor(method);
        Class<?> secondResult = creator.typeFor(method);
        Assert.assertEquals(firstResult, secondResult);
        mockery.assertIsSatisfied();
    }

}
