package br.com.caelum.vraptor.http.ognl;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ReflectionBasedNullHandlerTest {
    
    private ReflectionBasedNullHandler handler;
    private OgnlContext context;

    @Before
    public void setup() {
        this.handler = new ReflectionBasedNullHandler();
        this.context = (OgnlContext) Ognl.createDefaultContext(null);
        context.setTraceEvaluations(true);
    }
    
    @Test
    public void shouldInstantiateAnObjectIfRequiredToSetAProperty() throws OgnlException {
        OgnlRuntime.setNullHandler(House.class, handler);
        House house = new House();
        Ognl.setValue("mouse.name",context, house, "James");
        MatcherAssert.assertThat(house.getMouse().getName(), Matchers.is(Matchers.equalTo("James")));
    }
    @Test
    public void shouldInstantiateAListOfStrings() throws OgnlException {
        OgnlRuntime.setNullHandler(House.class, handler);
        OgnlRuntime.setNullHandler(Mouse.class, handler);
        House house = new House();
        Ognl.setValue("mouse.eyeColors[0]",context, house, "Blue");
        Ognl.setValue("mouse.eyeColors[1]", context,house, "Green");
        MatcherAssert.assertThat(house.getMouse().getEyeColors().get(0), Matchers.is(Matchers.equalTo("Blue")));
        MatcherAssert.assertThat(house.getMouse().getEyeColors().get(1), Matchers.is(Matchers.equalTo("Green")));
    }

    public static class House {
        private Mouse mouse;

        public void setMouse(Mouse cat) {
            this.mouse = cat;
        }

        public Mouse getMouse() {
            return mouse;
        }
        
    }
    

}
