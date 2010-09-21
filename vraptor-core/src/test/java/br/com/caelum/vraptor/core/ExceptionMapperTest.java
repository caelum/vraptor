package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.proxy.DefaultProxifier;

public class ExceptionMapperTest {

    private ExceptionMapper mapper;

    @Before
    public void setup() {
        mapper = new DefaultExceptionMapper(new DefaultProxifier());
    }

    @Test
    public void exceptionsAreAdded() {
        mapper.record(NullPointerException.class);
        mapper.record(IOException.class);

        assertThat(mapper.findByException(new NullPointerException()), notNullValue());
        assertThat(mapper.findByException(new IOException(new RuntimeException())), notNullValue());

        assertThat(mapper.findByException(new SQLException()), nullValue());
        assertThat(mapper.findByException(new Exception()), nullValue());
    }
}