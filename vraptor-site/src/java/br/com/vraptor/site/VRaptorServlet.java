package br.com.vraptor.site;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VRaptorServlet
 */
public class VRaptorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Locale PT_BR = new Locale("pt", "br");

    /**
     * @see HttpServlet#HttpServlet()
     */
    public VRaptorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getContextPath();
		String uri = request.getRequestURI().replaceFirst(path, "");

		response.setLocale(uri.startsWith("/en")? Locale.ENGLISH : PT_BR);
		request.setAttribute("contextPath", path);

		request.getRequestDispatcher(uri).forward(request, response);
	}

}
