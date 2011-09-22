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
package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;
import ognl.ArrayPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.StringUtils;

/**
 * Deals with acessing values within an array.<br>
 * Re-instantiates and invokes setter methods on arrays as soon as they are not
 * big enough for all required parameters.
 *
 * @author Guilherme Silveira
 */
public class ArrayAccessor extends ArrayPropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object object) throws OgnlException {
        try {
            return super.getProperty(context, target, object);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    @Override
    public void setProperty(Map context, Object array, Object key, Object value) throws OgnlException {

        int index = (Integer) key;
        int length = Array.getLength(array);
        if (length <= index) {
            Object newArray = copyOf(array, index, length);
            OgnlContext ctx = (OgnlContext) context;
            if (array == ctx.getRoot()) {
            	ctx.setRoot(newArray);
            } else {
	            String fieldName = ctx.getCurrentEvaluation().getPrevious().getNode().toString();
	            Object origin = ctx.getCurrentEvaluation().getPrevious().getSource();
	            
	            Proxifier proxifier = (Proxifier) context.get("proxifier");
	            Method setter = new ReflectionBasedNullHandler(proxifier).findMethod(origin.getClass(),
	                    "set" + StringUtils.capitalize(fieldName), origin.getClass(), null);
	            
	            EmptyElementsRemoval removal = (EmptyElementsRemoval) context.get("removal");
	            removal.add(newArray, setter, origin);

	            new Mirror().on(origin).invoke().method(setter).withArgs(newArray);
            }
            array = newArray;
        }
        super.setProperty(context, array, key, value);
    }

    private Object copyOf(Object array, int desiredLength, int currentLength) {
        Object newArray = Array.newInstance(array.getClass().getComponentType(), desiredLength + 1);
        for (int i = 0; i < currentLength; i++) {
            Array.set(newArray, i, Array.get(array, i));
        }
        for (int i = currentLength; i < desiredLength; i++) {
            Array.set(newArray, i, null);
        }
        array = newArray;
        return array;
    }

}
