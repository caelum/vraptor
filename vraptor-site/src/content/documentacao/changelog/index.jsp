
<jsp:include page="/header.jsp">
	<jsp:param name="extras" value='
		<link href="../includes/css/java.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="../includes/css/xml2html.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="../includes/css/style.css" rel="stylesheet" type="text/css" media="screen" />
	'/>
</jsp:include><div id="contentWrap">
    	<div id="contentDocumentacao">
        	<h2><span>documentação</span></h2>
            <h3>documentação toda em português, configuração, migração e utilização.</h3>
            
            <div id="subMenuDoc">
            	<img id="positionTop" src="../includes/images/subMenuTop-trans.png" />
                <img id="positionBottom" src="../includes/images/subMenuBottom-trans.png" />
            	<ol type="1">
									<li><a class="link_toc" href="../../documentacao/vraptor3-guia-de-1-minuto/">1. VRaptor3 - Guia de 1 minuto</a></li>
		
									<li><a class="link_toc" href="../../documentacao/vraptor3-o-guia-inicial-de-10-minutos/">2. VRaptor3 - o guia inicial de 10 minutos</a></li>
		
									<li><a class="link_toc" href="../../documentacao/resources-rest/">3. Resources-Rest</a></li>
		
									<li><a class="link_toc" href="../../documentacao/componentes/">4. Componentes</a></li>
		
									<li><a class="link_toc" href="../../documentacao/conversores/">5. Conversores</a></li>
		
									<li><a class="link_toc" href="../../documentacao/interceptadores/">6. Interceptadores</a></li>
		
									<li><a class="link_toc" href="../../documentacao/validacao/">7. Validação</a></li>
		
									<li><a class="link_toc" href="../../documentacao/view-e-ajax/">8. View e Ajax</a></li>
		
									<li><a class="link_toc" href="../../documentacao/injecao-de-dependencias/">9. Injeção de dependências</a></li>
		
									<li><a class="link_toc" href="../../documentacao/downloading/">10. Downloading</a></li>
		
									<li><a class="link_toc" href="../../documentacao/componentes-utilitarios-opcionais/">11. Componentes Utilitários Opcionais</a></li>
		
									<li><a class="link_toc" href="../../documentacao/configuracoes-avancadas-sobrescrevendo-as-convencoes-e-comportamento-do-vraptor/">12. Configurações avancadas: sobrescrevendo as convenções e comportamento do VRaptor</a></li>
		
									<li><a class="link_toc" href="../../documentacao/spring-joda-time-hibernate-e-google-app-engine/">13. Spring, Joda Time, Hibernate e Google App Engine</a></li>
		
									<li><a class="link_toc" href="../../documentacao/testando-componentes-e-controllers/">14. Testando componentes e controllers</a></li>
		
									<li><a class="link_toc" href="../../documentacao/changelog/">15. ChangeLog</a></li>
		
									<li><a class="link_toc" href="../../documentacao/migrando-do-vraptor2-para-o-vraptor3/">16. Migrando do VRaptor2 para o VRaptor3</a></li>
		
                </ol>
            </div><!-- submenu-->
                        
            <div id="textoCapitulo">
	
		<h2 class="chapter">ChangeLog</h2>

		


<h3 class="section">3.0.0</h3>
	    	<ul class="list"><li><span class="paragraph">ValidationError foi renomeado para ValidationException</span></li><li><span class="paragraph">result.use(Results.http()) para setar headers e status codes do protocolo HTTP</span></li><li><span class="paragraph">Corre&ccedil;&atilde;o de bugs</span></li><li><span class="paragraph">documenta&ccedil;&atilde;o</span></li><li><span class="paragraph">novo site</span></li></ul>
		

<h3 class="section">3.0.0-rc-1</h3>
	    	<ul class="list"><li><span class="paragraph">aplica&ccedil;&atilde;o de exemplo: mydvds</span></li><li><span class="paragraph">novo jeito de adicionar os componentes opcionais do VRaptor:</span><div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">ComponenteOpcional.class, ComponenteOpcional.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div></li><li><span class="paragraph">Utils: HibernateTransactionInterceptor e JPATransactionInterceptor</span></li><li><span class="paragraph">Um exemplo completo de aplica&ccedil;&atilde;o na documenta&ccedil;&atilde;o.</span></li><li><span class="paragraph">Docs em ingl&ecirc;s</span></li></ul>
		

<h3 class="section">3.0.0-beta-5</h3>
	    	<ul class="list"><li><span class="paragraph">Novo jeito de fazer valida&ccedil;&otilde;es:</span><div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">visualiza</span><span class="java8">(</span><span class="java10">Cliente cliente</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">() {{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">cliente.getId</span><span class="java8">() </span><span class="java10">!= null, </span><span class="java5">&#34;id&#34;</span><span class="java10">, </span><span class="java5">&#34;id.deve.ser.preenchido&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">ClientesController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//continua o metodo<br />
</span><span class="java8">}</span></code></div></li><li><span class="paragraph">UploadedFile.getFile() agora retorna InputStream.</span></li><li><span class="paragraph">EntityManagerCreator e EntityManagerFactoryCreator</span></li><li><span class="paragraph">bugfixes</span></li></ul>
		

<h3 class="section">3.0.0-beta-4</h3>
	    	<ul class="list"><li><span class="paragraph">Novo result: result.use(page()).of(MeuController.class).minhaLogica() renderiza a view
	padr&atilde;o (/WEB-INF/jsp/meu/minhaLogica.jsp) sem executar a minhaLogica.</span></li><li><span class="paragraph">Classes Mocks para testes: MockResult e MockValidator, para facilitar testes unit&aacute;rios
	das l&oacute;gicas. Eles ignoram a maioria das chamadas e guardam par&acirc;metros inclu&iacute;dos no result
	e erros de valida&ccedil;&atilde;o.</span></li><li><span class="paragraph">As URIs passadas para result.use(page()).forward(uri) e result.use(page()).redirect(uri)
	n&atilde;o podem ser URIs de l&oacute;gicas, usem os forwards e redirects do result.use(logic())</span></li><li><span class="paragraph">Os par&acirc;metros passados para as URIs agora aceitam pattern-matching:</span><ul class="list"><li><span class="paragraph">Autom&aacute;tico: se temos a URI /clients/{client.id} e client.id &eacute; um Long, o par&acirc;metro {client.id} 
		s&oacute; vai casar com n&uacute;meros, ou seja, a URI /clients/42 casa, mas a uri /clients/random n&atilde;o casa.
		Isso funciona para todos os tipos num&eacute;ricos, booleanos e enums, o vraptor vai restringir para
		os valores poss&iacute;veis.</span></li><li><span class="paragraph">Manual: no CustomRoutes voc&ecirc; vai poder fazer:
		routeFor("/clients/{client.id}").withParameter("client.id").matching("\\d{1,4}")
  			.is(ClienteController.class).mostra(null);
  	ou seja, pode restringir os valores para o determinado par&acirc;metro via express&otilde;es regulares
  	no m&eacute;todo matching.</span></li></ul></li><li><span class="paragraph">Converters para LocalDate e LocalTime do joda-time j&aacute; v&ecirc;m por padr&atilde;o.</span></li><li><span class="paragraph">Quando o Spring &eacute; usado como IoC Provider, o VRaptor tenta buscar o spring da aplica&ccedil;&atilde;o para
	usar como container pai. A busca &eacute; feita por padr&atilde;o em um dos dois jeitos:</span><ul class="list"><li><span class="paragraph">WebApplicationContextUtils.getWebApplicationContext(servletContext), para o caso em que voc&ecirc;
		tem os listeners do Spring configurados.</span></li><li><span class="paragraph">applicationContext.xml dentro do classpath</span></li></ul><span class="paragraph">Se isso n&atilde;o for o suficiente voc&ecirc; pode implementar a interface SpringLocator e disponbilizar
	o ApplicationContext do spring usado pela sua aplica&ccedil;&atilde;o.</span></li><li><span class="paragraph">Utils:</span><ul class="list"><li><span class="paragraph">SessionCreator e SessionFactoryCreator para disponbilizar a Session e o SessionFactory do hibernate
		para os componentes registrados.</span></li><li><span class="paragraph">EncodingInterceptor, para mudar o encoding da sua aplica&ccedil;&atilde;o.</span></li></ul></li><li><span class="paragraph">corre&ccedil;&atilde;o de v&aacute;rios bugs e melhorias na documenta&ccedil;&atilde;o.</span></li></ul>
		

<h3 class="section">3.0.0-beta-3</h3>
	    	<ul class="list"><li><span class="paragraph">O Spring &eacute; o Provider de IoC padr&atilde;o</span></li><li><span class="paragraph">o applicationContext.xml no classpath &eacute; usado como configura&ccedil;&atilde;o incial do spring, caso exista.</span></li><li><span class="paragraph">a documenta&ccedil;&atilde;o <a class="link" target="_blank" href="http://vraptor.caelum.com.br/documentacao">http://vraptor.caelum.com.br/documentacao</a> est&aacute; mais completa e atualizada</span></li><li><span class="paragraph">pequenos bugs e otimiza&ccedil;&otilde;es</span></li></ul>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>