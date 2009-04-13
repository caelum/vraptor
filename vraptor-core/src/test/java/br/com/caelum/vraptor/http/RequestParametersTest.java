package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class RequestParametersTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private Map<String, Object> map;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.request = mockery.mock(HttpServletRequest.class);
        this.map = new HashMap<String, Object>();
        mockery.checking(new Expectations() {
            {
                one(request).getParameterMap();
                will(returnValue(map));
            }
        });
    }

    @Test
    public void shouldReturnTheListOfRequestParameters() {
        map.put("key", new String[] { "value" });
        DefaultRequestParameters params = new DefaultRequestParameters(request);
        assertThat(params.get("key"), is(equalTo(new String[] { "value" })));
    }

    @Test
    public void shouldReturnTheListOfRequestParametersEvenIfItsOneSingleValue() {
        map.put("key", "value");
        DefaultRequestParameters params = new DefaultRequestParameters(request);
        assertThat(params.get("key"), is(equalTo(new String[] { "value" })));
    }

    @Test
    public void shouldReturnNullIfTheParameterDoesNotExist() {
        DefaultRequestParameters params = new DefaultRequestParameters(request);
        assertThat(params.get("key"), is(nullValue()));
    }

    @Test
    public void shouldAllowAnAddedParameter() {
        DefaultRequestParameters params = new DefaultRequestParameters(request);
        params.set("key", new String[]{"first"});
        assertThat(params.get("key"), is(equalTo(new String[] { "first" })));
    }

    @Test
    public void shouldAllowToOverrideRequestParameters() {
        map.put("base", "original");
        DefaultRequestParameters params = new DefaultRequestParameters(request);
        params.set("base", new String[]{"overriden"});
        assertThat(params.get("base"), is(equalTo(new String[] { "overriden" })));
    }

}
