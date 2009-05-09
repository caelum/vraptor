package br.com.caelum.vraptor.http.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.http.DefaultParameterNameProvider;
import br.com.caelum.vraptor.interceptor.DogAlike;

public class AsmBasedTypeCreatorTest {

    private AsmBasedTypeCreator creator;
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.creator = new AsmBasedTypeCreator(new DefaultParameterNameProvider());
        this.mockery = new VRaptorMockery();
    }

    @Test
    public void testShouldCreateGetterAndSetterForAMethodParameter() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("bark",String.class)));
        
        Method getter = type.getDeclaredMethod("getString");
        Method setter = type.getDeclaredMethod("setString", String.class);
        
        Object instance = type.newInstance();
        setter.invoke(instance, "MESSAGE");
        MatcherAssert.assertThat((String)getter.invoke(instance), Matchers.is(Matchers.equalTo("MESSAGE")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testShouldWorkFineIfMemberNameIsAReservedWordAsInt() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("bark",int.class)));
        
        Method getter = type.getDeclaredMethod("getInt");
        Method setter = type.getDeclaredMethod("setInt", int.class);
        
        Object instance = type.newInstance();
        setter.invoke(instance, 3);
        MatcherAssert.assertThat((Integer)getter.invoke(instance), Matchers.is(Matchers.equalTo(3)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToDealWithGenericCollection() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("eat",List.class)));
        
        Method getter = type.getDeclaredMethod("getListOfString");
        Assert.assertTrue(getter.getGenericReturnType() instanceof ParameterizedType);
        ParameterizedType returnType = (ParameterizedType) getter.getGenericReturnType();
        Assert.assertTrue(List.class.isAssignableFrom((Class)returnType.getRawType()));
        Type firstType = returnType.getActualTypeArguments()[0];
        Assert.assertTrue(String.class.isAssignableFrom((Class)firstType));
        
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfPrimitive() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("dropDead",int[].class)));
        
        Method getter = type.getDeclaredMethod("getInt");
        Method setter = type.getDeclaredMethod("setInt", int[].class);
        
        Object instance = type.newInstance();
        Integer[] array = new Integer[]{0,1};
        setter.invoke(instance, array);
        MatcherAssert.assertThat((Integer[])getter.invoke(instance), Matchers.is(Matchers.equalTo(array)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfType() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("recurse",DogAlike[].class)));
        
        Method getter = type.getDeclaredMethod("getDogAlike");
        Method setter = type.getDeclaredMethod("setDogAlike", DogAlike[].class);
        
        Object instance = type.newInstance();
        DogAlike[] array = new DogAlike[]{mockery.mock(DogAlike.class)};
        setter.invoke(instance, new Object[]{array});
        MatcherAssert.assertThat((DogAlike[])getter.invoke(instance), Matchers.is(Matchers.equalTo(array)));
        mockery.assertIsSatisfied();
    }

}
