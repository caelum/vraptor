$(document).ready(function(){
	caelum.cadastra();
	caelum.valida();
	caelum.ehValido();
	caelum.ehCpf();
	caelum.doTips();
});


$.validator.addMethod("cpfValido", function(){
	return caelum.validaCpf($("#cpf").val());
}, 'Por favor digite um CPF v&aacute;lido');

$.validator.addMethod("enderecoValido", function(){
	return caelum.validaEndereco($("#endereco").val());
}, 'Por favor preencha com um Endere&ccedil;o v&aacute;lido');

var caelum = {
	doTips: function(){
		var endereco = '(Logradouro) (Endereço),(Número)';
		$('#endereco').attr('style','color:grey').val(endereco);
		$('#endereco').focus(function(){
			this.value == endereco ? this.value = '' : this.value;
			$('#endereco').removeAttr('style');
		});
		$('#endereco').blur(function(){
			if($(this).val() == ''){
				$('#endereco').attr('style','color:grey').val(endereco);
			}
		});
	},
	
	cadastra : function(){
		$('#cadastrar').click(function() {
			$('#success').fadeOut("slow");
			if(!$(this.form).valid()){
				return false;
			}
			this.disabled = true;
			var dataString = $(this.form).serialize();
			var url = '';
			url = "http://caelumweb.caelum.com.br/caelumweb/evento/aluno/" + this.form.turmaId.value + "/cadastra.snippet.logic?" + dataString;
			$.getJSON(url + "&callback=?", function(data){
				if(data['data'] == "sucesso"){
					$('.erro_msg').hide();
					$('#success').fadeIn("slow");
					$('#formulario_inscricao')[0].reset();
				}else{
					$('.erro_msg').show();
					$('.erro_msg ul').append('<li id="serverError">Ocorreu um erro, contacte a Caelum.</li>');
					$(this).removeAttr('disabled');
				}
			});
			return false;
		});
	},
	
	valida: function(){
		$("#formulario_inscricao").validate({
			rules: {
				"aluno.nome": "required",
				"aluno.cpf": {
					required: true,
					cpfValido: true
				},
				"aluno.cep": {
					required: true,
					minlength: 8
				},
				"aluno.endereco": {
					required: true,
					enderecoValido: true
				},
				"aluno.cidade": "required",
				"aluno.estado": {
					required: true,
					maxlength: 2
				},
				"aluno.emails[0].email": {
					required: true,
					email: true
				}
			},
			
			messages: {
				"aluno.nome": "Por favor preencha o campo Nome",
				"aluno.cpf": {
					required: "Por favor preencha o campo CPF",
					isCpf: 'Por favor digite um CPF v&aacute;lido'
				},
				"aluno.cep": {
					required: "Por favor preencha o campo CEP",
					minlength: "Seu CEP deve ter no m&iacute;nimo de 8 caracteres"
				},
				"aluno.endereco": "Por favor preencha o campo Endere&ccedil;o",
				"aluno.cidade": "Por favor preencha o campo Cidade",
				"aluno.estado": {
					required: "Por favor preencha o campo Estado",
					maxlength: "O Estado deve ter dois caracteres"
				},
				"aluno.emails[0].email": {
					required: "Por favor preencha o campo Email",
					email: "Por favor entre com um Email v&aacute;lido"
				}
			}
		});
	},
	
	ehValido: function(){
		$('#formulario_inscricao input').each(function(count, element){
			$(element).blur(function(){
				$(this).valid();
			});
		});
	},
	
	ehCpf: function(){
		$("#cpf").keydown(function(ev){
			var blackList = ['190','109','189'];
			var keyEvent = ev.which || ev.keyCode || ev.charCode;
			if(blackList.contains(keyEvent) || (keyEvent >= 65 && keyEvent <= 90)){
				ev.preventDefault();
			}
		});
		var valido = false;
		$("#cpf").blur(function(){
			valido = caelum.validaCpf($(this).val());
		});
		return valido;
	},
	
	validaCpf: function(cpf){
		var numeros, digitos, soma, i, resultado, digitos_iguais;
		digitos_iguais = 0;
		if (cpf.length < 11){
			return false;
		}
		for (var j=0; j < cpf.length -1; j++) {
			digitos_iguais = cpf.charAt(j) == cpf.charAt(j + 1) ? ++digitos_iguais : digitos_iguais;
			if(digitos_iguais == 10){
				return false;
			}
		}
		for (i = 0; i < cpf.length - 1; i++){
            numeros = cpf.substring(0,9);
            digitos = cpf.substring(9);
            soma = 0;
            for (i = 10; i > 1; i--){
				soma += numeros.charAt(10 - i) * i;
			}
            resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
            if (resultado != digitos.charAt(0)){
				return false;
			}
            numeros = cpf.substring(0,10);
            soma = 0;
            for (i = 11; i > 1; i--){
				soma += numeros.charAt(11 - i) * i;
			}
            resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
            if (resultado != digitos.charAt(1)){
				return false;
			}
            return true;
		}
	},
	
	validaEndereco: function(endereco){
		if(endereco == '(Logradouro) (Endereço) (Número)'){
			return false;
		}else{
			return true;
		}
	}
}

Array.prototype.contains = function (element) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == element) {
			return true;
		}
	}
	return false;
}