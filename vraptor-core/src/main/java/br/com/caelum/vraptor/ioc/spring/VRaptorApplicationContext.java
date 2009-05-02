package br.com.caelum.vraptor.ioc.spring;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.http.ognl.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesCreator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.resource.DefaultResourceNotFoundHandler;
import br.com.caelum.vraptor.validator.DefaultValidator;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

/**
 * @author Fabio Kung
 */
public class VRaptorApplicationContext extends AbstractRefreshableWebApplicationContext {
    public static final String RESOURCES_LIST = "br.com.caelum.vraptor.resources.list";

    private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    private String[] basePackages;
    private SpringBasedContainer container;

    public VRaptorApplicationContext(SpringBasedContainer container, String... basePackages) {
        this.container = container;
        this.basePackages = basePackages;
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory);
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        beanFactory.registerSingleton(ServletContext.class.getName(), getServletContext());
        beanFactory.ignoreDependencyType(ServletContext.class);
        registerApplicationScopedComponentsOn(beanFactory);
        registerRequestScopedComponentsOn(beanFactory);

        new ComponentScanner(beanFactory).scan(basePackages);

        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
        registerCustomInjectionProcessor(beanFactory);
    }

    private void registerApplicationScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        registerOn(beanFactory, DefaultRouter.class);
        registerOn(beanFactory, StupidTranslator.class);
        registerOn(beanFactory, DefaultInterceptorRegistry.class);
        registerOn(beanFactory, AsmBasedTypeCreator.class);
        registerOn(beanFactory, DefaultPathResolver.class);
        registerOn(beanFactory, ParanamerNameProvider.class);
        registerOn(beanFactory, DefaultConverters.class);
        registerOn(beanFactory, EmptyElementsRemoval.class);
        registerOn(beanFactory, ResourcesHolder.class);
        registerOn(beanFactory, ResourceFinder.class);
        registerOn(beanFactory, ResourceRegistrar.class);
        registerOn(beanFactory, NoRoutesConfiguration.class);
        registerOn(beanFactory, PathAnnotationRoutesCreator.class);
    }

    private void registerRequestScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
        registerOn(beanFactory, ParametersInstantiatorInterceptor.class);
        registerOn(beanFactory, DefaultMethodInfo.class);
        registerOn(beanFactory, InterceptorListPriorToExecutionExtractor.class);
        registerOn(beanFactory, URLParameterExtractorInterceptor.class);
        registerOn(beanFactory, DefaultInterceptorStack.class);
        registerOn(beanFactory, DefaultRequestExecution.class);
        registerOn(beanFactory, ResourceLookupInterceptor.class);
        registerOn(beanFactory, InstantiateInterceptor.class);
        registerOn(beanFactory, DefaultResult.class);
        registerOn(beanFactory, ExecuteMethodInterceptor.class);
        registerOn(beanFactory, DefaultPageResult.class);
        registerOn(beanFactory, ForwardToDefaultViewInterceptor.class);
        registerOn(beanFactory, DefaultLogicResult.class);
        registerOn(beanFactory, OgnlParametersProvider.class);
        registerOn(beanFactory, DefaultMethodInfo.class);
        registerOn(beanFactory, DefaultValidator.class);
        registerOn(beanFactory, DefaultLogicResult.class);
        registerOn(beanFactory, DefaultResourceNotFoundHandler.class);
        registerOn(beanFactory, VRaptorRequestProvider.class, true);
        registerOn(beanFactory, HttpServletRequestProvider.class, true);
        registerOn(beanFactory, HttpServletResponseProvider.class, true);
        registerOn(beanFactory, HttpSessionProvider.class, true);
        registerOn(beanFactory, JstlLocalization.class);

        beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);
    }

    public void register(Class<?> type) {
        registerOn((BeanDefinitionRegistry) getBeanFactory(), type, true);
    }

    private void registerOn(BeanDefinitionRegistry registry, Class<?> type) {
        registerOn(registry, type, false);
    }

    private void registerOn(BeanDefinitionRegistry registry, Class<?> type, boolean customComponent) {
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
        definition.setLazyInit(true);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        if (customComponent) {
            definition.setPrimary(true);
            definition.setRole(BeanDefinition.ROLE_APPLICATION);
        } else {
            definition.setPrimary(false);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        String name = beanNameGenerator.generateBeanName(definition, registry);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definition, name);

        VRaptorScopeResolver scopeResolver = new VRaptorScopeResolver();
        ScopeMetadata scopeMetadata = scopeResolver.resolveScopeMetadata(definition);
        definitionHolder = applyScopeOn(registry, definitionHolder, scopeMetadata);

        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * From org.springframework.context.annotation.ClassPathBeanDefinitionScanner#applyScope()
     *
     * @param definition
     * @param scopeMetadata
     * @return
     */
    private BeanDefinitionHolder applyScopeOn(BeanDefinitionRegistry registry, BeanDefinitionHolder definition, ScopeMetadata scopeMetadata) {
        String scope = scopeMetadata.getScopeName();
        ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
        definition.getBeanDefinition().setScope(scope);
        if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope) ||
                proxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        } else {
            boolean proxyTargetClass = proxyMode.equals(ScopedProxyMode.TARGET_CLASS);
            return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
        }
    }

    private void registerCustomInjectionProcessor(BeanDefinitionRegistry registry) {
        RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        definition.getPropertyValues().addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
        registry.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
    }

    public <T> T getBean(Class<T> type) {
        @SuppressWarnings("unchecked")
        Map<String, ? extends T> instances = BeanFactoryUtils.beansOfTypeIncludingAncestors(this, type);
        if (instances.size() == 0) {
            throw new NoSuchBeanDefinitionException(type, "no bean for this type registered");
        } else if (instances.size() == 1) {
            return instances.values().iterator().next();
        } else {
            for (Map.Entry<String, ? extends T> entry : instances.entrySet()) {
                BeanDefinition definition = getBeanFactory().getBeanDefinition(entry.getKey());
                if (isPrimary(definition)) {
                    return entry.getValue();
                } else if (hasGreaterRoleThanInfrastructure(definition)) {
                    return entry.getValue();
                }
            }
            throw new NoSuchBeanDefinitionException("there are " + instances.size() +
                    " implementations for the type [" + type +
                    "], but none of them is primary or has a Role greater than BeanDefinition.ROLE_INFRASTRUCTURE");
        }
    }

    private boolean isPrimary(BeanDefinition definition) {
        return definition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) definition).isPrimary();
    }

    private boolean hasGreaterRoleThanInfrastructure(BeanDefinition definition) {
        return definition.getRole() < BeanDefinition.ROLE_INFRASTRUCTURE;
    }
}
