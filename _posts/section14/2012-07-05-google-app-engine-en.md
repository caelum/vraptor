---
title: Google App Engine
layout: page
language: en
section: 14
---

<h3>Starting a new project</h3>

Due to security restrictions on Google App Engine's sandbox, some of VRaptor3's components must be replaced, and a different selection of dependencies must be used. A version of the blank project featuring these modifications is available at our download page.

<h3>Configuration</h3>

To enable VRaptor components replaced to Google App Engine, you need add the lines bellow in your web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>br.com.caelum.vraptor.gae</param-value>
</context-param>
{% endhighlight %}

<h3>Limitations</h3>

A relevant detais is that the dependency injection does not work when redirecting from one logic to another; the controller is instantiated by filling with null all of its arguments. This said, one should avoid call like:

{% highlight java %}
result.use(Results.logic()).redirectTo(SomeController.class).someMethod();
validator.onErrorUse(Results.logic()).of(SomeController.class).someMethod();
{% endhighlight %}

using, instead, Results.page(). An alternative would be to prepare your controllers to expect null as construction arguments.

<h3>Troubleshooting</h3>

App Engine's execution environment nor does enable support for Expression Language by default, nor supports the <jsp-config/jsp-proprerty-group/el-ignored> tag. In this situation, to enable the EL support, it's required to add the following line to your JSP files:

{% highlight jsp %}
<%@ page isELIgnored="false" %>
For tag files, use:
<%@ tag isELIgnored="false" %>
{% endhighlight %}

<h3>JPA 2</h3>

VRaptor supports JPA versions 1 and 2, but Google App Engine only supports JPA 1. Therefore you should avoid copying the jpa-api-2.0.jar file to your project.
