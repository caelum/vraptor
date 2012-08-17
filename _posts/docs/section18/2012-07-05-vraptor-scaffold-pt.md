---
title: VRaptor Scaffold
layout: page
language: pt
section: 18
category: [pt, docs]
---

O VRaptor 3 agora possui uma extensão chamada VRaptor Scaffold, que ter por finalidade facilitar a configuração de novos projetos e plugins.

<h3>Instalação</h3>

Para instalar o vraptor scaffold é necessário ter instalado o ruby e o rubygems. Você pode encontrar informações de instalação na <a href="http://www.ruby-lang.org/pt/downloads">página de downloads do Ruby</a>. Tendo isso instalado basta executar o comando a seguir.

{% highlight bash %}
gem install vraptor-scaffold
{% endhighlight %}

<h3>Começando um projeto</h3>

Abra um terminal e digite

{% highlight bash %}
vraptor new onlinestore
{% endhighlight %}

Esse comando vai criar toda a estrutra da aplicação, após isso entre na pasta onlinestore e execute a task jetty do ant

{% highlight bash %}
ant jetty.run
{% endhighlight %}

Abra o browser no endereço <a href="http://localhost:8080">http://localhost:8080</a> e você deve ver <strong>It works!</strong>.
Agora vamos criar um cadastro completo(CRUD) de produtos para nossa loja virtual, para isso basta executar

{% highlight bash %}
vraptor scaffold product name:string value:double
{% endhighlight %}

Execute novamente

{% highlight bash %}
ant jetty.run
{% endhighlight %}

Acesse <a href="http://localhost:8080/products">http://localhost:8080/products</a>

<h3>Package</h3>
O pacote raiz por padrão é app, para mudar isso crie a aplicação com o seguinte comando

{% highlight bash %}
vraptor new onlinestore --package=br.com.caelum
{% endhighlight %}

Você também pode configurar os pacotes de modelos, controllers e repositórios:

{% highlight bash %}
vraptor new onlinestore --package=br.com.caelum -m modelo -c controlador -r repositorio
{% endhighlight %}

<h3>Build: Maven, Gradle ou Ivy</h3>

O vraptor-scaffold gera um projeto com ant e ivy por padrão, mas você pode escolher outra ferramenta de build com um simples comando na hora de criar seu projeto:

{% highlight bash %}
# for maven
vraptor new onlinestore --build-tool=mvn

# for gradle
vraptor new onlinestore --build-tool=gradle
{% endhighlight %}

Ao usar gradle, utilize

{% highlight bash %}
gradle jettyRun
{% endhighlight %}

para rodar a aplicação.

<h3>ORM: JPA ou Hibernate, connection pool</h3>

Um projeto novo já vem por padrão com o c3p0 configurado. Além disso você pode escolher entre JPA (EntityManager, padrão), ou Hibernate (Session), ao criar seu projeto:

{% highlight bash %}
vraptor new onlinestore -o=jpa
vraptor new onlinestore -o=hibernate
{% endhighlight %}

<h3>Freemarker</h3>
O template engine padrão é jsp, para utilizar o freemarker, crie a aplicação com

{% highlight bash %}
vraptor new onlinestore --template-engine=ftl
{% endhighlight %}

<h3>Eclipse</h3>

Se você optou pelo maven execute

{% highlight bash %}
mvn eclipse:eclipse
{% endhighlight %}

Para gerar os arquivos de configuração do eclipse, após isso apenas faça a importação do projeto normalmente.
Se você optou pelo ant os arquivos de configuração serão gerados no momento em que criar o projeto, não se esqueça de executar

{% highlight bash %}
ant compile
{% endhighlight %}

para baixar todas as dependências antes de importar o projeto.
É possível pular a criação desses arquivos com o comando

{% highlight bash %}
vraptor new onlinestore --skip-eclipse
{% endhighlight %}

<h3>Tipos de atributos suportados</h3>

É possível gerar um CRUD com os seguintes atributos: boolean, double, float, short, integer, long, string e text.

<h3>Plugins</h3>

Plugins do vraptor são facilmente instalados através do comando

{% highlight bash %}
vraptor plugin simple-email -v 1.0.0
{% endhighlight %}

Você pode encontrar uma lista de plugins disponíveis no <a href="https://github.com/caelum/vraptor-contrib">repositório GitHub do vraptor-contrib</a>

<h3>jQuery</h3>

A versão do jQuery é sempre a última disponível para download, para utilizar uma versão anterior use o comando:

{% highlight bash %}
vraptor new onlinestore -j 1.4.4
{% endhighlight %}

<h3>Heroku</h3>

Agora é possível fazer um deploy de qualquer aplicação java no Heroku com um simples:

{% highlight bash %}
git push heroku master
{% endhighlight %}

E o vraptor-scaffold já tem um comando que gera toda a aplicação do jeito que o Heroku espera. Para isso basta utilizar o comando:

{% highlight bash %}
vraptor new onlinestore --heroku
{% endhighlight %}

Depois é só fazer o deploy da sua aplicação seguindo os passos do Heroku. Para mais informações acesse o site do <a href="http://www.heroku.com/java">Heroku para Java</a>.

<h3>Comando Help</h3>

Para visualizar a lista de comandos disponibilizados pelo vraptor-scaffold, execute:

{% highlight bash %}
vraptor -h
vraptor new -h 
vraptor scaffold -h 
vraptor plugin -h
{% endhighlight %}

<h3>Contribuindo</h3>

O projeto está sendo desenvolvido em ruby e o <a href="https://github.com/caelum/vraptor-scaffold">código fonte</a> está hospedado no github. Você pode colaborar com o projeto fazendo o fork e enviando seu path ou uma nova funcionalide. Não se esqueça dos testes.
