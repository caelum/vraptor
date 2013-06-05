---
title: VRaptor Scaffold
layout: page
section: 18
category: [en, docs]
---

VRaptor scaffold extension to make it easier configuring new projects and plugins.

<h3>Installation</h3>

Ensure you have installed ruby and rubygems. You can easily find more information how to do this in the follow address <a href="http://www.ruby-lang.org/pt/downloads">http://www.ruby-lang.org/pt/downloads</a>. After installed ruby stuffs open your terminal and run

{% highlight bash %}
gem install vraptor-scaffold
{% endhighlight %}

<h3>Getting started</h3>

Open your terminal again and run

{% highlight bash %}
vraptor new onlinestore
{% endhighlight %}

This command will create all configurations, after that go into onlinestore folder and run

{% highlight bash %}
ant jetty.run
{% endhighlight %}

open your browser in the follow address <a href="http://localhost:8080">http://localhost:8080</a> and you should see <strong>It works!</strong>.
Now lets create a CRUD to online store, to do that just run

{% highlight bash %}
vraptor scaffold product name:string value:double
{% endhighlight %}

and run server

{% highlight bash %}
ant jetty.run
{% endhighlight %}

Go <a href="http://localhost:8080/products">http://localhost:8080/products</a>

<h3>Package</h3>
The root default folder is app to change that you have the fallow command

{% highlight bash %}
vraptor new onlinestore --package=br.com.caelum
{% endhighlight %}

You can also change the model, controller and repository packages:

{% highlight bash %}
vraptor new onlinestore --package=br.com.caelum -m modelo -c controlador -r repositorio
{% endhighlight %}

<h3>Build: Maven, Gradle or Ivy</h3>

The default build tool is ant with ivy to deal with dependencies, to change your build tool, just create your application with:

{% highlight bash %}
# for maven
vraptor new onlinestore --build-tool=mvn

# for gradle
vraptor new onlinestore --build-tool=gradle
{% endhighlight %}

When using gradle, use

{% highlight bash %}
gradle jettyRun
{% endhighlight %}

to run the application.

<h3>ORM: JPA or Hibernate, connection pool</h3>

A new project already comes with the connection pool configured and in place. Besides that, one can choose between JPA (EntityManager, default), or Hibernate (Session), when creating your project:

{% highlight bash %}
vraptor new onlinestore -o=jpa
vraptor new onlinestore -o=hibernate
{% endhighlight %}

<h3>Freemarker</h3>
The default template engine is jsp, to change that create your application with

{% highlight bash %}
vraptor new onlinestore --template-engine=ftl
{% endhighlight %}

<h3>Eclipse</h3>

If you choose maven, run:

{% highlight bash %}
mvn eclipse:eclipse
{% endhighlight %}

to create eclipse files.
If you are using ant eclipse files are generated with the application to skip them run

{% highlight bash %}
vraptor new onlinestore --skip-eclipse
{% endhighlight %}

<h3>Supported attributes type</h3>

The supported attributes type are: boolean, double, float, short, integer, long, string, text, date and references.

<h3>Plugins</h3>

Vraptor plugin can be installed by issuing a

{% highlight bash %}
vraptor plugin simple-email -v 1.0.0
{% endhighlight %}

You can find a list of available plugins at <a href="https://github.com/caelum/vraptor-contrib">https://github.com/caelum/vraptor-contrib</a>

<h3>jQuery</h3>

The jQuery version is always the latest available, you can choose older version with:

{% highlight bash %}
vraptor new onlinestore -j 1.4.4
{% endhighlight %}

<h3>Heroku</h3>

Now you can deploy any kind of Java application on Heroku with a simple

{% highlight bash %}
git push heroku master
{% endhighlight %}

And vraptor-scaffold take care with all the stuff you need to getting started with Heroku. To create an application that uses the heroku workflow just run:

{% highlight bash %}
vraptor new onlinestore --heroku
{% endhighlight %}

After that you need to follow the Heroku steps to get done. You can find more information here <a href="http://www.heroku.com/java">http://www.heroku.com/java</a>

<h3>Help command</h3>

To get hold of all available commands execute

{% highlight bash %}
vraptor -h
{% endhighlight %}

To get more information on a command usage, use, for example:

{% highlight bash %}
vraptor new -h 
vraptor scaffold -h 
vraptor plugin -h
{% endhighlight %}

<h3>Contributing</h3>

This project is being developed in ruby and the source is hosted in <a href="https://github.com/caelum/vraptor-scaffold">https://github.com/caelum/vraptor-scaffold</a>. Feel free to fork and create your path or features, dont forget the tests.
