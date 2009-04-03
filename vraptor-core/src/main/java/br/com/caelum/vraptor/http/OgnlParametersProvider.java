package br.com.caelum.vraptor.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import br.com.caelum.vraptor.converter.OgnlBasedConverterController;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ognl.ContainerBasedNullHandler;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class OgnlParametersProvider implements ParametersProvider {

    private final HttpServletRequest request;

    private final TypeCreator creator;

    private final Container container;

    private final Converters converters; 

    public OgnlParametersProvider(HttpServletRequest request, TypeCreator creator, Container container, Converters converters) {
        this.creator = creator;
        this.request = request;
        this.container = container;
        this.converters = converters;
        OgnlRuntime.setNullHandler(Object.class, new ContainerBasedNullHandler());
    }

    public Object[] getParametersFor(ResourceMethod method) {
        try {
            Class<?> type = creator.typeFor(method);
            Object root = type.getDeclaredConstructor().newInstance();
            Map context = Ognl.createDefaultContext(root);
            context.put(Container.class,this.container);
            Ognl.setTypeConverter(context, new OgnlBasedConverterController(converters));
            for(String key : (Set<String>)request.getParameterMap().keySet()) {
                String[] values = request.getParameterValues(key);
                //Map<Class,Object> context= new HashMap<Class,Object>();
                Ognl.setValue(key, context,root, values.length==1 ? values[0] : values);
            }
            Type[] types = method.getMethod().getGenericParameterTypes();
            Object[] result = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                Type paramType = types[i];
                result[i] = root.getClass().getMethod("get" + creator.nameFor(paramType)).invoke(root);
            }
            return result;
        } catch (InstantiationException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (IllegalAccessException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (IllegalArgumentException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (SecurityException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (InvocationTargetException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (NoSuchMethodException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (OgnlException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        }
    }
}
