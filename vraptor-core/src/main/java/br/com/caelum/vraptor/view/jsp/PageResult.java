package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;

import javax.servlet.ServletException;

public interface PageResult {

    public abstract void forward(String result) throws ServletException, IOException;

    public abstract void include(String result) throws ServletException, IOException;

    public abstract void include(String key, Object value);

}