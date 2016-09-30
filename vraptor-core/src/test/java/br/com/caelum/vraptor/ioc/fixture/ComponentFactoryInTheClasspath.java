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
package br.com.caelum.vraptor.ioc.fixture;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
@ApplicationScoped
public final class ComponentFactoryInTheClasspath implements ComponentFactory<br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath.Provided> {
	private int callsToPreDestroy = 0 ;

	private ComponentFactoryInTheClasspath () {
		throw new InstantiationError( "Must not instantiate this class" );
	}

	@PreDestroy
	public void preDestroy() {
		callsToPreDestroy++;
	}
	
	
	public static class Provided {
		public Provided() {}
	}
	public static Provided PROVIDED = new Provided();
	
	@Produces		
	public Provided getInstance() {
		return PROVIDED;
	}
	
	public int getCallsToPreDestroy() {
		return callsToPreDestroy;
	}

}
