package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.http.StupidTranslator;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletContext;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer implements Container {
    private final GenericWebApplicationContext applicationContext;
    private final ServletContext servletContext;

    private String[] basePackages = {"br.com.caelum.vraptor"};

    public SpringBasedContainer(ServletContext context, String... basePackages) {
        // TODO provide users the ability to provide custom containers
        servletContext = context;
        if (basePackages.length > 0) {
            this.basePackages = basePackages;
        }
        applicationContext = new GenericWebApplicationContext();
        registerCustomInjectionProcessor(applicationContext);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(applicationContext);
    }

    private void registerCustomInjectionProcessor(GenericApplicationContext applicationContext) {
        RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        definition.getPropertyValues().addPropertyValue("order", new Integer(Ordered.LOWEST_PRECEDENCE));
        applicationContext.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
    }

    public void start() {
        new ComponentScanner(applicationContext).scan(basePackages);
        applicationContext.refresh();
        applicationContext.start();
    }

    public void stop() {
        applicationContext.stop();
        applicationContext.destroy();
    }

    public RequestExecution prepare(ResourceMethod method, HttpServletRequest request, HttpServletResponse response) {
        RequestExecution execution = instanceFor(RequestExecution.class);
        return new RequestExecutionWrapper(execution);
    }

    public <T> T instanceFor(Class<T> type) {
        return (T) BeanFactoryUtils.beanOfType(applicationContext, type);
    }
}
