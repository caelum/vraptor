Para contribuir com a documentação do VRaptor, adicionando novos textos ou traduzindo os já existentes, basta acessar a branch gh-pages do repositório, efetuar as alterações desejadas nas seções da pasta _posts e commitá-las.

Criando um novo capítulo
========================

Para que não haja erros, ele deverá conter o seguinte cabeçalho:

\-\-\-  
title: Título do capítulo   
section: Índice do último capítulo existente + 1  
layout: page 
category: [Abreviação do idioma do capítulo: pt/en/etc , docs (para documentação) ou cookbook.] Os dois dados entre colchetes e separados por uma vírgula.
\-\-\-

Para o nome do arquivo, siga o padrão YYYY-MM-DD-titulo-do-capitulo.md

Salve o arquivo na pasta docs ou cookbook, de acordo com o que for passado no campo "category", acima.


Traduzindo um capítulo existente
================================

Para a tradução, basta criar um novo arquivo, mantendo inalterados os campos section e layout. Altere apenas o primeiro item do campo category (abreviação do idioma) e traduza o title adequadamente.

Recomendações
=============

Coloque trechos de código entre a seguinte tag para o highlight:

{% highlight nome-da-linguagem %}  
{% endhighlight %}

Atente para certos caracteres que podem causar erros na criação da página:

{{, _, []

Nesses casos, opte pelas seguintes alternativas:

{ {, &\#95;, &\#91;&\#93;
