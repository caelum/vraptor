package br.com.caelum.vraptor.vraptor2;

import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.resource.Resource;

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

}
