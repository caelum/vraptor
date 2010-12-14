package br.com.caelum.vraptor.ioc.spring;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

public class SpringRegistry {

	private final AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
	private final ConfigurableListableBeanFactory beanFactory;
	private final VRaptorScopeResolver scopeResolver = new VRaptorScopeResolver();
	private final SpringBasedContainer container;

	public SpringRegistry(ConfigurableListableBeanFactory configurableListableBeanFactory, SpringBasedContainer container) {
		this.beanFactory = configurableListableBeanFactory;
		this.container = container;
	}

	public void registerOn(Class<?> type, boolean customComponent) {
		AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
		definition.setLazyInit(true);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		if (customComponent) {
			definition.setPrimary(true);
			definition.setRole(BeanDefinition.ROLE_APPLICATION);
		} else {
			definition.setPrimary(false);
			definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		}

		String name = beanNameGenerator.generateBeanName(definition, (BeanDefinitionRegistry) beanFactory);
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definition, name);

		ScopeMetadata scopeMetadata = scopeResolver.resolveScopeMetadata(definition);
		definitionHolder = applyScopeOn(definitionHolder, scopeMetadata);

		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, (BeanDefinitionRegistry) beanFactory);
	}

	/**
	 * From
	 * org.springframework.context.annotation.ClassPathBeanDefinitionScanner
	 * #applyScope()
	 * @param definition
	 * @param scopeMetadata
	 *
	 * @return
	 */
	private BeanDefinitionHolder applyScopeOn(BeanDefinitionHolder definition, ScopeMetadata scopeMetadata) {
		String scope = scopeMetadata.getScopeName();
		ScopedProxyMode proxyMode = scopeMetadata.getScopedProxyMode();
		definition.getBeanDefinition().setScope(scope);
		if (BeanDefinition.SCOPE_SINGLETON.equals(scope) || BeanDefinition.SCOPE_PROTOTYPE.equals(scope)
				|| proxyMode.equals(ScopedProxyMode.NO)) {
			return definition;
		} else {
			boolean proxyTargetClass = proxyMode.equals(ScopedProxyMode.TARGET_CLASS);
			return ScopedProxyUtils.createScopedProxy(definition, (BeanDefinitionRegistry) beanFactory, proxyTargetClass);
		}
	}

	void registerOn(Class<?> type) {
		registerOn(type, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void registerFactory(final Class<?> type) {
		if (ComponentFactory.class.isAssignableFrom(type)) {
			beanFactory.registerSingleton(type.getName(), new ComponentFactoryBean(container, type));
		}
	}

	void register(final Class<?> type) {
		registerOn(type, true);
		registerFactory(type);
	}

	void registerPrototypeScopedComponentsOn() {
		for (Class<?> prototypeComponent : BaseComponents.getPrototypeScoped().values()) {
			registerOn(prototypeComponent);
		}
	}

	void registerCachedComponentsOn() {
		for (Class<?> cachedComponent : BaseComponents.getCachedComponents().values()) {
			registerOn(cachedComponent, true);
		}
	}

	void registerApplicationScopedComponentsOn() {
		for (Class<?> type : BaseComponents.getApplicationScoped().values()) {
			registerOn(type);
			registerFactory(type);
		}

		for (Class<? extends StereotypeHandler> handlerType : BaseComponents.getStereotypeHandlers()) {
			registerOn(handlerType);
		}

		registerOn(StereotypedBeansRegistrar.class);
		registerOn(DefaultSpringLocator.class);
	}

	void registerRequestScopedComponentsOn() {
		for (Class<?> type : BaseComponents.getRequestScoped().values()) {
			registerOn(type);
			registerFactory(type);
		}

		for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
			registerOn(converterType);
		}

		registerOn(VRaptorRequestProvider.class, true);
		registerOn(HttpServletRequestProvider.class, true);
		registerOn(HttpServletResponseProvider.class, true);
		registerOn(HttpSessionProvider.class, true);

		beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);
	}

	void registerVRaptorComponents() {
		registerApplicationScopedComponentsOn();
		registerRequestScopedComponentsOn();
		registerPrototypeScopedComponentsOn();
	}
}
