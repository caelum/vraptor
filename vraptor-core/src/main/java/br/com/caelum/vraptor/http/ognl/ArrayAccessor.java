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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import ognl.ArrayPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Deals with acessing values within an array.<br>
 * Re-instantiates and invokes setter methods on arrays as soon as they are not
 * big enough for all required parameters.
 * 
 * @author Guilherme Silveira
 */
public class ArrayAccessor extends ArrayPropertyAccessor {

    @SuppressWarnings("unchecked")
    @Override
    public Object getProperty(Map context, Object target, Object object) throws OgnlException {
        try {
            return super.getProperty(context, target, object);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setProperty(Map context, Object array, Object key, Object value) throws OgnlException {

        int index = (Integer) key;
        int length = Array.getLength(array);
        if (length <= index) {
            array = copyOf(array, index, length);
            OgnlContext ctx = (OgnlContext) context;
            String fieldName = ctx.getCurrentEvaluation().getPrevious().getNode().toString();
            Object origin = ctx.getCurrentEvaluation().getPrevious().getSource();
            Method setter = ReflectionBasedNullHandler.findMethod(origin.getClass(),
                    "set" + Info.capitalize(fieldName), origin.getClass(), null);
            Container container = (Container) context.get(Container.class);
            EmptyElementsRemoval removal = container.instanceFor(EmptyElementsRemoval.class);
            removal.add(array, setter, origin);
            try {
                setter.invoke(origin, array);
            } catch (IllegalArgumentException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            }
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
