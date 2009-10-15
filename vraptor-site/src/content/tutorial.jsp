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
					Av.Lins de Vasconselhos, 3352 - Vila Mariana <br/>
					Ao lado do metrô vila mariana</address></dd>
				<dt>Informações:</dt> 
				<dd>contato@caelum.com.br<br/>
					(11) 5571-2751</dd>
			</dl>
		</div>
		<form id="formulario_inscricao">
	     	<input type="hidden" style="display: none;" name="turma.id" value="1129" id="turmaId"/>
			<div class="erro_msg"><ul></ul></div>
	
			<fieldset>
				<legend align="right">Dados Pessoais</legend>
				<label class="required" for="nome">Nome</label>
				<input id="nome" name="aluno.nome" type="text" />
		
				<label class="required" for="email">Email</label>
				<input id="email" name="aluno.emails[0].email" type="text" />
				
				<label class="required" for="cpf">CPF</label>
				<input id="cpf" maxlength="11" name="aluno.cpf" type="text"/>
			</fieldset>		                            
			
			<fieldset>
				<legend align="right">Endere&ccedil;o</legend>
				<label class="required" for="endereco">Rua</label>
				<input id="endereco" name="aluno.endereco" type="text" />
				
				<label for="complemento">Complemento</label>
				<input id="complemento" name="aluno.complemento" type="text"/>
				
				<label class="required" for="cep">CEP</label>
				<input id="cep" maxlength="8" name="aluno.cep" type="text"/>
				
				<label class="required" for="cidade">Cidade</label>
				<input id="cidade" name="aluno.cidade" type="text"/>
				
				<label class="required" for="estado">Estado</label>
				<input id="estado" name="aluno.estado" type="text" maxlength="2" onkeyup="this.value = this.value.toUpperCase();"/>
			</fieldset>
			<fieldset class="buttons">
				<button id="cadastrar" type="submit">Cadastrar</button> 
			</fieldset>
	                     
		</form>
	</div>
</div>
<script type="text/javascript" src="${path }/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${path }/jquery.validate.min.js"></script>
<script type="text/javascript" src="${path }/form.js"></script>
<%@include file="/footer.jsp" %>
