package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import ognl.ArrayPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.vraptor2.Info;

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
            Method setter = ReflectionBasedNullHandler.findMethod(origin.getClass(), "set" + Info.capitalize(fieldName), origin.getClass());
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
