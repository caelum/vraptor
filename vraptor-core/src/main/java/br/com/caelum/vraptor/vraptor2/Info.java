package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class Info {

    public static String getComponentName(Class<?> type) {
        Component component = type.getAnnotation(Component.class);
        String componentName = component.value();
        if(componentName.equals("")) {
            componentName = type.getSimpleName();
        }
        return componentName;
    }

    public static boolean isOldComponent(Resource resource) {
        Class<?> type = resource.getType();
        return type.isAnnotationPresent(Component.class);
    }

    public static String getLogicName(Method method) {
        Logic logic = method.getAnnotation(Logic.class);
        String logicName = (logic==null || logic.value()==null || logic.value().length==0) ? method.getName() : logic.value()[0];
        return logicName;
    }

}
