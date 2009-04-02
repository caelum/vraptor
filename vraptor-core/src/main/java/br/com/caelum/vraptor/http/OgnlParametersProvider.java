package br.com.caelum.vraptor.http;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import br.com.caelum.vraptor.http.ognl.ContainerBasedNullHandler;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class OgnlParametersProvider implements ParametersProvider {

    private final HttpServletRequest request;

    private final TypeCreator creator; 

    public OgnlParametersProvider(HttpServletRequest request, TypeCreator creator) {
        this.creator = creator;
        this.request = request;
        OgnlRuntime.setNullHandler(Object.class, new ContainerBasedNullHandler());
    }

    public Object[] getParametersFor(ResourceMethod method) {
        try {
            Class<?> type = creator.typeFor(method);
            Object root = type.getDeclaredConstructor().newInstance();
            for(String key : (Set<String>)request.getParameterMap().keySet()) {
                String[] values = request.getParameterValues(key);
                Ognl.setValue(key, root, values.length==1 ? values[0] : values);
            }
            return (Object[]) type.getDeclaredMethod("gimmeMyValues").invoke(root);
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
