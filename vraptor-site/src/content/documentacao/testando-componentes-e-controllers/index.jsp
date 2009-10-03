
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
	
		<h2 class="chapter">Testando componentes e controllers</h2>

		<span class="paragraph">Criar um teste unit&aacute;rio do seu controller VRaptor costuma ser muito f&aacute;cil:
dado o desacoplamento das suas classes com a api <code class="inlineCode">javax.servlet</code> e os
par&acirc;metros serem populados atrav&eacute;s do request, seu teste ser&aacute; como o de uma
classen qualquer, sem mist&eacute;rios.</span><span class="paragraph">O VRaptor3 gerencia as depend&ecirc;ncias da sua classe, ent&atilde;o voc&ecirc; n&atilde;o precisa 
se preocupar com a cria&ccedil;&atilde;o do seus componentes e controllers, basta receber
suas depend&ecirc;ncias no construtor que o VRaptor3 vai procur&aacute;-las e instanciar
sua classe.</span><span class="paragraph">Na hora de testar suas classes voc&ecirc; pode aproveitar que todas as depend&ecirc;ncias
est&atilde;o sendo recebidas no construtor, e passar implementa&ccedil;&otilde;es falsas (mocks)
dessas depend&ecirc;ncias, para testar unitariamente sua classe.</span><span class="paragraph">Mas os componentes do VRaptor3 que v&atilde;o ser mais presentes na sua aplica&ccedil;&atilde;o
- o <code class="inlineCode">Result</code> e o <code class="inlineCode">Validator</code> - possuem a interface fluente, o que dificulta a cria&ccedil;&atilde;o
de implementa&ccedil;&otilde;es falsas (mocks). Por causa disso existem implementa&ccedil;&otilde;es falsas
j&aacute; dispon&iacute;veis no VRaptor3: <code class="inlineCode">MockResult</code> e <code class="inlineCode">MockValidator</code>. Isso facilita
em muito os seus testes que seriam mais complexos.</span>


<h3 class="section">MockResult</h3>
	    	<span class="paragraph">O MockResult ignora os redirecionamentos que voc&ecirc; fizer, e acumula os objetos inclu&iacute;dos,
para voc&ecirc; poder inspeciona-los e fazer as suas asser&ccedil;&otilde;es.</span>
	    	<span class="paragraph">Um exemplo de uso seria:</span>
	    	<div class="java"><code class="java">
<span class="java10">MockResult result = </span><span class="java4">new </span><span class="java10">MockResult</span><span class="java8">()</span><span class="java10">;<br />
ClienteController controller = </span><span class="java4">new </span><span class="java10">ClienteController</span><span class="java8">(</span><span class="java10">..., result</span><span class="java8">)</span><span class="java10">;<br />
controller.list</span><span class="java8">()</span><span class="java10">; </span><span class="java3">// vai chamar result.include(&#34;clients&#34;, algumaCoisa);<br />
</span><span class="java10">List&lt;Client&gt; clients = result.included</span><span class="java8">(</span><span class="java5">&#34;clients&#34;</span><span class="java8">)</span><span class="java10">; </span><span class="java3">// o cast &eacute; autom&aacute;tico<br />
</span><span class="java10">Assert.assertNotNull</span><span class="java8">(</span><span class="java10">clients</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java3">// mais asserts</span></code></div>
	    	<span class="paragraph">Quaisquer chamadas do tipo result.use(...) v&atilde;o ser ignoradas.</span>
		

<h3 class="section">MockValidator</h3>
	    	<span class="paragraph">O MockValidator vai acumular os erros gerados, e quando o validator.onErrorUse
for chamado, vai lan&ccedil;ar um ValidationError caso haja algum erro. Desse jeito
voc&ecirc; pode inspecionar os erros adicionados, ou simplesmente ver se deu algum erro:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Test</span><span class="java8">(</span><span class="java10">expected=ValidationException.</span><span class="java4">class</span><span class="java8">)<br />
</span><span class="java4">public </span><span class="java9">void </span><span class="java10">testaQueVaiDarErroDeValidacao</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">ClienteController controller = </span><span class="java4">new </span><span class="java10">ClienteController</span><span class="java8">(</span><span class="java10">..., </span><span class="java4">new </span><span class="java10">MockValidator</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; controller.adiciona</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Cliente</span><span class="java8">())</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">ou</span>
	    	<div class="java"><code class="java">
<span class="java16">@Test<br />
</span><span class="java4">public </span><span class="java9">void </span><span class="java10">testaQueVaiDarErroDeValidacao</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">ClienteController controller = </span><span class="java4">new </span><span class="java10">ClienteController</span><span class="java8">(</span><span class="java10">..., </span><span class="java4">new </span><span class="java10">MockValidator</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">try </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.adiciona</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Cliente</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Assert.fail</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">} </span><span class="java4">catch </span><span class="java8">(</span><span class="java10">ValidationException e</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">List&lt;Message&gt; errors = e.getErrors</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">//asserts nos erros<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>