package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.http.ParameterNameProvider;

public class LogicAnnotationWithParanamerParameterNameProviderTest {

    class Component {
        @Logic(parameters = { "first" })
        public void annotated(String value) {
        }

        public void nonAnnotated(String value) {
        }
    }

    @Test
    public void shouldUseParametersWithAnnotation() throws SecurityException, NoSuchMethodException {
        String[] result = new LogicAnnotationWithParanamerParameterNameProvider().parameterNamesFor(Component.class
                .getMethod("annotated", String.class));
        assertThat(result, Matchers.arrayContaining("first"));
    }

    @Test
    public void shouldUseDelegateWithoutAnnotation() throws SecurityException, NoSuchMethodException {
        Mockery mockery = new Mockery();
        final ParameterNameProvider delegate = mockery.mock(ParameterNameProvider.class);
        mockery.checking(new Expectations() {
            {
                one(delegate).parameterNamesFor(Component.class.getMethod("nonAnnotated", String.class));
                will(returnValue(new String[] { "second" }));
            }
        });
        String[] result = new LogicAnnotationWithParanamerParameterNameProvider(delegate)
                .parameterNamesFor(Component.class.getMethod("nonAnnotated", String.class));
        assertThat(result, Matchers.arrayContaining("second"));
    }

}
