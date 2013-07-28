---
title: Componentes utilitários opcionais
layout: page
section: 11
categories: [pt, docs]
---

<h3>Registrando um componente opcional</h3>

O VRaptor possui alguns componentes opcionais, que estão no pacote br.com.caelum.vraptor.util. Para registrá-los você pode adicionar seus pacotes no web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.um.pacote,
        br.com.caelum.vraptor.util.outro.pacote
    </param-value>
</context-param>
{% endhighlight %}

ou você pode criar um custom provider:

<ul>
	<li>
	Crie uma classe filha do Provider que você está usando:

	{% highlight java %}
	package br.com.nomedaempresa.nomedoprojeto;

	public class CustomProvider extends SpringProvider {

	}
	{% endhighlight %}
	</li>

	<li>Registre essa classe como provider no web.xml:

	{% highlight xml %}
	<context-param>
		<param-name>br.com.caelum.vraptor.provider</param-name>
		<param-value>br.com.nomedaempresa.nomedoprojeto.CustomProvider</param-value>
	</context-param>
	{% endhighlight %}
	</li>

	<li>
	Sobrescreva o método registerCustomComponents e adicione os componentes opcionais:

	{% highlight java %}
	package br.com.nomedaempresa.nomedoprojeto;

	public class CustomProvider extends SpringProvider {

		@Override
		protected void registerCustomComponents(ComponentRegistry registry) {
		    registry.register(ComponenteOpcional.class, ComponenteOpcional.class);
		}
	}
	{% endhighlight %}
	</li>
</ul>

<h3>Componentes opcionais disponíveis</h3>

<h3>Converters Localizados</h3>

Existem alguns converters para números que são localizados, ou seja, que consideram o Locale atual para converter os parâmetros. Você pode registrá-los adicionando o pacote br.com.caelum.vraptor.converter.l10n no seu web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        br.com.caelum.vraptor.util.outros.pacotes...,
        br.com.caelum.vraptor.converter.l10n
    </param-value>
</context-param>
{% endhighlight %}

<h3>Instanciador de Parâmetros: IOGI x OGNL</h3>

O VRaptor utiliza por padrão o IOGI para injetar os parâmetros nos objetos. Se você preverir é possível usar o OGNL, bastando remover o jar do IOGI e adicionar os jars do OGNL.

<h3>Integração com ExtJS</h3>

Existe uma View do VRaptor que consegue gerar alguns formatos de JSON que o ExtJS espera. Para isso use:

{% highlight java %}
result.use(ExtJSJson.class).....serialize();
{% endhighlight %}

<h3>Registrando um plugin automaticamente</h3>

Para registrar um plugin automaticamente, basta adicionar o arquivo META-INF/br.com.caelum.vraptor.packages dentro do jar, contendo os pacotes do plugin, um por linha.

<h3>Integração JPA e Hibernate</h3>

Há três projetos externos ao VRaptor para integração persistência. Para cada um deles, basta adicionar o jar no classpath que o suporte será ativado automaticamente sem a necessidade de registrar os componentes.

<ul>
  <li><a href="https://github.com/caelum/vraptor-jpa">vraptor-jpa</a>, para suporte a JPA</li>
  <li><a href="https://github.com/caelum/vraptor-hibernate">vraptor-hibernate</a>, para suporte ao Hibernate 3</li>
  <li><a href="https://github.com/garcia-jj/vraptor-plugin-hibernate4">vraptor-plugin-hibernate4</a>, para suporte ao Hibernate 4</li>
</ul>

O projeto <a href="https://github.com/caelum/vraptor-jpa">vraptor-jpa</a> é disponibilizado sem nenhum provider. Então para usá-lo você precisa 
incluir o provider de sua preferência.

Para usar o Hibernate como provider:

{% highlight xml %}
	<dependency>
		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-entitymanager</artifactId>
		<version>4.0.1.Final</version>
	</dependency>
{% endhighlight %}


Para usar o Eclipselink como provider:

{% highlight xml %}
	<dependency>
		<groupId>org.eclipse.persistence</groupId>
		<artifactId>javax.persistence</artifactId>
		<version>2.0.0</version>
	</dependency>
{% endhighlight %}

<br /><br />
