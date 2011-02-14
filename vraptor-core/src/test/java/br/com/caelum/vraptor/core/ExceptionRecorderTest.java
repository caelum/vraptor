package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;

public class ExceptionRecorderTest {
    
    static final String DEFAULT_REDIRECT = "/any-resource";

    @Mock private Result result;
    private ExceptionMapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Proxifier proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
        mapper = new DefaultExceptionMapper(proxifier);
    }

    @Test
    public void withRootException() {
        mapper.record(Exception.class).forwardTo(DEFAULT_REDIRECT);
        mapper.findByException(new Exception()).replay(result);

        verify(result).forwardTo(DEFAULT_REDIRECT);
    }

    @Test
    public void withNestedException() {
        mapper.record(IllegalStateException.class).forwardTo(DEFAULT_REDIRECT);
        mapper.findByException(new RuntimeException(new IllegalStateException())).replay(result);

        verify(result).forwardTo(DEFAULT_REDIRECT);
    }

    @Test
    public void whenNotFoundException() {
        mapper.record(IOException.class).forwardTo(DEFAULT_REDIRECT);
        ExceptionRecorder<Result> recorder = mapper.findByException(new RuntimeException(new IllegalStateException()));

        assertThat(recorder, Matchers.nullValue());
    }
}
