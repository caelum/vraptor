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
package br.com.caelum.vraptor.eval;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Evaluates expressions in order to access values.
 * 
 * @author guilherme silveira
 * 
 */
public class Evaluator {

	public Object get(Object root, String path) {
		String[] paths = path.split("[\\]\\.]");
		Object current = root;
		for (String p : paths) {
			try {
				current = navigate(current, p);
			} catch (InvocationTargetException e) {
				throw new VRaptorException("Unable to evaluate expression " + path, e.getCause());
			} catch (Exception e) {
				throw new VRaptorException("Unable to evaluate expression " + path, e);
			}
			if (current == null) {
				return "";
			}
		}
		return current;
	}

	private Object navigate(Object current, String path) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException {
		int index = path.indexOf("[");
		int position = -1;
		if (index != -1) {
			position = Integer.parseInt(path.substring(index + 1));
			path = path.substring(0, index);
		}
		Method method;
		try {
			method = current.getClass().getDeclaredMethod("get" + Info.capitalize(path));
		} catch (NoSuchMethodException e) {
			try {
				method = current.getClass().getDeclaredMethod("is" + Info.capitalize(path));
			} catch (NoSuchMethodException e1) {
				throw new InvocationTargetException(e1, "Unable to find get or is method.");
			}
		}
		method.setAccessible(true);
		Object instance = method.invoke(current);
		if (index != -1) {
			instance = access(instance, position);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private Object access(Object current, int position) throws InvocationTargetException {
		if (current.getClass().isArray()) {
			return Array.get(current, position);
		} else if (List.class.isAssignableFrom(current.getClass())) {
			return ((List) current).get(position);
		} else if (Collection.class.isAssignableFrom(current.getClass())) {
			Iterator it = ((Collection) current).iterator();
			for (int i = 0; i < position; i++) {
				it.next();
			}
			return it.next();
		}
		String msg = "Unable to access position of a" + current.getClass().getName() + ".";
		throw new InvocationTargetException(new VRaptorException(msg), msg);
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString("client.favoriteColors[1]".split("[\\]\\.]")));
	}
}
