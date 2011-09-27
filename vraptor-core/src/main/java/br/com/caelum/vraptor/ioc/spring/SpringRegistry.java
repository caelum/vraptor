/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.ioc.spring;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.Container;

/**
 * Class responsible for registering beans on spring.
 * 
 * @author Lucas Cavalcanti
 * @author Douglas Campos (qmx)
 * @since 3.3.0
 */
public class SpringRegistry {

	private final BeanNameGenerator beanNameGenerator = new UniqueBeanNameGenerator(new AnnotationBeanNameGenerator());
	private final ConfigurableListableBeanFactory beanFactory;
	private final VRaptorScopeResolver scopeResolver = new VRaptorScopeResolver();
	private final Container container;

	public SpringRegistry(ConfigurableListableBeanFactory configurableListableBeanFactory, Container container) {
		this.beanFactory = configurableListableBeanFactory;
		this.container = container;
	}

	private void registerOn(Class<?> type, boolean customComponent) {
		AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(type);
		if (!customComponent) {
			definition.setLazyInit(true);
		}
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
		definition.setPrimary(customComponent);
		definition.setRole(customComponent ? BeanDefinition.ROLE_APPLICATION : BeanDefinition.ROLE_INFRASTRUCTURE);

		String name = beanNameGenerator.generateBeanName(definition, (BeanDefinitionRegistry) beanFactory);
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definition, name);

		ScopeMetadata scopeMetadata = scopeResolver.resolveScopeMetadata(definition);
		definitionHolder = applyScopeOn(definitionHolder, scopeMetadata);

		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, (BeanDefinitionRegistry) beanFactory);
	}

	/**
	 * From org.springframework.context.annotation.ClassPathBeanDefinitionScanner#applyScope()
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

	private void registerOn(Class<?> type) {
		registerOn(type, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerFactory(final Class<?> type) {
		if (ComponentFactory.class.isAssignableFrom(type)) {
			beanFactory.registerSingleton(type.getName(), new ComponentFactoryBean(container, type));
		}
	}

	void register(final Class<?> type) {
		register(type, true);
	}

	private void register(final Class<?> type, boolean customComponent) {
		registerOn(type, customComponent);
		registerFactory(type);
	}

	private void registerPrototypeScopedComponentsOn() {
		for (Class<?> prototypeComponent : BaseComponents.getPrototypeScoped().values()) {
			registerOn(prototypeComponent);
		}
	}

	private void registerCachedComponentsOn() {
		for (Class<?> cachedComponent : BaseComponents.getCachedComponents().values()) {
			registerOn(cachedComponent, true);
		}
	}

	private void registerApplicationScopedComponentsOn() {
		registerComponents(BaseComponents.getApplicationScoped().values());

		registerComponents(Arrays.asList(BaseComponents.getStereotypeHandlers()));

		registerOn(StereotypedBeansRegistrar.class);
		registerOn(DefaultSpringLocator.class);
	}

	private void registerRequestScopedComponentsOn() {
		registerComponents(BaseComponents.getRequestScoped().values());

		registerComponents(BaseComponents.getBundledConverters());

		registerOn(VRaptorRequestProvider.class, true);
		registerOn(HttpServletRequestProvider.class, true);
		registerOn(HttpServletResponseProvider.class, true);
		registerOn(HttpSessionProvider.class, true);

		beanFactory.registerSingleton(SpringBasedContainer.class.getName(), container);
	}

	private void registerVRaptorComponents() {
		registerApplicationScopedComponentsOn();
		registerRequestScopedComponentsOn();
		registerPrototypeScopedComponentsOn();
	}

	private void registerCustomInjectionProcessor() {
		RootBeanDefinition definition = new RootBeanDefinition(InjectionBeanPostProcessor.class);
		definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		definition.getPropertyValues().addPropertyValue("order", Ordered.LOWEST_PRECEDENCE);
		((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME, definition);
	}

	void registerCustomComponents(Collection<Class<?>> toRegister) {
		for (Class<?> type : toRegister) {
			register(type);
		}
	}

	private <T> void registerComponents(Collection<Class<? extends T>> toRegister) {
		for (Class<?> type : toRegister) {
			register(type, false);
		}
	}

	void configure() {
		registerVRaptorComponents();

		AnnotationConfigUtils.registerAnnotationConfigProcessors((BeanDefinitionRegistry) beanFactory);
		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary((BeanDefinitionRegistry) beanFactory);

		registerCustomInjectionProcessor();

		registerCachedComponentsOn();

		WebApplicationContextUtils.registerWebApplicationScopes(beanFactory);
	}
}
