package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;

public class ViewsPropertiesPageResultTest {

    private Mockery mockery;
    private ViewsPropertiesPageResult result;
    private Config config;
    private HttpServletRequest request;
    private PathResolver resolver;
    private ResourceMethod method;
    private HttpServletResponse response;
    private ServletContext context;
    private HttpSession session;
    private Resource resource;
    private RequestDispatcher dispatcher;
    private RequestInfo requestInfo;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.session = mockery.mock(HttpSession.class);
        this.method = mockery.mock(ResourceMethod.class);
        this.requestInfo = mockery.mock(RequestInfo.class);
        this.resource = mockery.mock(Resource.class);
        this.config = mockery.mock(Config.class);
        this.resolver = mockery.mock(PathResolver.class);
        this.dispatcher = mockery.mock(RequestDispatcher.class);
        mockery.checking(new Expectations() {
            {
                one(request).getParameterMap();
                will(returnValue(new HashMap<String, Object>()));
                allowing(request).getSession();
                will(returnValue(session));
                one(session).getAttribute("org.vraptor.scope.ScopeType_FLASH");
                will(returnValue(new HashMap<String, Object>()));
                allowing(requestInfo).getResourceMethod();
                will(returnValue(method));
                allowing(method).getResource();
                will(returnValue(resource));
            }
        });
        this.result = new ViewsPropertiesPageResult(this.config, this.request, this.resolver, this.requestInfo,
                this.response, this.context);
    }

    @Component
    class CommonComponentOld {
        public void base() {
        }
    }

    @Test
    public void forwardNotOverridenUsesTheCommonAlgorithm() throws NoSuchMethodException, ServletException, IOException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue(null));
                one(resolver).pathFor(method, "ok");
                will(returnValue("defaultPath"));
                one(request).getRequestDispatcher("defaultPath");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test
    public void redirectPrefixExecutesClientRedirection() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("redirect:clientSide"));
                one(response).sendRedirect("clientSide");
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    class NewResource {
        public void base() {
        }
    }

    @Test
    public void newResourceUsesCommonAlgorithm() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(NewResource.class));
                allowing(method).getMethod();
                will(returnValue(NewResource.class.getMethod("base")));
                one(resolver).pathFor(method, "ok");
                will(returnValue("MyPath"));
                one(request).getRequestDispatcher("MyPath");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test
    public void commonOverrideExecutesForward() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("serverSide"));
                one(request).getRequestDispatcher("serverSide");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test(expected = ResultException.class)
    public void expressionProblemThrowsExceptionAndDoesNotRedirect() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("serverSide?client.id=${client.id}"));
                exactly(2).of(request).getAttribute("client"); will(returnValue(new CommonComponentOld()));
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    public static class MyClient {
    	private Long id;
		public Long getId() {
			return id;
		}
    }
    @Test
    public void nullValueIsWrittenAsEmpty() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(config).getForwardFor("CommonComponentOld.base.ok");
                will(returnValue("serverSide?client.id=${client.id}"));
                exactly(2).of(request).getAttribute("client"); will(returnValue(new MyClient()));
                one(request).getRequestDispatcher("serverSide?client.id=");
            }
        });
        this.result.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test
    public void includesUsesTheCommonAlgorithm() throws ServletException, IOException, NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
                allowing(resource).getType();
                will(returnValue(CommonComponentOld.class));
                allowing(method).getMethod();
                will(returnValue(CommonComponentOld.class.getMethod("base")));
                one(resolver).pathFor(method, "ok");
                will(returnValue("defaultPath"));
                one(request).getRequestDispatcher("defaultPath");
                will(returnValue(dispatcher));
                one(dispatcher).include(request, response);
            }
        });
        this.result.include("ok");
        mockery.assertIsSatisfied();
    }
}
