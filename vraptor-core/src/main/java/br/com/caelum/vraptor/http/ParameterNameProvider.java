package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

public interface ParameterNameProvider {

    String[] parameterNamesFor(Method method);

}
