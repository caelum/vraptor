package br.com.caelum.vraptor.http;

import java.lang.reflect.Type;

public interface ParameterNameProvider {

    String nameFor(Type paramType);

}
