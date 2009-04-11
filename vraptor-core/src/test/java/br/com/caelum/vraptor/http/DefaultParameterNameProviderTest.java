package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DefaultParameterNameProviderTest {
    
    private DefaultParameterNameProvider provider;

    @Before
    public void setup() {
        this.provider = new DefaultParameterNameProvider();
    }
    
    class Field {
        
    }
    interface Horse {
        void runThrough(Field f);
        void rest(int hours);
        void setLeg(int[] length);
    }
    
    @Test
    public void shouldNameObjectTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("runThrough", Field.class))[0], is(equalTo(DefaultParameterNameProviderTest.class.getSimpleName())));
    }

    @Test
    public void shouldNamePrimitiveTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("rest", int.class))[0], is(equalTo(int.class.getSimpleName())));
    }

    @Test
    public void shouldNameArrayAsItsSimpleTypeName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("setLeg", int[].class))[0], is(equalTo(int.class.getSimpleName())));
    }

    @Test
    public void shouldNameGenericCollectionUsingOf() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Cat.class.getDeclaredMethod("fightWith", List.class))[0], is(equalTo("ListOfString")));
    }
    
    static public interface Cat {
        void fightWith(List<String> cats);
    }

}
