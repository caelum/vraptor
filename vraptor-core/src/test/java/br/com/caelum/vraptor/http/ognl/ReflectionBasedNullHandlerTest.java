package br.com.caelum.vraptor.http.ognl;

import java.util.List;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ReflectionBasedNullHandlerTest {
    
    private ReflectionBasedNullHandler handler;

    @Before
    public void setup() {
        this.handler = new ReflectionBasedNullHandler();
    }
    
    @Test
    public void shouldInstantiateAnObjectIfRequiredToSetAProperty() throws OgnlException {
        OgnlRuntime.setNullHandler(House.class, handler);
        House house = new House();
        Ognl.setValue("cat.name", house, "James");
        MatcherAssert.assertThat(house.getCat().getName(), Matchers.is(Matchers.equalTo("James")));
    }
    @Test
    public void shouldInstantiateAListOfStrings() throws OgnlException {
        OgnlRuntime.setNullHandler(House.class, handler);
        OgnlRuntime.setNullHandler(Cat.class, handler);
        House house = new House();
        Ognl.setValue("cat.eyeColors[0]", house, "Blue");
        Ognl.setValue("cat.eyeColors[1]", house, "Green");
        MatcherAssert.assertThat(house.getCat().getEyeColors().get(0), Matchers.is(Matchers.equalTo("Blue")));
        MatcherAssert.assertThat(house.getCat().getEyeColors().get(1), Matchers.is(Matchers.equalTo("Green")));
    }

    public static class House {
        private Cat cat;

        public void setCat(Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }
        
    }
    

}
class Cat{
    private String name;
    
    private List<String> eyeColors;

    private int[] legSize;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEyeColors(List<String> eyeColors) {
        this.eyeColors = eyeColors;
    }

    public List<String> getEyeColors() {
        return eyeColors;
    }

    public void setLegSize(int[] legSize) {
        this.legSize = legSize;
    }

    public int[] getLegSize() {
        return legSize;
    }
}
