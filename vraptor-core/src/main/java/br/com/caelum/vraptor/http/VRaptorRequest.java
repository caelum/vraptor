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

package br.com.caelum.vraptor.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A request capable of adding new parameters.
 *
 * @author guilherme silveira
 *
 */
public class VRaptorRequest extends HttpServletRequestWrapper implements MutableRequest {

	private static final Logger logger = LoggerFactory.getLogger(VRaptorRequest.class);

	private final Hashtable<String, String[]> extraParameters = new Hashtable<String, String[]>();

	public VRaptorRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		if (extraParameters.containsKey(name)) {
			String[] values = extraParameters.get(name);
			if (values.length == 1) {
				return values[0];
			} else {
				return Arrays.toString(values);
			}
		}
		return super.getParameter(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Enumeration getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		if (extraParameters.containsKey(name)) {
			return extraParameters.get(name);
		}
		return super.getParameterValues(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getParameterMap() {
		Map complete = new HashMap(super.getParameterMap());
		complete.putAll(this.extraParameters);
		return complete;
	}

	public void setParameter(String key, String... value) {
		logger.debug("Setting {} with {}", key, value);
		this.extraParameters.put(key, value);
	}

	@Override
	public String toString() {
		return String.format("[VRaptorRequest %s]", this.getRequest());
	}

}
