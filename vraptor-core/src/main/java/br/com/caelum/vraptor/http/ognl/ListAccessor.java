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
package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.Evaluation;
import ognl.ListPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.OgnlToConvertersController;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * This list accessor is responsible for setting null values up to the list
 * size.<br>
 * Compatibility issues might arrive (in previous vraptor versions, the object
 * was instantiated instead of null being set).
 * 
 * @author Guilherme Silveira
 * 
 */
public class ListAccessor extends ListPropertyAccessor {

	@SuppressWarnings("unchecked")
	public Object getProperty(Map context, Object target, Object value) throws OgnlException {
		try {
			return super.getProperty(context, target, value);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setProperty(Map context, Object target, Object key, Object value) throws OgnlException {
		// code comments by Guilherme Silveira in a moment of rage agains ognl
		// code
		List<?> list = (List<?>) target;
		int index = (Integer) key;
		for (int i = list.size(); i <= index; i++) {
			list.add(null);
		}
		if (value instanceof String) {
			// it might be that suckable ognl did not call convert, i.e.: on the
			// values[i] = 2l in a List<Long>.
			// we all just looooove ognl.
			OgnlContext ctx = (OgnlContext) context;
			// if direct injecting, cannot find out what to do, use string
			if (ctx.getRoot() != target) {
				Evaluation eval = ctx.getCurrentEvaluation();
				Evaluation previous = eval.getPrevious();
				String fieldName = previous.getNode().toString();
				Object origin = previous.getSource();
				Method getter = ReflectionBasedNullHandler.findMethod(origin.getClass(), "get"
						+ Info.capitalize(fieldName), origin.getClass(), null);
				Type genericType = getter.getGenericReturnType();
				Class type = OgnlToConvertersController.rawTypeOf(genericType);
				if (!type.equals(String.class)) {
					// suckable ognl doesnt support dependency injection or
					// anything alike... just that suckable context... therefore
					// procedural
					// programming and ognl live together forever!
					Container container = (Container) context.get(Container.class);
					Converter<?> converter = container.instanceFor(Converters.class).to(type, container);
					List<ValidationMessage> errors = (List<ValidationMessage>) context.get("errors");
					ResourceBundle bundle = (ResourceBundle) context.get(ResourceBundle.class);
					Object result = converter.convert((String) value, type, bundle);
					super.setProperty(context, target, key, result);
					return;
				}
			}
		}
		super.setProperty(context, target, key, value);
	}

}
