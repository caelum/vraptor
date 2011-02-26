/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.ioc;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class ComponentFactoryIntrospectorTest {
	public static class MyFactory implements ComponentFactory<NeedsCustomInstantiation> {
		public NeedsCustomInstantiation getInstance() {
			return null;
		}
	}

	@Test
	public void shouldExtractTargetTypeFromGenericDefinition() {
		Object targetType = new ComponentFactoryIntrospector().targetTypeForComponentFactory(MyFactory.class);
		assertEquals(NeedsCustomInstantiation.class, targetType);
	}

	@SuppressWarnings("rawtypes")
	public static class FactoryWithoutTargetType implements ComponentFactory {
		public Object getInstance() {
			return null;
		}
	}

	@Test(expected = ComponentRegistrationException.class)
	public void shouldRequireGenericTypeInformationToBePresent() {
		new ComponentFactoryIntrospector().targetTypeForComponentFactory(FactoryWithoutTargetType.class);
	}

	class SuperClassThatImplementsCF implements ComponentFactory<String> {
		public String getInstance() {
			return "abc";
		}
	}

	class ClassThatImplementsCFExtending extends SuperClassThatImplementsCF {
	}

	@Test
	public void shoudWorkWithSubclassesOfComponenetFactoryImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatImplementsCFExtending.class));
		Assert.assertEquals(String.class, c);
	}

	@Test
	public void shoudWorkWithImplementationsOfComponenetFactorySubinterfacesImplementations() {
		Class<?> c = new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatImplementsCFIndirectly.class));
		Assert.assertEquals(String.class, c);
	}

	interface InterfaceThatExtendCF extends ComponentFactory<String> {
	}

	class ClassThatImplementsCFIndirectly implements InterfaceThatExtendCF {
		public String getInstance() {
			return "def";
		}
	}

	class ClassThatIsNotCFAtAll {
		public String getInstance() {
			return "def";
		}
	}

	@Test(expected = ComponentRegistrationException.class)
	public void shoudNotWorkWithClassesThatDoesNotImplementComponentFactory() {
		new ComponentFactoryIntrospector().targetTypeForComponentFactory((ClassThatIsNotCFAtAll.class));
	}
}
