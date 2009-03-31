package br.com.caelum.vraptor.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.interceptor.DogAlike;

public class OgnlTypeCreatorTest {

    private OgnlTypeCreator creator;
    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.creator = new OgnlTypeCreator();
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
        
        Method getter = type.getDeclaredMethod("getint");
        Method setter = type.getDeclaredMethod("setint", int.class);
        
        Object instance = type.newInstance();
        setter.invoke(instance, 3);
        MatcherAssert.assertThat((Integer)getter.invoke(instance), Matchers.is(Matchers.equalTo(3)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testGimmeMyValuesShouldReturnCurrentValue() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("bark",int.class)));
        
        Method gimme = type.getDeclaredMethod("gimmeMyValues");
        Method setter = type.getDeclaredMethod("setint", int.class);
        
        Object instance = type.newInstance();
        setter.invoke(instance, 3);
        MatcherAssert.assertThat((Integer)((Object[])gimme.invoke(instance))[0], Matchers.is(Matchers.equalTo(3)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldBeAbleToDealWithGenericCollection() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> type = creator.typeFor(mockery.method(DogAlike.class.getDeclaredMethod("eat",List.class)));
        
        Method getter = type.getDeclaredMethod("getList");
        Method setter = type.getDeclaredMethod("setList", List.class);
        
        Object instance = type.newInstance();
        List list = new ArrayList();
        setter.invoke(instance, list);
        MatcherAssert.assertThat((List)getter.invoke(instance), Matchers.is(Matchers.equalTo(list)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfPrimitive() {
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandleArraysOfType() {
        mockery.assertIsSatisfied();
    }

}
