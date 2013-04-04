---
title: Como contribuir com o VRaptor
layout: page
section: 20
categories: [pt, docs]
---


<h3>Participando das listas de discussão</h3>

Você pode responder às dúvidas dos outros usuários no sub-fórum "Frameworks e Bibliotecas brasileiros" no <a href="http://guj.com.br/forums/show/23.java">GUJ</a> ou no <a href="http://groups.google.com/group/caelum-vraptor">Google Groups</a>.


<h3>Colaborando com documentação</h3>

Você pode ajudar escrevendo Javadocs, melhorando o conteúdo do site, com alguma receita ou com algum artigo em seu blog.


<h3>Reportando bugs e sugerindo novas funcionalidades</h3>

Se você encontrou um bug, avise a equipe de desenvolvimento do VRaptor usando as <a href="http://groups.google.com/group/caelum-vraptor">listas de discussões para usuários</a> ou a <a href="http://groups.google.com/group/caelum-vraptor-dev">lista de desenvolvedores</a>. Você também pode cadastrar uma <a href="http://github.com/caelum/vraptor/issues">issue no Github</a>.


<h3>Colaborando com código</h3>

Se você tem alguma melhoria que gostaria de ver no VRaptor, envie sua sugestão para os desenvolvedores na <a href="http://groups.google.com/group/caelum-vraptor-dev">lista de discussão</a>. Se você já implementou a melhoria, envie seu pull request pelo Github.

Você pode resolver umas das <a href="http://github.com/caelum/vraptor/issues">issues cadastradas no Github</a>, enviando-nos um pull request com as suas alterações.

O VRaptor é um Framework Web MVC focado em simplicidade e facilidade de uso. Quando você implementar algo, cuide para seguir as boas práticas de Orientação a Objetos e baixo acoplamento, uso de composição ao invés de herança, convenção ao invés de configurações e um código bem estruturado. Não deixe, também, de escrever os Javadocs e classes de testes unitários.

Contribuições de funcionalidades como segurança, paginação, multitenant, e outros são muito bem vindos por meio de plugins e contribuições para o <a href="http://github.com/caelum/vraptor-contrib">vraptor-contrib</a>, ou extensões para o <a href="https://github.com/caelum/vraptor-scaffold">vraptor-scaffold</a>.

<h3>Montando o ambiente</h3>

Todos os módulos do VRaptor usam o <a href="https://maven.apache.org/">Apache Maven</a>, que possui plugins para as principais IDEs: <a href="https://maven.apache.org/plugins/maven-eclipse-plugin/">Eclipse</a>, <a href="http://wiki.netbeans.org/Maven">Netbeans</a> e <a href="https://maven.apache.org/plugins/maven-idea-plugin/">IDEA</a>. No Eclipse, por exemplo, basta você usar o comando mvn eclipse:eclipse para montar os arquivos de configuração do projeto automaticamente.
