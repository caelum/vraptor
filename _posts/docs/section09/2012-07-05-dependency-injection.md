---
title: Dependency injection
layout: page
language: en
section: 9
category: docs
---

VRaptor is strongly based on Dependency Injection, since all its internal components are managed using this technique.
Tha basic concept behind Dependency Injection (DI) says you should not look for what you want to access. Instead, it should be provided for you somehow.
In Java, this is accomplished by passing components to your controller's constructor. Suppose your clients controller needs to access a clients Dao. Specify that need in your code:

{% highlight java %}
@Resource
public class ClientController {
    private final ClientDao dao;
    
    public ClientController(ClientDao dao) {
        this.dao = dao;
    }

    @Post
    public void add(Client client) {
        this.dao.add(client);
    }
}
{% endhighlight %}

And annotate the ClientDao component as a VRaptor @Component:

{% highlight java %}
@Component
public class ClientDao {
}
{% endhighlight %}

From now on, VRaptor will provide your ClientController with an instance of ClientDao when needed. Remember that VRaptor will honor the scope specified by the component. For example, if ClientDao had specified Session scope (@SessionScoped), only one instance of that component would be created per session. (Note that it is probably wrong to specify session scope for a Dao, it is only a simple example).

<h3>ComponentFactory</h3>

Sometimes we want our components to receive other libraries' components. In that case we are unable to change the libraries's source code in order to annotate its components with @Component (and any other changes we may need to do).
The most common example is acquiring a Hibernate Session. We need to create a component that is responsible for providing Session instances for other components that depend on it.
VRaptor has an interface called ComponentFactory which allows your classes to provide components.
Classes implementing that interface define a single method. See the following example, which starts Hibernate when the component is built and uses that configuration to provide Session instances for our application:

{% highlight java %}
@Component
@ApplicationScoped
public class SessionFactoryCreator implements ComponentFactory<SessionFactory> {

    private SessionFactory factory;
    
    @PostConstruct
    public void create() {
        factory = new AnnotationConfiguration().configure().buildSessionFactory();
    }
    
    public SessionFactory getInstance() {
        return factory;
    }
    
    @PreDestroy
    public void destroy() {
        factory.close();
    }
}

@Component
@RequestScoped
public class SessionCreator implements ComponentFactory<Session> {

    private final SessionFactory factory;
    private Session session;

    public SessionCreator(SessionFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void create() {
        this.session = factory.openSession();
    }

    public Session getInstance() {
        return session;
    }

    @PreDestroy
    public void destroy() {
        this.session.close();
    }
}
{% endhighlight %}

These implementations are already on VRaptor3. Utils chapter will show you how to use them.

<h3>Providers</h3>

Behind the scenes, VRaptor uses a Depenency Injection container. There are three supported containers on VRaptor:

<ul>
<li><strong>Spring IoC:</strong> besides dependency injection, Spring IoC lets you to use any other Spring component along with VRaptor, without further configurations.</li>

<li><strong>Google Guice:</strong> a lighter and faster container, that lets you to have a better control of your dependency wiring, the use of the new javax.inject API, and some AOP features.</li>

<li><strong>Pico container:</strong> o lightweight and simple container, for those who will use no more than dependency injection.</li>
</ul>

To choose any of these containers, one have to put their jars on the classpath. The required jars can be found on lib/containers folders at vraptor zip. Para selecionar qualquer um desses containers basta colocars seus respectivos jars no classpath. Os jars estão disponíveis na pasta lib/containers do zip do VRaptor.
By default the containers will consider only classes under WEB-INF/classes folder, but you can also put annotated classes inside jars, if they include directory entries. ("Add directory entries" on eclipse, or ant without "files only" option). For enabling this you must put this parameter on web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>com.package.inside.the.jar</param-value>
</context-param>
{% endhighlight %}

<h3>Spring</h3>

When using Spring, you gain all its features and built-in components to use with VRaptor. In other words, all components that work with Sprint DI/IoC, also work with VRaptor. In that case, all the annotations.
To enable Spring on VRaptor, add all jars from lib/containers/spring on your app classpath.
VRaptor will use your Spring configurations, if you have it already configured in your project (Context listeners and applicationContext.xml on classpath). If VRaptor can't find your Spring configuration or you need to do more complex configurations, you can extend the Spring provider:
package com.yourapp;

{% highlight java %}
public class CustomProvider extends SpringProvider {

    @Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        registry.register(AClass.class, ImplementationOfThisClass.class);
        //...
    }

    @Override
    protected ApplicationContext getParentApplicationContext(ServletContext context) {
        ApplicationContext context = //configure your own application context
        return context;
    }
}
{% endhighlight %}

and to use this provider, add it to web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.provider</param-name>
    <param-value>com.yourapp.CustomProvider</param-value>
</context-param>
{% endhighlight %}

<h3>Google Guice</h3>

To enable Google Guice, add all jars from lib/containers/guice to your app classpath.
When using Guice you can choose to not use VRaptor's @Component annotation, and use guice or javax.inject annotations to control the injection.
If you need more complex configurations, just create a class that extends guice provider:

{% highlight java %}
public class CustomProvider extends GuiceProvider {

    @Override
    protected void registerCustomComponents(ComponentRegistry registry) {
        //binding only to AClass
        registry.register(AClass.class, ImplementationOfThisClass.class); 

        //binding on OtherClass and all its superclasses and interfaces
        registry.deepRegister(OtherClass.class); 
    }
    
    @Override
    protected Module customModule() {
        //you need to install super.customModule() if you
        //want to enable the registerCustomComponents method
        //and the classpath scanning
        final Module module = super.customModule(); 
        
        return new AbstractModule() {
           public void configure() {
                module.configure(binder());
                
                // custom guice bindings
           }
        };
    }
}
{% endhighlight %}

and to use this provider, add to the web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.provider</param-name>
    <param-value>com.yourapp.CustomProvider</param-value>
</context-param>
{% endhighlight %}

<h3>Pico Container</h3>

To enable Google Guice, add all jars from lib/containers/picocontainer to your app classpath.
