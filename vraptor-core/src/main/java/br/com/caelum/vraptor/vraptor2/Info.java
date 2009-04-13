package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;

import org.vraptor.annotations.Component;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.resource.Resource;

/**
 * VRaptor 2 util methods.
 * 
 * @author Guilherme Silveira
 */
public class Info {

    public static final String[] OLD_COMPONENT_TERMINATIONS = { "Controller", "Logic", "Command", "Action",
            "Component", "Manager" };

    public static String getComponentName(Class<?> type) {
        Component component = type.getAnnotation(Component.class);
        String componentName = component.value();
        if (componentName.equals("")) {
            return getComponentNameForTypeWithoutAnnotationValue(type);
        }
        return componentName;
    }

    public static boolean isOldComponent(Resource resource) {
        Class<?> type = resource.getType();
        return type.isAnnotationPresent(Component.class);
    }

    public static String getLogicName(Method method) {
        Logic logic = method.getAnnotation(Logic.class);
        if (logic == null || logic.value().length == 0) {
            return method.getName();
        }
        return logic.value()[0];
    }

    public static String capitalize(String name) {
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static String getComponentNameForTypeWithoutAnnotationValue(Class<?> type) {
        String name = removeEnding(type.getSimpleName(), OLD_COMPONENT_TERMINATIONS);
        if (!name.equals(type.getSimpleName())) {
            // removed some endings --> lowercase the type name without the
            // ending
            return name.toLowerCase();
        } else {
            // did not remove the ending, therefore the component name is the
            // type name
            return type.getSimpleName();
        }
    }

    private static String removeEnding(String string, String[] terminations) {
        for (String ending : terminations) {
            if (string.endsWith(ending)) {
                return string.substring(0, string.length() - ending.length());
            }
        }
        return string;
    }

}
