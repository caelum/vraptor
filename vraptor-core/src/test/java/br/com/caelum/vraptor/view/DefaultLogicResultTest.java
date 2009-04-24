package br.com.caelum.vraptor.view;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Container;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private MyComponent instance;
    private Container container;

    class MyComponent {
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.instance = new MyComponent();
        this.container = mockery.mock(Container.class);
        this.logicResult = new DefaultLogicResult(container);
    }

    @Test
    public void instantiatesUsingTheContainer() {
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyComponent.class); will(returnValue(instance));
            }
        });
        MyComponent component = logicResult.redirectTo(MyComponent.class);
        assertThat(component, is(equalTo(instance)));
        mockery.assertIsSatisfied();
    }

}
