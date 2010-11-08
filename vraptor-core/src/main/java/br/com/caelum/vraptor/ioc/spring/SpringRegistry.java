package br.com.caelum.vraptor.ioc.spring;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

public class SpringRegistry {

	private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
	private final SpringBasedContainer container;
	private final BasicConfiguration config;

	public SpringRegistry(SpringBasedContainer container, BasicConfiguration config) {
		this.container = container;
		this.config = config;
	}

	public void registerOn(BeanDefinitionRegistry registry, Class<?> type, boolean customComponent) {
		AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
		definition.setLazyInit(true);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
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
	 * From
	 * org.springframework.context.annotation.ClassPathBeanDefinitionScanner
	 * #applyScope()
	 *
	 * @param definition
	 * @param scopeMetadata
	 * @return
	 */
	private BeanDefinitionHolder applyScopeOn(BeanDefinitionRegistry registry, BeanDefinitionHolder definition,
			ScopeMetadata scopeMetadata) {
		String scope = scopeMetadata.getScopeName();
		ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
		definition.getBeanDefinition().setScope(scope);
		if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope)
				|| proxyMode.equals(ScopedProxyMode.NO)) {
			return definition;
		} else {
			boolean proxyTargetClass = proxyMode.equals(ScopedProxyMode.TARGET_CLASS);
			return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
		}
	}

	void registerOn(BeanDefinitionRegistry registry, Class<?> type) {
		registerOn(registry, type, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void registerFactory(final Class<?> type, ConfigurableListableBeanFactory beanFactory) {
		if (ComponentFactory.class.isAssignableFrom(type)) {
			beanFactory.registerSingleton(type.getName(), new ComponentFactoryBean(container, type));
		}
	}

	void register(final Class<?> type, ConfigurableListableBeanFactory beanFactory) {
		registerOn((BeanDefinitionRegistry) beanFactory, type, true);
		registerFactory(type, beanFactory);
	}

	void registerPrototypeScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
		for (Class<?> prototypeComponent : BaseComponents.getPrototypeScoped().values()) {
			registerOn(beanFactory, prototypeComponent);
		}
	}

	void registerCachedComponentsOn(DefaultListableBeanFactory beanFactory) {
		for (Class<?> cachedComponent : BaseComponents.getCachedComponents().values()) {
			registerOn(beanFactory, cachedComponent, true);
		}
	}

	void registerApplicationScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
		for (Class<?> type : BaseComponents.getApplicationScoped().values()) {
			registerOn(beanFactory, type);
			registerFactory(type, beanFactory);
		}

		for (Class<? extends StereotypeHandler> handlerType : BaseComponents.getStereotypeHandlers()) {
			registerOn(beanFactory, handlerType);
		}

		registerOn(beanFactory, StereotypedBeansRegistrar.class);
		registerOn(beanFactory, DefaultSpringLocator.class);

		config.getServletContext();
	}

	void registerRequestScopedComponentsOn(DefaultListableBeanFactory beanFactory) {
		for (Class<?> type : BaseComponents.getRequestScoped().values()) {
			registerOn(beanFactory, type);
			registerFactory(type, beanFactory);
		}

		for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
			registerOn(beanFactory, converterType);
		}

		registerOn(beanFactory, VRaptorRequestProvider.class, true);
		registerOn(beanFactory, HttpServletRequestProvider.class, true);
		registerOn(beanFactory, HttpServletResponseProvider.class, true);
		registerOn(beanFactory, HttpSessionProvider.class, true);

		beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);
	}

	void registerVRaptorComponents(DefaultListableBeanFactory beanFactory) {
		registerApplicationScopedComponentsOn(beanFactory);
		registerRequestScopedComponentsOn(beanFactory);
		registerPrototypeScopedComponentsOn(beanFactory);
	}
}
