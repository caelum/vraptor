/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;

public class PathAnnotationRules implements ListOfRules {

	private final List<Rule> rules = new ArrayList<Rule>();

	public PathAnnotationRules(Resource resource) {
		Class<?> baseType = resource.getType();
		registerRulesFor(baseType, baseType);
	}

	public List<Rule> getRules() {
		return rules;
	}

	private void registerRulesFor(Class<?> actualType, Class<?> baseType) {
		for (Method javaMethod : actualType.getDeclaredMethods()) {
			if (isEligible(javaMethod)) {
				String uri = getUriFor(javaMethod, baseType);
				UriBasedRule rule = new UriBasedRule(uri);
				for (HttpMethod m : HttpMethod.values()) {
					if (javaMethod.isAnnotationPresent(m.getAnnotation())) {
						rule.with(m);
					}
				}
				rule.is(baseType, javaMethod);
				this.rules.add(rule);
			}
		}
	}

	private String getUriFor(Method javaMethod, Class<?> type) {
		if (javaMethod.isAnnotationPresent(Path.class)) {
			return javaMethod.getAnnotation(Path.class).value();
		}
		return extractControllerFromName(type.getSimpleName()) + "/" + javaMethod.getName();
	}

	private String extractControllerFromName(String baseName) {
		if (baseName.endsWith("Controller")) {
			return "/" + baseName.substring(0, baseName.lastIndexOf("Controller"));
		}
		return "/" + baseName;
	}

	private boolean isEligible(Method javaMethod) {
		return Modifier.isPublic(javaMethod.getModifiers()) && !Modifier.isStatic(javaMethod.getModifiers());
	}

}
