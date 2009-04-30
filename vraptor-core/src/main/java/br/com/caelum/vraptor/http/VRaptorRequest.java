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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A request capable of adding new parameters.
 * @author guilherme silveira
 *
 */
public class VRaptorRequest extends HttpServletRequestWrapper{
	
	private static final Logger logger = LoggerFactory.getLogger(VRaptorRequest.class);
	
	private final Hashtable<String, String[]> extraParameters = new Hashtable<String,String[]>();

	public VRaptorRequest(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String name) {
		if(extraParameters.containsKey(name)) {
			String[] values = extraParameters.get(name);
			if(values.length==1) {
				return values[0];
			} else {
				return Arrays.toString(values);
			}
		}
		return super.getParameter(name);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getParameterNames() {
		return new IteratorEnumeration(getParameterMap().keySet().iterator());
	}
	
	@Override
	public String[] getParameterValues(String name) {
		if(extraParameters.containsKey(name)) {
			return extraParameters.get(name);
		}
		return super.getParameterValues(name);
	}
	
	@Override
	public Map getParameterMap() {
		Map complete = super.getParameterMap();
		complete.putAll(this.extraParameters);
		return complete;
	}

	public void setParameter(String key, String... value) {
		logger.debug("Setting " + key + " with " + value);
		this.extraParameters.put(key,value);
	}

}
