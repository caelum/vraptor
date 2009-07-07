package br.com.caelum.vraptor.ioc.pico;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.NameBinding;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.lifecycle.JavaEE5LifecycleStrategy;
import org.picocontainer.lifecycle.LifecycleState;
import org.picocontainer.monitors.NullComponentMonitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author Fabio Kung
 */
public class VRaptorPicoContainer implements MutablePicoContainer {

    private final MutablePicoContainer containerForBundledComponents;

    private final MutablePicoContainer containerForCustomComponents;

    public VRaptorPicoContainer(PicoContainer parent) {
        this.containerForBundledComponents = new DefaultPicoContainer(new Caching(),
                new JavaEE5LifecycleStrategy(new NullComponentMonitor()), parent);
        this.containerForCustomComponents = new DefaultPicoContainer(new Caching(),
                new JavaEE5LifecycleStrategy(new NullComponentMonitor()), this.containerForBundledComponents);
    }

    public MutablePicoContainer addBundledComponent(Object componentKey, Object componentImplementationOrInstance,
                                                    Parameter... parameters) {
        containerForBundledComponents.addComponent(componentKey, componentImplementationOrInstance, parameters);
        return this;
    }

    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance,
                                             Parameter... parameters) {
        containerForCustomComponents.addComponent(componentKey, componentImplementationOrInstance, parameters);
        return this;
    }

    public MutablePicoContainer addBundledComponent(Object implOrInstance) {
        containerForBundledComponents.addComponent(implOrInstance);
        return this;
    }

    public MutablePicoContainer addComponent(Object implOrInstance) {
        containerForCustomComponents.addComponent(implOrInstance);
        return this;
    }

    public MutablePicoContainer addConfig(String name, Object val) {
        containerForCustomComponents.addConfig(name, val);
        return this;
    }

    public MutablePicoContainer addAdapter(ComponentAdapter<?> componentAdapter) {
        containerForCustomComponents.addAdapter(componentAdapter);
        return this;
    }

    public <T> ComponentAdapter<T> removeComponent(Object componentKey) {
        return containerForCustomComponents.removeComponent(componentKey);
    }

    public <T> ComponentAdapter<T> removeComponentByInstance(T componentInstance) {
        return containerForCustomComponents.removeComponentByInstance(componentInstance);
    }

    public MutablePicoContainer makeChildContainer() {
        return containerForCustomComponents.makeChildContainer();
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        containerForCustomComponents.addChildContainer(child);
        return this;
    }

    public boolean removeChildContainer(PicoContainer child) {
        return containerForCustomComponents.removeChildContainer(child);
    }

    public MutablePicoContainer change(Properties... properties) {
        containerForCustomComponents.change(properties);
        return this;
    }

    public MutablePicoContainer as(Properties... properties) {
        containerForCustomComponents.as(properties);
        return this;
    }

    public void setName(String name) {
        containerForBundledComponents.setName("VRaptor" + name + "Bundled");
        containerForCustomComponents.setName(name);
    }

    public void setLifecycleState(LifecycleState lifecycleState) {
        containerForBundledComponents.setLifecycleState(lifecycleState);
        containerForCustomComponents.setLifecycleState(lifecycleState);
    }

    public Object getComponent(Object componentKeyOrType) {
        return containerForCustomComponents.getComponent(componentKeyOrType);
    }

    public Object getComponent(Object componentKeyOrType, Type into) {
        return containerForCustomComponents.getComponent(componentKeyOrType, into);
    }

    public <T> T getComponent(Class<T> componentType) {
        return containerForCustomComponents.getComponent(componentType);
    }

    public <T> T getComponent(Class<T> componentType, Class<? extends Annotation> binding) {
        return containerForCustomComponents.getComponent(componentType, binding);
    }

    public List<Object> getComponents() {
        return containerForCustomComponents.getComponents();
    }

    public PicoContainer getParent() {
        return containerForBundledComponents.getParent();
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return containerForCustomComponents.getComponentAdapter(componentKey);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, NameBinding componentNameBinding) {
        return containerForCustomComponents.getComponentAdapter(componentType, componentNameBinding);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType, Class<? extends Annotation> binding) {
        return containerForCustomComponents.getComponentAdapter(componentType, binding);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return containerForCustomComponents.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return containerForCustomComponents.getComponentAdapters(componentType);
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType, Class<? extends Annotation> binding) {
        return containerForCustomComponents.getComponentAdapters(componentType, binding);
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return containerForCustomComponents.getComponents(componentType);
    }

    public void accept(PicoVisitor visitor) {
        containerForBundledComponents.accept(visitor);
        containerForCustomComponents.accept(visitor);

    }

    public void start() {
        containerForBundledComponents.start();
        containerForCustomComponents.start();
    }

    public void stop() {
        containerForCustomComponents.stop();
        containerForBundledComponents.stop();
    }

    public void dispose() {
        containerForCustomComponents.dispose();
        containerForBundledComponents.dispose();
    }

}
