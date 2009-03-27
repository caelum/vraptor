package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;

public class JspView implements View {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public JspView(HttpServletRequest req, HttpServletResponse res) {
        this.request = req;
        this.response = res;
    }

    public static Class<JspView> jsp() {
        return JspView.class;
    }

    public void forward() throws ServletException, IOException {
        request.getRequestDispatcher("").forward(request, response);
    }
    
    public void include() throws ServletException, IOException {
        request.getRequestDispatcher("").include(request, response);
    }
    
}
