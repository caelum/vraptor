package br.com.caelum.vraptor.converter;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class CachedConvertersTest {

    private Mockery mockery;
    private CachedConverters converters;
    private Converters delegate;
    private Converter converter;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(Converters.class);
        this.converter = mockery.mock(Converter.class);
        this.converters = new CachedConverters(delegate);
        this.container = mockery.mock(Container.class);
        mockery.checking(new Expectations() {
            {
                one(delegate).to(CachedConvertersTest.class, container); will(returnValue(converter));
            }
        });
    }

    @Test
    public void shouldUseTheProvidedConverterDuringFirstRequest() {
        Converter found = converters.to(CachedConvertersTest.class, container);
        MatcherAssert.assertThat(found, Matchers.is(Matchers.equalTo(this.converter)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameConverterOnFurtherRequests() {
        mockery.checking(new Expectations(){{
            one(container).register(converter.getClass());
            one(container).instanceFor(converter.getClass()); will(returnValue(converter));
        }});
        Converter found = converters.to(CachedConvertersTest.class, container);
        MatcherAssert.assertThat(converters.to(CachedConvertersTest.class, container), Matchers.is(Matchers.equalTo(found)));
        mockery.assertIsSatisfied();
    }

}
