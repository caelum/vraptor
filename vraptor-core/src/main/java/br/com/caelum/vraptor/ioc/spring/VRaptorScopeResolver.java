package br.com.caelum.vraptor.ioc.spring;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.WebApplicationContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * @author Fabio Kung
 */
public class VRaptorScopeResolver implements ScopeMetadataResolver {
    private final Map<String, String> scopes = new LinkedHashMap<String, String>();

    public VRaptorScopeResolver() {
        // TODO prototype scope?
        scopes.put(RequestScoped.class.getName(), WebApplicationContext.SCOPE_REQUEST);
        scopes.put(SessionScoped.class.getName(), WebApplicationContext.SCOPE_SESSION);
        scopes.put(ApplicationScoped.class.getName(), BeanDefinition.SCOPE_SINGLETON);
    }

    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        AnnotationMetadata metadata = ((AnnotatedBeanDefinition) definition).getMetadata();
        for (Map.Entry<String, String> scope : scopes.entrySet()) {
            if (metadata.hasAnnotation(scope.getKey())) {
                ScopeMetadata scopeMetadata = new ScopeMetadata();
                scopeMetadata.setScopeName(scope.getValue());
                return scopeMetadata;
            }
        }
        return requestScopeAsDefault();
    }

    private ScopeMetadata requestScopeAsDefault() {
        ScopeMetadata singleton = new ScopeMetadata();
        singleton.setScopeName(WebApplicationContext.SCOPE_REQUEST);
        return singleton;
    }
}
