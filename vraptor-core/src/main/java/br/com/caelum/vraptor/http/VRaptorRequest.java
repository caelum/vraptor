package br.com.caelum.vraptor.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A request capable of adding new parameters.
 * @author guilherme silveira
 *
 */
public class VRaptorRequest extends HttpServletRequestWrapper{
	
	private final Map<String, String> extraParameters = new HashMap<String,String>();

	public VRaptorRequest(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return super.getParameter(name);
	}
	
	@Override
	public Enumeration getParameterNames() {
		return new IteratorEnumeration(getParameterMap().keySet().iterator());
	}
	
	@Override
	public String[] getParameterValues(String name) {
		// TODO Auto-generated method stub
		return super.getParameterValues(name);
	}
	
	@Override
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return super.getParameterMap();
	}

}
