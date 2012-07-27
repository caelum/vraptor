---
section: 14
title: Configurando o VRaptor na Locaweb
language: pt
category: cookbook
layout: page
---

<h4>por Flavio Gabriel Duarte</h4>

Este tutorial tem como objetivo orientar o deploy de sistemas usando o framework VRaptor na Locaweb com JVM decidado em ambiente Linux compartilhado.

<h1>Preparando o ambiente</h1>

Toda a requisição que chega até a Locaweb vai primeiramente para o Apache e se necessário no <a href="http://en.wikipedia.org/wiki/Mod_jk">mod_jk</a> passará enviará a requisição para o Tomcat, como explicado <a href="http://wiki.locaweb.com.br/pt-br/Tomcat_integrado_com_o_Apache">nessa página</a>.

A configuração sugerida pela Locaweb leva em conta que a sua aplicação não usa nenhum framework, apenas arquivos jsps e servlets configurado pelo arquivo web.xml.

Como o VRaptor não usa o web.xml para definir as urls a configuração sugerida pela Locaweb não funcionará. Para funcionar você precisa abrir um chamado na Locaweb informando que gostaria de usar o framework e escrever a seguinte regra: JkMount /* [login] >> TOMCAT e a pasta do servidor que deseja aplicar a regra caso não seja a raiz.

Aguarde a resposta do chamado para saber quando estará funcionando. O procedimento leva +- 24h para eles realizarem, então peça com antecedência.

Após isso basta enviar a sua aplicação para a Locaweb e reinicar o ambiente que ela estará funcionando. A Locaweb não aceita deploy com arquivos WAR, para enviar os arquivos pelo WAR você precisaria enviá-lo pelo FTP e depois descompactar pelo SSH. Se você está usando o eclipse para desenvolver a sua aplicação e o recurso "servers" do próprio eclipse, a sua aplicação fica dentro de workspace/.metadata/.plugins/org.eclipse.wst.server.core. Dentro deve ter algumas pasta "tmp" isso vai de acordo com a quantidade de servidores que você tenha, alguma delas deve ser a sua aplicação. Como a aplicação está disposta dentro desta pasta, ela poderia ir para a locaweb e ser executada. Essa não é a melhor de fazer o deploy, mas é uma das aceitas neste tipo de ambiente na Locaweb.

A Locaweb define um timeout de 15 segundos em suas conexões MySQL, se você tiver usando este banco e não atentar para isso terá problemas, para contornar essa problema você pode seguir a <a href="http://wiki.locaweb.com.br/pt-br/Resolvendo_Problemas_Conex%C3%A3o_JAVA_com_MYSQL">sugestão da Locaweb</a> ou configurar o c3p0 como descrito <a href="http://blog.caelum.com.br/2009/10/19/a-java-net-socketexception-broken-pipe/">nesse post</a> respeitando o tempo imposto pela Locaweb.
