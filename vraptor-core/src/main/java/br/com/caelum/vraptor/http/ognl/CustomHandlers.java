package br.com.caelum.vraptor.http.ognl;

public interface CustomHandlers {

    CustomHandler to(Object target);

    void register(CustomHandler handler);

}
