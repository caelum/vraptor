
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
	
		<h2 class="chapter">Validação</h2>

		<span class="paragraph">O VRaptor3 suporta 2 estilos de valida&ccedil;&atilde;o. Cl&aacute;ssico e fluente. A porta de entrada para ambos os estilos &eacute; a interface Validator.
Para que seu recurso tenha acesso ao Validator, basta receb&ecirc;-lo no construtor do seu recurso:</span><div class="java"><code class="java">
<span class="java4">import </span><span class="java10">br.com.caelum.vraptor.Validator;<br />
...<br />
<br />
</span><span class="java16">@Resource<br />
</span><span class="java4">class </span><span class="java10">FuncionarioController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">Validator validator;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">FuncionarioController</span><span class="java8">(</span><span class="java10">Validator validator</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.validator = validator;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>


<h3 class="section">Estilo clássico</h3>
	    	<span class="paragraph">A forma cl&aacute;ssica &eacute; semelhante a forma como as valida&ccedil;&otilde;es eram feitas no VRaptor2.
Dentro da sua l&oacute;gica de neg&oacute;cios, basta fazer a verifica&ccedil;&atilde;o que deseja e caso haja um erro de valida&ccedil;&atilde;o, adicionar esse erro na lista de erros de valida&ccedil;&atilde;o.
Por exemplo, para validar que o nome do funcionario deve ser Fulano, fa&ccedil;a:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Funcionario funcionario</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if</span><span class="java8">(</span><span class="java10">! funcionario.getNome</span><span class="java8">()</span><span class="java10">.equals</span><span class="java8">(</span><span class="java5">&#34;Fulano&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">ValidationMessage</span><span class="java8">(</span><span class="java5">&#34;erro&#34;</span><span class="java10">,</span><span class="java5">&#34;nomeInvalido&#34;</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">FuncionarioController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.formulario</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; dao.adiciona</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">Ao chamar o validator.onErrorUse, se existirem erros de valida&ccedil;&atilde;o, o VRaptor para a execu&ccedil;&atilde;o e redireciona a
p&aacute;gina que voc&ecirc; indicou. O redirecionamento funciona da mesma forma que o result.use(..).ed</span>
		

<h3 class="section">Estilo fluente</h3>
	    	<span class="paragraph">No estilo fluente, a id&eacute;ia &eacute; que o c&oacute;digo para fazer a valida&ccedil;&atilde;o seja algo muito parecido com a linguagem natural.
Por exemplo, caso queiramos obrigar que seja informado o nome do funcionario:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Funcionario funcionario</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!funcionario.getNome</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;erro&#34;</span><span class="java10">,</span><span class="java5">&#34;nomeNaoInformado&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">FuncionarioController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.formulario</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.adiciona</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">Voc&ecirc; pode ler esse c&oacute;digo como: "Validador, cheque as minhas valida&ccedil;&otilde;es. 
A primeira valida&ccedil;&atilde;o &eacute; que o nome do funcion&aacute;rio n&atilde;o pode ser vazio".
Bem mais pr&oacute;ximo a linguagem natural.</span>
	    	<span class="paragraph">Assim sendo, caso o nome do funcionario seja vazio, ele vai ser redirecionado novamente para a logica "formulario", 
que exibe o formulario para que o usu&aacute;rio adicione o funcion&aacute;rio novamente. Al&eacute;m disso, ele devolve para o formulario a mensagem de erro que aconteceu na valida&ccedil;&atilde;o.</span>
	    	<span class="paragraph">Muitas vezes algumas valida&ccedil;&otilde;es s&oacute; precisam acontecer se uma outra deu certo, por exemplo, eu s&oacute; vou
checar a idade do usu&aacute;rio se o usu&aacute;rio n&atilde;o for null. O m&eacute;todo that retorna um boolean dizendo se o que
foi passado pra ele &eacute; v&aacute;lido ou n&atilde;o:</span>
	    	<div class="java"><code class="java">
<span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if </span><span class="java8">(</span><span class="java10">that</span><span class="java8">(</span><span class="java10">usuario != null, </span><span class="java5">&#34;usuario&#34;</span><span class="java10">, </span><span class="java5">&#34;usuario.nulo&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">usuario.getIdade</span><span class="java8">() </span><span class="java10">&gt;= </span><span class="java7">18</span><span class="java10">, </span><span class="java5">&#34;usuario.idade&#34;</span><span class="java10">, </span><span class="java5">&#34;usuario.menor.de.idade&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}})</span><span class="java10">;</span></code></div>
	    	<span class="paragraph">Desse jeito a segunda valida&ccedil;&atilde;o s&oacute; acontece se a primeira n&atilde;o falhou.</span>
		

<h3 class="section">Validação usando matchers do Hamcrest</h3>
	    	<span class="paragraph">Voc&ecirc; pode tamb&eacute;m usar matchers do Hamcrest para deixar a valida&ccedil;&atilde;o mais leg&iacute;vel, e ganhar
a vantagem da composi&ccedil;&atilde;o de matchers e da cria&ccedil;&atilde;o de novos matchers que o Hamcrest te oferece:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">admin</span><span class="java8">(</span><span class="java10">Funcionario funcionario</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">funcionario.getRoles</span><span class="java8">()</span><span class="java10">, hasItem</span><span class="java8">(</span><span class="java5">&#34;ADMIN&#34;</span><span class="java8">)</span><span class="java10">, </span><span class="java5">&#34;admin&#34;</span><span class="java10">,</span><span class="java5">&#34;funcionario.nao.eh.admin&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">LoginController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.login</span><span class="java8">()</span><span class="java10">;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.adiciona</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">Hibernate validator</h3>
	    	<span class="paragraph">O VRaptor tamb&eacute;m suporta integra&ccedil;&atilde;o com o HibernateValidator. No exemplo anterior para validar o objeto Funcionario 
usando o Hibernate Validator basta adicionar uma linha de c&oacute;digo:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Funcionario funcionario</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Valida&ccedil;ão do Funcionario com Hibernate Validator<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java10">Hibernate.validate</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">))</span><span class="java10">;<br />
<br />
&nbsp;&nbsp;&nbsp; validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!funcionario.getNome</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;erro&#34;</span><span class="java10">,</span><span class="java5">&#34;nomeNaoInformado&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.adiciona</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">Para onde redirecionar no caso de erro</h3>
	    	<span class="paragraph">Outro ponto importante que deve ser levado em considera&ccedil;&atilde;o no momento de fazer valida&ccedil;&otilde;es &eacute; o 
redirecionamento quando ocorrer um erro. Como enviamos o usu&aacute;rio para outro recurso com o VRaptor3, 
caso haja erro na valida&ccedil;&atilde;o?</span>
	    	<span class="paragraph">Simples, apenas diga no seu c&oacute;digo que quando correr um erro, &eacute; para o usu&aacute;rio ser enviado para algum recurso.
Como no exemplo:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">adiciona</span><span class="java8">(</span><span class="java10">Funcionario funcionario</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Valida&ccedil;ão na forma fluente<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java5">&#34;erro&#34;</span><span class="java10">,</span><span class="java5">&#34;nomeNaoInformado&#34;</span><span class="java10">, !funcionario.getNome</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Valida&ccedil;ão na forma cl&aacute;ssica<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if</span><span class="java8">(</span><span class="java10">! funcionario.getNome</span><span class="java8">()</span><span class="java10">.equals</span><span class="java8">(</span><span class="java5">&#34;Fulano&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">ValidationMessage</span><span class="java8">(</span><span class="java5">&#34;erro&#34;</span><span class="java10">,</span><span class="java5">&#34;nomeInvalido&#34;</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<strong>&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">FuncionarioController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.formulario</span><span class="java8">()</span><span class="java10">;<br /></strong>
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.adiciona</span><span class="java8">(</span><span class="java10">funcionario</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">Note que se sua l&oacute;gica adiciona algum erro de valida&ccedil;&atilde;o voc&ecirc; <strong class="definition">precisa</strong> dizer pra onde o VRaptor deve ir.
O validator.onErrorUse funciona do mesmo jeito que o result.use: voc&ecirc; pode usar qualquer view da
classe Results.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>