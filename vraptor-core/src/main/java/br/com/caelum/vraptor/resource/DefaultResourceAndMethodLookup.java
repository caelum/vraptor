/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import br.com.caelum.vraptor.Path;

/**
 * The default resource method lookup algorithm. It goes through every method
 * searching for one mapped with the same id in the Path annotation.
 * 
 * @author Guilherme Silveira
 * @author Rafael Ferreira
 */
public class DefaultResourceAndMethodLookup implements ResourceAndMethodLookup {

	private final Resource resource;

	public DefaultResourceAndMethodLookup(Resource resource) {
		this.resource = resource;
	}

	public ResourceMethod methodFor(String pathUriComponent, String httpMethodName) {
	    return methodFor(resource.getType(), resource.getType(), pathUriComponent, httpMethodName);
	}

	private ResourceMethod methodFor(Class<?> resourceType, Class<?> searchingType, String pathUriComponent, String httpMethodName) {
	    if(searchingType.equals(Object.class)) {
	        return null;
	    }
        for (Method javaMethod : searchingType.getDeclaredMethods()) {
            if (isEligible(javaMethod) 
                && pathAccepted(pathUriComponent, resourceType, javaMethod) 
                && httpMethodAccepted(httpMethodName, javaMethod)) {
                return new DefaultResourceMethod(resource, javaMethod);
            }
        }
        return methodFor(resourceType, searchingType.getSuperclass(), pathUriComponent, httpMethodName);
    }

    private boolean isEligible(Method javaMethod) {
		return Modifier.isPublic(javaMethod.getModifiers()) && !Modifier.isStatic(javaMethod.getModifiers());
	}

	private boolean pathAccepted(String id, Class<?> resourceType, Method method) {
		return hasMatchingPathAnnotation(id, method) || sameNameAsId(id, resourceType, method);
	}

	private boolean hasMatchingPathAnnotation(String id, Method method) {
		Path path = method.getAnnotation(Path.class);
		if (path == null) {
			return false;
		}
		
		String regexFromWildcards = path.value().replaceAll("\\*", ".\\*");
		return id.matches(regexFromWildcards);
	}

	private boolean sameNameAsId(String id, Class<?> resourceType, Method method) {
		return ("/" + resourceType.getSimpleName() + "/" + method.getName()).equals(id);
	}

	private boolean httpMethodAccepted(String httpMethodName, Method javaMethod) {
		Class<? extends Annotation> httpMethodAnnotation = HttpMethod.valueOf(httpMethodName)
				.getAnnotation();
		return javaMethod.isAnnotationPresent(httpMethodAnnotation)
				|| noAnnotationPresent(HttpMethod.values(), javaMethod);
	}

	private boolean noAnnotationPresent(HttpMethod[] values, Method javaMethod) {
		for (HttpMethod key : values) {
			if (javaMethod.isAnnotationPresent(key.getAnnotation())) {
				return false;
			}
		}
		return true;
	}

}
