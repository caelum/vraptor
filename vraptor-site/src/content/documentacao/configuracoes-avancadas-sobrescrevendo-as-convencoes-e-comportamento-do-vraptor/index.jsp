
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
	
		<h2 class="chapter">Configurações avancadas: sobrescrevendo as convenções e comportamento do VRaptor</h2>

		


<h3 class="section">Mudando a view renderizada por padrão</h3>
	    	<span class="paragraph">Se voc&ecirc; precisa mudar a view renderizada por padr&atilde;o, ou mudar o local em que ela &eacute; procurada,
basta criar a seguinte classe:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">CustomPathResolver </span><span class="java4">extends </span><span class="java10">DefaultPathResolver </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getPrefix</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;/pasta/raiz/&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getExtension</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;ftl&#34;</span><span class="java10">; </span><span class="java3">// ou qualquer outra extensão<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String extractControllerFromName</span><span class="java8">(</span><span class="java10">String baseName</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//sua conven&ccedil;ão aqui<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //ex.: Ao inv&eacute;s de redirecionar UserController para 'user'<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //voc&ecirc; quer redirecionar para 'userResource'<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //ex.2: Se voc&ecirc; sobrescreveu a conve&ccedil;ão para nome dos Controllers para XXXResource<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //e quer continuar redirecionando para 'user' e não para 'userResource'<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
}</span></code></div>
	    	<span class="paragraph">Se voc&ecirc; precisa mudar mais ainda a conven&ccedil;&atilde;o basta implementar a interface PathResolver.</span>
		

<h3 class="section">Mudando a URI padrão</h3>
	    	<span class="paragraph">Por padr&atilde;o, a URI para o m&eacute;todo ClientesController.lista() &eacute; /clientes/lista, ou seja,
nome_do_controller/nome_do_metodo. Para sobrescrever essa conven&ccedil;&atilde;o, basta criar a classe:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
@ApplicationScoped<br />
</span><span class="java4">public class </span><span class="java10">MeuRoutesParser </span><span class="java4">extends </span><span class="java10">PathAnnotationRoutesParser </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//delegate constructor<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String extractControllerNameFrom</span><span class="java8">(</span><span class="java10">Class&lt;?&gt; type</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//sua conven&ccedil;ão aqui<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String defaultUriFor</span><span class="java8">(</span><span class="java10">String controllerName, String methodName</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//sua conven&ccedil;ão aqui<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Se voc&ecirc; precisa mudar mais ainda a conven&ccedil;&atilde;o basta implementar a interface RoutesParser.</span>
		

<h3 class="section">Mudando o IoC provider</h3>
	    	<span class="paragraph">O IoC provider padr&atilde;o &eacute; o spring. Para mud&aacute;-lo basta colocar no web.xml:
&lt;context-param&gt;
    &lt;param-name&gt;br.com.caelum.vraptor.provider&lt;/param-name&gt;
    &lt;param-value&gt;br.com.classe.do.seu.provider.Preferido&lt;/param-value&gt;
&lt;/context-param&gt;</span>
	    	<span class="paragraph">Entre os padr&atilde;o existem: br.com.caelum.vraptor.ioc.spring.SpringProvider e br.com.caelum.vraptor.ioc.pico.PicoProvider.
Voc&ecirc; pode ainda estender alguma dessas duas classes e usar seu pr&oacute;prio provider.</span>
		

<h3 class="section">Mudando ApplicationContext base do Spring</h3>
	    	<span class="paragraph">Caso o VRaptor n&atilde;o esteja usando o seu ApplicationContext como base, basta estender o SpringProvider
e implementar o m&eacute;todo getParentApplicationContext, passando o ApplicationContext da sua aplica&ccedil;&atilde;o:</span>
	    	<div class="java"><code class="java">
<span class="java4">package </span><span class="java10">br.com.nomedaempresa.nomedoprojeto;<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ApplicationContext getParentApplicationContext</span><span class="java8">(</span><span class="java10">ServletContext context</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">ApplicationContext applicationContext = </span><span class="java3">//l&oacute;gica pra criar o applicationContext<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">applicationContext;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">e mudar o provider no web.xml:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.provider</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">br.com.nomedaempresa.nomedoprojeto.CustomProvider</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div>
	    	<span class="paragraph">Por padr&atilde;o o VRaptor tenta procurar o applicationContext via 
<code class="inlineCode">WebApplicationContextUtils.getWebApplicationContext(servletContext);</code> ou carregando do applicationContext.xml
que est&aacute; no classpath.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>