---
section: 5
title: VRaptor e Tiles 2.2
categories: [pt, cookbook]
layout: page
---

<h4>por Rogerio Alcantara no <a href="http://www.guj.com.br/posts/list/215206.java#1098196">post do GUJ</a></h4>

baixar o tiles-jsp.jar. Como estou utilizando maven2:

{% highlight xml %}
<dependency>
    <groupId>org.apache.tiles</groupId>
    <artifactId>tiles-jsp</artifactId>
    <version>2.2.2</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
{% endhighlight %}

No web.xml:

{% highlight xml %}
<!-- tiles configuration -->
<servlet>
    <servlet-name>TilesServlet</servlet-name>
    <servlet-class>org.apache.tiles.web.startup.TilesServlet</servlet-class>
    <init-param>
        <param-name>org.apache.tiles.factory.TilesContainerFactory.MUTABLE</param-name>
        <param-value>true</param-value>
    </init-param>
    <load-on-startup>2</load-on-startup>
</servlet>
<!-- /tiles configuration -->

<!-- vraptor configuration -->
<filter>
    <filter-name>vraptor</filter-name>
    <filter-class>br.com.caelum.vraptor.VRaptor</filter-class>
</filter>

<filter-mapping>
    <filter-name>vraptor</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>REQUEST</dispatcher>
</filter-mapping>
<!-- vraptor configuration -->
{% endhighlight %}

Repare que não é declarado o DEFINITIONS_CONFIG, nem registrado o TilesDispatchServlet. Pois não utilizaremos arquivo para guardar as definitions, e quem cuidara dos redirecionamentos continuará sendo o VRaptor, que já faz isso muito bem obrigado! ;D Outro detalhe importante, é deixar o TilesContainerFactory como MUTABLE. ;)

<h3>TilesPathResolver?</h3>

Nessa abordagem, não é necessário criar implementar esse PathResolver, já que essa responsabilidade continuará sendo do VRaptor. ;D

<strong>/WEB-INF/jsp/template.jsp</strong> - servirá de base para todas as páginas

{% highlight jsp %}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>site</title>
    </head>
    <body>
        <div id="divHeader">
            <tiles:insertTemplate template="/WEB-INF/jsp/header.jsp"/>
        </div>
        <div id="divContent">
            <tiles:insertAttribute name="body"/>
        </div>
        <div id="divFooter">
            <tiles:insertTemplate template="/WEB-INF/jsp/footer.jsp"/>
        </div>
    </body>
</html>
{% endhighlight %}

<strong>/WEB-INF/jsp/home/index.jsp</strong> - exemplo que utilizará o template - repare que o path mantém a convenção do VRaptor3! ^^

{% highlight jsp %}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<tiles:insertTemplate template="/WEB-INF/jsp/template.jsp">

    <tiles:putAttribute name="body">
        olá mundo!
    </tiles:putAttribute>

</tiles:insertTemplate>
{% endhighlight %}

<strong>HomeController.java</strong> - e finalmente o controler para redirecionar para index.jsp

{% highlight java %}
@Resource
public class HomeController {

    private Result result;

    public HomeController(final Result result) {

        super();
        this.result = result;
    }

    public void index() { }
}
{% endhighlight %}

Prontinho! Estamos utilizando o tiles apenas para montar o template das páginas, o redirecionamento continua sendo cargo do VRaptor! ^^
Só tem um detalhe que eu gostaria de compartilhar, pois me deu muito trabalho de descobrir: suponha que eu queria estender o template.jsp para incluir um outro body. (tive essa necessidade na seção about do site..)

<strong>/WEB-INF/jsp/about/about_base.jsp</strong> - base que estenderá a template.jsp para alterar o layout

{% highlight jsp %}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:insertTemplate template="/WEB-INF/jsp/template.jsp">

    <tiles:putAttribute name="body">

        <div id="divCenter">
            <tiles:insertAttribute name="content" />
        </div>

        <div id="divRight">
            <p>
            <tiles:insertAttribute name="content_right" />
            </p>
        </div>

    </tiles:putAttribute>

</tiles:insertTemplate>
{% endhighlight %}

Pronto, agora a minha página do about, estenderá about_base.jsp e não template.jsp, por exemplo.

<strong>/WEB-INF/jsp/about/whyUse.jsp</strong> - estenderá o about_base.jsp

{% highlight jsp %}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:insertTemplate template="/WEB-INF/jsp/about/about_base.jsp" flush="true">

    <tiles:putAttribute name="content" cascade="true">
        o conteúdo central!
    </tiles:putAttribute>

    <tiles:putAttribute name="content_right" cascade="true">
                o conteúdo da direita!
    </tiles:putAttribute>

</tiles:insertTemplate>
{% endhighlight %}

Repare que dessa vez, o insertTemplate possui o atributo flush="true": isso é importante para que a about_base.jsp seja reinderizado primeiro. Note também que os putAttributes possuem o atributo cascade="true": que serve para disponibilizar esses atributos "para os templates de cima".

Bom, isso foi bem chatinho de descobrir, mas agora a aplicação está rodando bunitinha, sem um XML, com o layout definido nas JSPs e com o mínimo de interferência no VRaptor3! ^^
