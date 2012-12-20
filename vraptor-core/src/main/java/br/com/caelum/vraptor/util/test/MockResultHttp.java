package br.com.multiway.interligation7.api.controller;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.DefaultHttpResult;
import br.com.caelum.vraptor.view.DefaultStatus;
import br.com.caelum.vraptor.view.HttpResult;

public class MockResultHttp extends MockResult{
	private MockHttpServletResponse response = new MockHttpServletResponse();
	
	public MockResultHttp() {
		super(new CglibProxifier(new ObjenesisInstanceCreator()));
	}
	
	@Override
	public <T extends View> T use(Class<T> view) {
		if (view.isAssignableFrom(HttpResult.class)){
			DefaultHttpResult defaultHttpResult = new DefaultHttpResult(response, new DefaultStatus(response, this, null, proxifier, null));
			return view.cast(defaultHttpResult);
		}
		
		return proxifier.proxify(view, returnOnFinalMethods(view));
	}
	
	public String getBody(){
		response.getWriter().flush();
		return response.getContentAsString();
	}
	
	public String getContentType(){
		return response.getContentType();
	}
	
	public String getCharacterEncoding(){
		return response.getCharacterEncoding();
	}
}