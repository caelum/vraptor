package br.com.caelum.vraptor.http.ognl;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ContainerBasedNullHandlerTest {
    
    private ContainerBasedNullHandler handler;

    @Before
    public void setup() {
        this.handler = new ContainerBasedNullHandler();
    }
    
    @Test
    public void shouldInstantiateAnObjectIfRequiredToSetAProperty() throws OgnlException {
        OgnlRuntime.setNullHandler(House.class, handler);
        House house = new House();
        Ognl.setValue("cat.name", house, "James");
        MatcherAssert.assertThat(house.getCat().getName(), Matchers.is(Matchers.equalTo("James")));
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
