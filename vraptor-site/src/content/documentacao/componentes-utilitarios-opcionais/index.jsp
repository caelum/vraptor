
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
	
		<h2 class="chapter">Componentes Utilitários Opcionais</h2>

		


<h3 class="section">Registrando um componente opcional</h3>
	    	<span class="paragraph">O VRaptor possui alguns componentes opcionais, que est&atilde;o no pacote 
<code class="inlineCode">br.com.caelum.vraptor.util</code>. Para registr&aacute;-los voc&ecirc; pode fazer o seguinte:</span>
	    	<ul class="list"><li><span class="paragraph">Crie uma classe filha do Provider que voc&ecirc; est&aacute; usando:</span><div class="java"><code class="java">
<span class="java4">package </span><span class="java10">br.com.nomedaempresa.nomedoprojeto;<br />
<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div></li><li><span class="paragraph">Registre essa classe como provider no web.xml:</span><div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.provider</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">br.com.nomedaempresa.nomedoprojeto.CustomProvider</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div></li><li><span class="paragraph">Sobrescreva o m&eacute;todo registerCustomComponents e adicione os componentes opcionais:</span><div class="java"><code class="java">
<span class="java4">package </span><span class="java10">br.com.nomedaempresa.nomedoprojeto;<br />
<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">ComponenteOpcional.class, ComponenteOpcional.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div></li></ul>
		

<h3 class="section">Hibernate Session e SessionFactory</h3>
	    	<span class="paragraph">Se voc&ecirc; precisa de Session's e SessionFactory nos seus componentes, voc&ecirc; geralmente
vai precisar de um ComponentFactory para cri&aacute;-los. Se voc&ecirc; usa entidades anotadas,
e o hibernate.cfg.xml na raiz do WEB-INF/classes, voc&ecirc; pode usar as ComponentFactory's para
isso que j&aacute; v&ecirc;m com o VRaptor. O que voc&ecirc; precisa fazer &eacute;:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">SessionCreator.class, SessionCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; registry.registry</span><span class="java8">(</span><span class="java10">SessionFactoryCreator.class, SessionFactoryCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">Inclusive voc&ecirc; pode habilitar um interceptor que controla a transa&ccedil;&atilde;o do Hibernate:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">HibernateTransactionInterceptor.class, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; HibernateTransactionInterceptor.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">JPA EntityManager e EntityManagerFactory</h3>
	    	<span class="paragraph">Se voc&ecirc; tiver um persistence.xml com o persistence-unit chamado "default", voc&ecirc; pode usar
os ComponentFactories para criar EntityManager e EntityManagerFactory j&aacute; dispon&iacute;veis no 
vraptor:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">EntityManagerCreator.class, EntityManagerCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; registry.registry</span><span class="java8">(</span><span class="java10">EntityManagerFactoryCreator.class, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; EntityManagerFactoryCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">Inclusive voc&ecirc; pode habilitar um interceptor que controla a transa&ccedil;&atilde;o da JPA:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">JPATransactionInterceptor.class, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; JPATransactionInterceptor.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>