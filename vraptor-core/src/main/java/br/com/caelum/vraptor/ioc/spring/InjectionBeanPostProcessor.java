package br.com.caelum.vraptor.ioc.spring;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import br.com.caelum.vraptor.In;

/**
 * Enhances the default behavior from Spring, adding support to injection
 * through not annotated constructor, if there is only one.
 *
 * @author Fabio Kung
 */
public class InjectionBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {


    public InjectionBeanPostProcessor() {
        this.setAutowiredAnnotationType(In.class);
    }

    public Constructor[] determineCandidateConstructors(Class beanClass, String beanName) throws BeansException {
        Constructor[] candidates = super.determineCandidateConstructors(beanClass, beanName);
        if (candidates == null) {
            Constructor constructor = checkIfThereIsOnlyOneNonDefaultConstructor(beanClass);
            if (constructor != null) {
                candidates = new Constructor[]{constructor};
            }
        }
        return candidates;
    }

    private Constructor checkIfThereIsOnlyOneNonDefaultConstructor(Class beanClass) {
        Constructor[] constructors = beanClass.getDeclaredConstructors();
        if (constructors.length == 1) {
            if (constructors[0].getParameterTypes().length > 0) {
                return constructors[0];
            }
        }
        return null;
    }
}
