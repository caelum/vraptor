package br.com.caelum.vraptor.view.jsp;

public interface PageResult {

    public abstract void forward(String result);

    public abstract void include(String result);

    public abstract void include(String key, Object value);

}