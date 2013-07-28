---
title: Utility components
layout: page
section: 11
categories: [en, docs]
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

<h3>Parameters Instantiator: IOGI x OGNL</h3>

VRaptor uses IOGI by default as parameter instantiator. If you want to use OGNL you can remove IOGI jar and adding OGNL jars to your application.

<h3>ExtJS Integration</h3>

There is a View that generates some ExtJS JSON formats. Use:

{% highlight java %}
result.use(ExtJSJson.class).....serialize();
{% endhighlight %}

<h3>Integration with JPA and Hibernate</h3>

There are three sub projects for persistence integrations. For each one you need only to add the jar in your classpath without any other configuration.

<ul>
  <li><a href="http://github.com/caelum/vraptor-jpa">vraptor-jpa</a>, for JPA support</li>
  <li><a href="http://github.com/caelum/vraptor-hibernate">vraptor-jpa</a>, for Hibernate 3 support</li>
  <li><a href="http://github.com/garcia-jj/vraptor-hibernate4">vraptor-jpa</a>, for Hibernate 4 support</li>
</ul>

The <a href="http://github.com/caelum/vraptor-jpa">vraptor-jpa</a> project is released without any provider. If you want to use then, you need to declare a provider as you prefer.

To use Hibernate as provider:

{% highlight xml %}
	<dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-entitymanager</artifactId>
		<version>4.0.1.Final</version>
	</dependency>
{% endhighlight %}


To use Eclipselink as provider:

{% highlight xml %}
	<dependency>
		<groupId>org.eclipse.persistence</groupId>
		<artifactId>javax.persistence</artifactId>
		<version>2.0.0</version>
	</dependency>
{% endhighlight %}

<br /><br />
