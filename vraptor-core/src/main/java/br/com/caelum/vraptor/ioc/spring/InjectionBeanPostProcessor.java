
package br.com.caelum.vraptor.ioc.spring;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

/**
 * Enhances the default behavior from Spring, adding support to injection
 * through not annotated constructor, if there is only one.
 *
 * @author Fabio Kung
 */
class InjectionBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {


    //  in case we are required to change the injection annotation:
    //  public InjectionBeanPostProcessor() {
    //      this.setAutowiredAnnotationType(In.class);
    //  }

    @Override
	@SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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
