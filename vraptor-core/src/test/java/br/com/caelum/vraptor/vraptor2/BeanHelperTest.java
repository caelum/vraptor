package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class BeanHelperTest {

    interface CustomType {
        boolean isValid();
        String getValue();
    }
    @Test
    public void canHandlePrimitiveBooleanGetter() throws SecurityException, NoSuchMethodException {
        assertThat(new BeanHelper().nameForGetter(CustomType.class.getMethod("isValid")), is(equalTo("valid")));
    }
    
    @Test
    public void canHandleCustomTypeGetter() throws SecurityException, NoSuchMethodException {
        assertThat(new BeanHelper().nameForGetter(CustomType.class.getMethod("getValue")), is(equalTo("value")));
    }
    
    @Test
    public void canDecapitalizeASingleCharacter() {
        assertThat(new BeanHelper().decapitalize("A"), is(equalTo("a")));
    }
    
}
