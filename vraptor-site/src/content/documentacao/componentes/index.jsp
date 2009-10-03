
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
	
		<h2 class="chapter">Componentes</h2>

		


<h3 class="section">O que são componentes?</h3>
	    	<span class="paragraph">Componentes s&atilde;o inst&acirc;ncias de classes que seu projeto precisa para executar tarefas ou armazenar estados
em diferentes situa&ccedil;&otilde;es.</span>
	    	<span class="paragraph">Exemplos cl&aacute;ssicos de uso de componentes seriam os Daos, enviadores de email etc.</span>
	    	<span class="paragraph">A sugest&atilde;o de boa pr&aacute;tica indica <em class="italic">sempre</em> criar uma interface para seus componentes.
Dessa maneira seu c&oacute;digo tamb&eacute;m fica mais f&aacute;cil de testar unitariamente.</span>
	    	<span class="paragraph">O exemplo a seguir mostra um componente a ser gerenciado pelo vraptor:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">ClienteDao </span><span class="java8">{<br />
 <br />
&nbsp; </span><span class="java4">private final </span><span class="java10">Session session;<br />
&nbsp; </span><span class="java4">public </span><span class="java10">ClienteDao</span><span class="java8">(</span><span class="java10">Session session</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session = session;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Cliente cliente</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">session.save</span><span class="java8">(</span><span class="java10">cliente</span><span class="java8">)</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
}</span></code></div>
		

<h3 class="section">Escopos</h3>
	    	<span class="paragraph">Assim como os recursos, os componentes vivem em um escopo espec&iacute;fico e seguem
as mesmas regras, por padr&atilde;o pertencendo ao escopo de requisic&atilde;o, isto &eacute;, a cada
nova requisi&ccedil;&atilde;o seu componente ser&aacute; novamente instanciado.</span>
	    	<span class="paragraph">O exemplo a seguir mostra o fornecedor de conexoes com o banco baseado no hibernate.
Esse fornecedor esta no escopo de aplicaca&ccedil;&atilde;o, portanto ser&aacute; instanciado somente
uma vez por contexto:</span>
	    	<div class="java"><code class="java">
<span class="java16">@ApplicationScoped<br />
@Component<br />
</span><span class="java4">public class </span><span class="java10">HibernateControl </span><span class="java8">{<br />
 <br />
&nbsp; </span><span class="java4">private final </span><span class="java10">SessionFactory factory;<br />
&nbsp; </span><span class="java4">public </span><span class="java10">HibernateControl</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.factory = </span><span class="java4">new </span><span class="java10">AnnotationConfiguration</span><span class="java8">()</span><span class="java10">.configure</span><span class="java8">()</span><span class="java10">.buildSessionFactory</span><span class="java8">()</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
&nbsp; </span><span class="java4">public </span><span class="java10">Session getSession</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">factory.openSession</span><span class="java8">()</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
}</span></code></div>
		

<h3 class="section">ComponentFactory</h3>
	    	<span class="paragraph">Muitas vezes voc&ecirc; quer receber como depend&ecirc;ncia da sua classe alguma classe que n&atilde;o &eacute; do
seu projeto, como por exemplo uma Session do hibernate ou um EntityManager da JPA.</span>
	    	<span class="paragraph">Para poder fazer isto, basta criar um ComponentFactory:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">SessionCreator </span><span class="java4">implements </span><span class="java10">ComponentFactory&lt;Session&gt; </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">SessionFactory factory;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">Session session;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">SessionCreator</span><span class="java8">(</span><span class="java10">SessionFactory factory</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.factory = factory;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@PostConstruct<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">create</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session = factory.openSession</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Session getInstance</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">session;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@PreDestroy<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">destroy</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session.close</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
}</span></code></div>
	    	<span class="paragraph">Note que voc&ecirc; pode adicionar os listeners @PostConstruct e @PreDestroy para controlar
a cria&ccedil;&atilde;o e destrui&ccedil;&atilde;o dos recursos que voc&ecirc; usa. Isso funciona para qualquer componente
que voc&ecirc; registrar no VRaptor.</span>
		

<h3 class="section">Injeção de dependências</h3>
	    	<span class="paragraph">O VRaptor utiliza um de seus provedores de inje&ccedil;&atilde;o de depend&ecirc;ncias para controlar o que
&eacute; necess&aacute;rio para instanciar cada um de seus componentes e recursos.</span>
	    	<span class="paragraph">Sendo assim, os dois exemplos anteriores permitem que qualquer um de seus recursos ou componentes receba
um ClienteDao em seu construtor, por exemplo:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ClienteController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">ClienteDao dao;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ClienteController</span><span class="java8">(</span><span class="java10">ClienteDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Post<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Cliente cliente</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao.adiciona</span><span class="java8">(</span><span class="java10">cliente</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>