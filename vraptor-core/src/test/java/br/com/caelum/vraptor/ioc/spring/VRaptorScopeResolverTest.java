package br.com.caelum.vraptor.ioc.spring;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Fabio Kung
 */
public class VRaptorScopeResolverTest {

    @Test
    public void shouldResolveToSingletonScopeByDefault() {
        ScopeMetadata scopeMetadata = readScopeMetadata(DummyComponent.class);
        Assert.assertEquals(ScopedProxyMode.NO, scopeMetadata.getScopedProxyMode());
        Assert.assertEquals(BeanDefinition.SCOPE_SINGLETON, scopeMetadata.getScopeName());
    }

    @Test
    public void shouldResolveRequestScopedAnnotationToRequestScope() {
        ScopeMetadata scopeMetadata = readScopeMetadata(RequestScopedComponent.class);
        Assert.assertEquals(ScopedProxyMode.NO, scopeMetadata.getScopedProxyMode());
        Assert.assertEquals(WebApplicationContext.SCOPE_REQUEST, scopeMetadata.getScopeName());
    }

    @Test
    public void shouldResolveSessionScopedAnnotationToSessionScope() {
        ScopeMetadata scopeMetadata = readScopeMetadata(SessionScopedComponent.class);
        Assert.assertEquals(ScopedProxyMode.NO, scopeMetadata.getScopedProxyMode());
        Assert.assertEquals(WebApplicationContext.SCOPE_SESSION, scopeMetadata.getScopeName());
    }

    @Test
    public void shouldResolveApplicationScopedAnnotationToSingletonScope() {
        ScopeMetadata scopeMetadata = readScopeMetadata(ApplicationScopedComponent.class);
        Assert.assertEquals(ScopedProxyMode.NO, scopeMetadata.getScopedProxyMode());
        Assert.assertEquals(BeanDefinition.SCOPE_SINGLETON, scopeMetadata.getScopeName());
    }

    private ScopeMetadata readScopeMetadata(Class componentType) {
        VRaptorScopeResolver resolver = new VRaptorScopeResolver();
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(componentType);
        ScopeMetadata scopeMetadata = resolver.resolveScopeMetadata(definition);
        return scopeMetadata;
    }
}

