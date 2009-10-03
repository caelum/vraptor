
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
	
		<h2 class="chapter">View e Ajax</h2>

		


<h3 class="section">Custom PathResolver</h3>
	    	<span class="paragraph">Por padr&atilde;o, para renderizar suas views, o VRaptor segue a conven&ccedil;&atilde;o:</span>
	    	<div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">ClientsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">list</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Este m&eacute;todo acima renderizar&aacute; a view <code class="inlineCode">/WEB-INF/jsp/clients/list.jsp</code>.</span>
	    	<span class="paragraph">No entanto, nem sempre queremos esse comportamento, e precisamos usar algum template engine,
como por exemplo, Freemarker ou Velocity, e precisamos mudar essa conven&ccedil;&atilde;o.</span>
	    	<span class="paragraph">Um jeito f&aacute;cil de mudar essa conven&ccedil;&atilde;o &eacute; estendendo a classe <code class="inlineCode">DefaultPathResolver</code>:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">FreemarkerPathResolver </span><span class="java4">extends </span><span class="java10">DefaultPathResolver </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getPrefix</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;/WEB-INF/freemarker/&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getExtension</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;ftl&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Desse jeito, a l&oacute;gica iria renderizar a view <code class="inlineCode">/WEB-INF/freemarker/clients/list.ftl</code>.
Se ainda assim isso n&atilde;o for o suficiente voc&ecirc; pode implementar a interface <code class="inlineCode">PathResolver</code>
e fazer qualquer conven&ccedil;&atilde;o que voc&ecirc; queira, n&atilde;o esquecendo de anotar a classe com @Component.</span>
		

<h3 class="section">View</h3>
	    	<span class="paragraph">Se voc&ecirc; quiser mudar a view de alguma l&oacute;gica espec&iacute;fica voc&ecirc; pode usar o objeto Result:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ClientsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">Result result;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ClientsController</span><span class="java8">(</span><span class="java10">Result result</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result = result;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">list</span><span class="java8">() {}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">save</span><span class="java8">(</span><span class="java10">Client client</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result.use</span><span class="java8">(</span><span class="java10">Results.logic</span><span class="java8">())</span><span class="java10">.redirectTo</span><span class="java8">(</span><span class="java10">ClientsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Por padr&atilde;o existem estes tipos de views implementadas:</span>
	    	<ul class="list"><li><span class="paragraph">Results.logic(), que vai redirecionar para uma outra l&oacute;gica qualquer do sistema</span></li><li><span class="paragraph">Results.page(), que vai redirecionar diretamente para uma p&aacute;gina, podendo ser um
	jsp, um html, ou qualquer uri relativa ao web application dir, ou ao contexto da aplica&ccedil;&atilde;o.</span></li><li><span class="paragraph">Results.http(), que manda informa&ccedil;&otilde;es do protocolo HTTP como status codes e headers</span></li><li><span class="paragraph">Results.nothing(), apenas retorna o c&oacute;digo de sucesso (HTTP 200 OK).</span></li></ul>
		

<h3 class="section">Redirecionamento e forward</h3>
	    	<span class="paragraph">No VRaptor3, podemos tanto redirecionar ou fazermos um forward do usu&aacute;rio para uma outra l&oacute;gica ou um jsp.
A grande diferen&ccedil;a entre fazer um redirecionamento (redirect) e um forward &eacute; que o redirecionamento acontece
no lado do cliente, e o forward acontece no lado do servidor.</span>
	    	<span class="paragraph">Um bom exemplo de uso do redirect, &eacute; o padr&atilde;o 'redirect-after-post', por exemplo, quando voc&ecirc; adiciona um cliente,
e quer retornar para a listagem dos clientes, por&eacute;m, n&atilde;o quer permitir que o usu&aacute;rio atualize a p&aacute;gina (F5) e 
reenvie toda a requisi&ccedil;&atilde;o, acarretando em dados duplicados.</span>
	    	<span class="paragraph">No caso do forward, um exemplo de uso &eacute; quando voc&ecirc; possui uma valida&ccedil;&atilde;o e essa valida&ccedil;&atilde;o falhou, geralmente
voc&ecirc; quer que o usu&aacute;rio continue na mesma tela do formul&aacute;rio com os dados da requisi&ccedil;&atilde;o preenchidos.</span>
		

<h3 class="section">Accepts e o parâmetro _format</h3>
	    	<span class="paragraph">Muitas vezes precisamos renderizar formatos diferentes para uma mesma l&oacute;gica. Por exemplo queremos
retornar um JSON, ao inv&eacute;s de um HTML.
Para fazer isso, podemos definir o Header Accepts da requisi&ccedil;&atilde;o para que aceite o tipo desejado, ou
colocar um par&acirc;metro <code class="inlineCode">_format</code> na requisi&ccedil;&atilde;o.</span>
	    	<span class="paragraph">Se o formato for JSON, a view renderizada por padr&atilde;o ser&aacute;: <code class="inlineCode">/WEB-INF/jsp/{controller}/{logic}.json.jsp</code>,
ou seja, em geral ser&aacute; renderizada a view: <code class="inlineCode">/WEB-INF/jsp/{controller}/{logic}.{formato}.jsp</code>.
Se o formato for HTML voc&ecirc; n&atilde;o precisa coloc&aacute;-lo no nome do arquivo.</span>
	    	<span class="paragraph">O par&acirc;metro <code class="inlineCode">_format</code> tem prioridade sobre o header Accepts.</span>
		

<h3 class="section">Ajax: construindo na view</h3>
	    	<span class="paragraph">Para devolver um JSON na sua view, basta que sua l&oacute;gica disponibilize o objeto para a view, e
dentro da view voc&ecirc; forme o JSON como desejar. Como no exemplo, o seu <code class="inlineCode">/WEB-INF/jsp/clients/load.json.jsp</code>:</span>
	    	<div class="java"><code class="java">{&nbsp;nome:&nbsp;'${client.name}',&nbsp;id:&nbsp;'${client.id}'&nbsp;}</code></div>
	    	<span class="paragraph">E na l&oacute;gica:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ClientsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">Result result;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">ClientDao dao;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ClientsController</span><span class="java8">(</span><span class="java10">Result result, ClientDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result = result;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">load</span><span class="java8">(</span><span class="java10">Client client</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">result.include</span><span class="java8">(</span><span class="java5">&#34;client&#34;</span><span class="java10">, dao.load</span><span class="java8">(</span><span class="java10">client</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>