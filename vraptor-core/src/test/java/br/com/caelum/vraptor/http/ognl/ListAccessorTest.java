package br.com.caelum.vraptor.http.ognl;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlContext;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorMockery;

public class ListAccessorTest {

    private ListAccessor accessor;

    @Before
    public void setup() {
        this.accessor = new ListAccessor();
    }

    @Test
    public void gettingShouldReturnNullIfIndexNotFound() throws Exception {
        List<String> l = new ArrayList<String>();
        Object value = accessor.getProperty(null, l, 1);
        MatcherAssert.assertThat(value, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void gettingShouldReturnValueIfIndexFound() throws Exception {
        List<String> l = new ArrayList<String>();
        l.add("nothing");
        l.add("guilherme");
        Object value = accessor.getProperty(null, l, 1);
        MatcherAssert.assertThat(value, Matchers.is(Matchers.equalTo((Object) "guilherme")));
    }

    @Test
    public void settingShouldNullifyUpToIndex() throws Exception {
        List<String> l = new ArrayList<String>();
        accessor.setProperty(null, l, 1, "hello");
        MatcherAssert.assertThat(l.get(0), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(l.get(1), Matchers.is(Matchers.equalTo("hello")));
    }

}
