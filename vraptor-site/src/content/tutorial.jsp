<%@include file="/header.jsp" %>
<div id="contentWrap">
	<div id="contentTutorial">
		<h2 class="title" style="text-transform: none;">Tutorial de VRaptor</h2>
		<h3 class="title" style="text-transform: none;">Formul&aacute;rio de inscri&ccedil;&atilde;o</h3>
		
		<div id="informacoes">
			<dl>
				<dt>Data:</dt> 
				<dd>24/10</dd>
				
				<dt>Horario:</dt>
				<dd>10h às 13h</dd>
				
				<dt>Local:</dt>
				<dd><address>Espaço Cultural Japonês<br/>
					Av.Lins de Vasconcelos, 3352 - Vila Mariana <br/>
					Ao lado do metrô vila mariana</address></dd>
				<dt>Informações:</dt> 
				<dd>contato@caelum.com.br<br/>
					(11) 5571-2751</dd>
			</dl>
		</div>
		<div class="erro_msg"><ul></ul></div>
		<div id="success">Cadastro efetuado com sucesso!</div>
		<div>Inscrições encerradas</div>
	</div>
</div>
<script type="text/javascript" src="${path }/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${path }/jquery.validate.min.js"></script>
<script type="text/javascript" src="${path }/form.js"></script>
<%@include file="/footer.jsp" %>
