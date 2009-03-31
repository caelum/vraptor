package br.com.caelum.vraptor.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import javax.servlet.http.HttpServletRequest;

import ognl.Ognl;
import ognl.OgnlException;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class OgnlParametersProvider implements ParametersProvider {

    private final HttpServletRequest request;
    
    private static int classLoadCounter = 0; 

    public OgnlParametersProvider(HttpServletRequest request) {
        this.request = request;
    }

    public Object[] getParametersFor(ResourceMethod method) {
        Method reflectionMethod = method.getMethod();
        ClassPool pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
        CtClass ctType = pool.makeClass(reflectionMethod.getDeclaringClass().getName() + "$"
                + reflectionMethod.getName() + "$" + Math.abs(reflectionMethod.hashCode()) + "$" + (++classLoadCounter));
        String valueLists = "";
        for (Class<?> type : reflectionMethod.getParameterTypes()) {
            try {
                CtClass fieldType = pool.get(type.getName());
                String fieldName = type.getSimpleName();
                CtField field = new CtField(fieldType, fieldName, ctType);
                ctType.addField(field);
                ctType.addMethod(CtNewMethod.getter("get" + fieldName, field));
                ctType.addMethod(CtNewMethod.setter("set" + fieldName, field));
                if(!valueLists.equals("")) {
                    valueLists += ",";
                }
                valueLists += fieldName;
            } catch (CannotCompileException e) {
                // TODO validation exception?
                throw new IllegalArgumentException("unable to compile expression");
            } catch (NotFoundException e) {
                // TODO validation exception?
                throw new IllegalArgumentException("unable to add field " + type.getName());
            }
        }
        try {
            ctType.addMethod(CtNewMethod.make("public Object[] gimmeMyValues() { return new Object[]{" + valueLists + "};}", ctType));
        } catch (CannotCompileException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression");
        }
        try {
            Class<?> type = ctType.toClass();
            Object root = type.getDeclaredConstructor().newInstance();
            for(String key : (Set<String>)request.getParameterMap().keySet()) {
                String[] values = request.getParameterValues(key);
                Ognl.setValue(key, root, values.length==1 ? values[0] : values);
            }
            return (Object[]) type.getDeclaredMethod("gimmeMyValues").invoke(root);
        } catch (CannotCompileException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
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
