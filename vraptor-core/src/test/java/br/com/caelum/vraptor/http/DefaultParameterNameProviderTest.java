package br.com.caelum.vraptor.http;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class DefaultParameterNameProviderTest {
    
    private DefaultParameterNameProvider creator;

    @Before
    public void setup() {
        this.creator = new DefaultParameterNameProvider();
    }
    
    @Test
    public void shouldNameObjectTypeAsItsSimpleName() {
        MatcherAssert.assertThat(creator.extractName(DefaultParameterNameProviderTest.class), Matchers.is(Matchers.equalTo(DefaultParameterNameProviderTest.class.getSimpleName())));
    }

    @Test
    public void shouldNamePrimitiveTypeAsItsSimpleName() {
        MatcherAssert.assertThat(creator.extractName(int.class), Matchers.is(Matchers.equalTo(int.class.getSimpleName())));
    }

    @Test
    public void shouldNameArrayAsItsSimpleTypeName() {
        MatcherAssert.assertThat(creator.extractName(int[].class), Matchers.is(Matchers.equalTo(int.class.getSimpleName())));
    }

    @Test
    public void shouldNameGenericCollectionUsingOf() throws SecurityException, NoSuchMethodException {
        ParameterizedType type = (ParameterizedType)Cat.class.getDeclaredMethod("fightWith", List.class).getGenericParameterTypes()[0];
        MatcherAssert.assertThat(creator.extractName(type), Matchers.is(Matchers.equalTo("ListOfString")));
    }
    
    static public interface Cat {
        void fightWith(List<String> cats);
    }


}
