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
package br.com.caelum.vraptor.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;

/**
 * The super-incredible MagicMap that exposes every request parameter in a map
 * chain that never ends! Useful for invalid forms to display input data.
 *
 * TODO is this class efficient?
 *
 * @see ParametersInstantiatorInterceptor
 */
public class RequestOutjectMap implements Map<String, Object> {

	private final List<Object> objectChain;
	private final Map<Object, Object> myObjects = new HashMap<Object, Object>();
	private final HttpServletRequest request;

	public RequestOutjectMap(String base, HttpServletRequest request) {
		this(new ArrayList<Object>(), request);

		objectChain.add(base);
	}

	RequestOutjectMap(List<Object> objects, HttpServletRequest request) {
		this.objectChain = objects;
		this.request = request;
	}

	public Object get(Object key) {
		if (!myObjects.containsKey(key)) {
			List<Object> nl = new ArrayList<Object>(objectChain);
			nl.add(key);
			myObjects.put(key, new RequestOutjectMap(nl, request));
		}
		return myObjects.get(key);
	}

	public String toParameterName() {
		StringBuilder sb = new StringBuilder();

		Object atual = null, anterior = null;
		Iterator<Object> it = objectChain.iterator();

		while (it.hasNext()) {
			anterior = atual;
			atual = it.next();

			if (anterior == null) {
				sb.append(atual.toString());
			}

			if (anterior != null && atual instanceof String) {
				sb.append('.').append(atual);
			}

			if (anterior instanceof String && atual instanceof Long) {
				sb.append('[').append(atual).append(']');
			}

		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return request.getParameter(toParameterName());
	}

	// to use in EL
	public String getAsString() {
		return toString();
	}

	public boolean containsKey(Object key) {
		return true;
	}

	public boolean isEmpty() {
		return false;
	}

	public int size() {
		String thisName = toParameterName();
		HashSet<String> aux = new HashSet<String>();

		Set<String> paramNames = request.getParameterMap().keySet();

		for (String p : paramNames) {
			if (p.startsWith(thisName)) {
				String s = p.substring(thisName.length());

				int indexOf = s.indexOf('.');
				if (indexOf > 0) {
					s = s.substring(0, indexOf);
				}

				indexOf = s.indexOf(']');
				if (indexOf > 0) {
					s = s.substring(0, indexOf);
				}

				aux.add(s);
			}
		}

		return aux.size();
	}

	/** FAKE METHODS **/
	public void clear() {
		throw new RuntimeException();
	}

	public boolean containsValue(Object value) {
		throw new RuntimeException();
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return Collections.emptySet();
	}

	public Set<String> keySet() {
		throw new RuntimeException();
	}

	public Object put(String key, Object value) {
		throw new RuntimeException();
	}

	public void putAll(Map<? extends String, ? extends Object> t) {
		throw new RuntimeException();
	}

	public Object remove(Object key) {
		throw new RuntimeException();
	}

	public Collection<Object> values() {
		throw new RuntimeException();
	}
} // end MagicMap
