package br.com.caelum.vraptor.util.hibernate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SessionFactoryCreatorTest {
    
    private @Mock AnnotationConfiguration cfg;
    private @Mock SessionFactory factory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void shouldReturnCreatedInstance() {
        SessionFactoryCreator creator = new SessionFactoryCreator();
        SessionFactoryCreator spy = spy(creator);
        
        doReturn(cfg).when(spy).getAnnotationConfiguration();
        when(cfg.configure()).thenReturn(cfg);
        when(cfg.buildSessionFactory()).thenReturn(factory);
        
        spy.create();
        
        assertEquals(factory, spy.getInstance());
    }

    @Test
    public void shouldCloseFactoryOnDestroy() {
        SessionFactoryCreator creator = new SessionFactoryCreator();
        SessionFactoryCreator spy = spy(creator);
        
        doReturn(cfg).when(spy).getAnnotationConfiguration();
        when(cfg.configure()).thenReturn(cfg);
        when(cfg.buildSessionFactory()).thenReturn(factory);
        
        spy.create();
        spy.destroy();
        
        verify(factory).close();
    }
}
