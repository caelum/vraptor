---
title: Utility components
layout: page
section: 11
category: [en, docs]
---

<h3>Registering optional components</h3>

VRaptor has some optional components, inside package br.com.caelum.vraptor.util. For registering them you can add their packages on web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.one.package, 
        br.com.caelum.vraptor.util.other.package
    </param-value>
</context-param>
{% endhighlight %}

Or you can create a custom provider:

<ul>
	<li>
	Create a child class of your DI Profile (Spring is the default):

	{% highlight java %}
	package com.companyname.projectName;

	public class CustomProvider extends SpringProvider {
		
	}
	{% endhighlight %}
	</li>

	<li>
	Register this class as your DI provider on web.xml:

	{% highlight xml %}
	<context-param>
	   <param-name>br.com.caelum.vraptor.provider</param-name>
	   <param-value>com.companyname.projectName.CustomProvider</param-value>
	</context-param>
	{% endhighlight %}
	</li>

	<li>
	Override the registerCustomComponents method and add your optional components:

	{% highlight java %}
	package com.companyname.projectName;

	public class CustomProvider extends SpringProvider {
		
		@Override
		protected void registerCustomComponents(ComponentRegistry registry) {
		    registry.register(OptionalComponent.class, OptionalComponent.class);
		}
	}
	{% endhighlight %}

	</li>
</ul>

<h3>Available optional components</h3>

<h3>Hibernate Session and SessionFactory</h3>

If your components need Hibernate Session and SessionFactory, you will need a ComponentFactory to create them for you. If you use annotated entities, and you have a hibernate.cfg.xml in the root of WEB-INF/classes, you can use VRaptor's built-in ComponentFactory. VRaptor also has an interceptor that creates a session and begins a transaction on the beginning of the request, and closes (and commits or rollbacks) them on the end of the request. You can register these components by adding the package <strong>br.com.caelum.vraptor.util.hibernate</strong> on your web.xml configuration:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.other.packages...,
        br.com.caelum.vraptor.util.hibernate
    </param-value>
</context-param>
{% endhighlight %}

or register them manually on your custom provider:

{% highlight java %}
@Override
protected void registerCustomComponents(ComponentRegistry registry) {
    registry.register(SessionCreator.class, SessionCreator.class); //creates Session's
    registry.register(SessionFactoryCreator.class, 
        SessionFactoryCreator.class); //creates a SessionFactory
     
    registry.register(HibernateTransactionInterceptor.class,
        HibernateTransactionInterceptor.class); //open session and transaction in view
}
{% endhighlight %}

<h3>JPA EntityManager e EntityManagerFactory</h3>

If you have a persistence.xml with the persistence-unit called "default", you can use VRaptor3 built-in ComponentFactories for EntityManager and EntityManagerFactory, by adding the package br.com.caelum.vraptor.util.jpa on your web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.other.packages...,
        br.com.caelum.vraptor.util.jpa
    </param-value>
</context-param>
{% endhighlight %}

or add to your custom provider:

{% highlight java %}
@Override
protected void registerCustomComponents(ComponentRegistry registry) {
    registry.register(EntityManagerCreator.class, 
        EntityManagerCreator.class); //creates EntityManager's
    registry.register(EntityManagerFactoryCreator.class, 
        EntityManagerFactoryCreator.class); //creates an EntityManager
    registry.register(JPATransactionInterceptor.class, 
        JPATransactionInterceptor.class); //open EntityManager and Transaction in view
}
{% endhighlight %}

<h3>Localized Converters</h3>

There are some converters for Numbers that are localized, i.e, that consider your current Locale in order to convert request parameters. You can register them by adding the package br.com.caelum.vraptor.converter.l10n to your web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.other.packages...,
        br.com.caelum.vraptor.converter.l10n
    </param-value>
</context-param>
{% endhighlight %}

<h3>Immutable Parameters Instantiator (beta)</h3>

If you want to work with immutable objects in your project, you can use a parameter provider that is able to populate your objects via constructor parameters:

{% highlight java %}
@Resource
public class CarsController {
    public void wash(Car car) {
    
    }
}
public class Car {
   private final String color;
   private final String model;
   public Car(String color, String model) {
     this.color = color;
     this.model = model;
   }
   //getters
}
{% endhighlight %}

The car will be populated with the usual request parameters: car.color and car.model.
To enable this behavior, one can add the package br.com.caelum.vraptor.http.iogi to its web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.other.packages...,
        br.com.caelum.vraptor.http.iogi
    </param-value>
</context-param>
{% endhighlight %}

<h3>ExtJS Integration</h3>

There is a View that generates some ExtJS JSON formats. Use:

{% highlight java %}
result.use(ExtJSJson.class).....serialize();
{% endhighlight %}
