---
title: Google App Engine
layout: page
language: pt
section: 14
category: docs
---

<h3>Começando um projeto</h3>

Devido às restrições de segurança na sandbox do Google App Engine, alguns componentes do VRaptor3 precisam ser substituídos e uma seleção diferente de dependências deve ser usada. Uma versão do blank-project já contendo estas alterações está disponível em nossa página de downloads.

<h3>Configuração</h3>

Para habilitar os componentes do VRaptor para o Google App Engine, você precisa adicionar a seguinte configuração no seu web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>br.com.caelum.vraptor.gae</param-value>
</context-param>
{% endhighlight %}

<h3>Limitações</h3>

Um detalhe importante é que a injeção de dependências não funciona no redirecionamento para lógicas; o controlador é instanciado recebendo null em todos os seus parâmetros. Sendo assim, deve-se evitar chamadas 
como:

{% highlight java %}
result.use(Results.logic()).redirectTo(SomeController.class).someMethod();
validator.onErrorUse(Results.logic()).of(SomeController.class).someMethod();
{% endhighlight %}

preferindo Results.page() - ou então escrever seus controllers de forma a esperar pelos valores nulos.

<h3>Problemas comuns</h3>

O ambiente de execução do App Engine não habilita o suporte a Expression Language por padrão, nem suporta a tag <jsp-config/jsp-proprerty-group/el-ignored>. Assim sendo, para habilitar o suporte a EL, é necessário adicionar a seguinte linha nos arquivos JSP:

{% highlight jsp %}
<%@ page isELIgnored="false" %>
{% endhighlight %}

No caso de arquivos de tags, deve-se adicionar:

{% highlight jsp %}
<%@ tag isELIgnored="false" %>
{% endhighlight %}

<h3>JPA 2</h3>

O VRaptor possui suporte ao JPA nas versões 1 e 2, porém o ambiente do Google App Engine suporta apenas JPA 1. Por isso você deve evitar de copiar o JAR jpa-api-2.0.jar para seu projeto.
