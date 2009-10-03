
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
	
		<h2 class="chapter">Spring, Joda Time, Hibernate e Google App Engine</h2>

		


<h3 class="section">Integração com Hibernate ou JPA</h3>
	    	<span class="paragraph">Existem ComponentFactories implementadas para Session, SessionFactory, EntityManager e EntityManagerFactory.
Voc&ecirc; pode us&aacute;-las ou se basear nelas para criar sua pr&oacute;pria ComponentFactory para essas classes.</span>
	    	<span class="paragraph">Al&eacute;m disso existem interceptors implementados que controlam as transa&ccedil;&otilde;es tanto na JPA quanto com o Hibernate.</span>
	    	<span class="paragraph">Para saber como fazer usar esses componentes veja o cap&iacute;tulo de utils.</span>
		

<h3 class="section">Integração com Spring</h3>
	    	<span class="paragraph">O VRaptor roda dentro do Spring, e usa o ApplicationContext da sua aplica&ccedil;&atilde;o como parent do ApplicationContext
do VRaptor. Logo todas as funcionalidades e m&oacute;dulos do Spring funcionam com o VRaptor sem nenhuma configura&ccedil;&atilde;o 
da parte do VRaptor!</span>
		

<h3 class="section">Conversores para Joda Time</h3>
	    	<span class="paragraph">A api de datas do Java &eacute; bem ruim, e por esse motivo existe o projeto joda-time (<a class="link" target="_blank" href="http://joda-time.sourceforge.net/">http://joda-time.sourceforge.net/</a>)
que tem uma api bem mais agrad&aacute;vel para trabalhar com datas. Se o jar do joda-time estiver no classpath,
o VRaptor registra automaticamente os conversores para os tipos LocalDate e LocalTime, logo voc&ecirc; pode
receb&ecirc;-los como par&acirc;metro sem problemas.</span>
		

<h3 class="section">Rodando o VRaptor3 no Google App Engine</h3>
	    	<span class="paragraph">Para rodar no Google App Engine(GAE), voc&ecirc; precisa fazer algumas mudan&ccedil;as no VRaptor3 por causa
das limita&ccedil;&otilde;es que o GAE te traz. Existe um blank-project para rodar uma aplica&ccedil;&atilde;o com VRaptor3
no GAE.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>