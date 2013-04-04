---
title: Validação
layout: page
section: 7
categories: [pt, docs]
---

O VRaptor3 suporta dois estilos de validação: clássico e fluente. A porta de entrada para ambos os estilos é a interface Validator. Para que seu recurso tenha acesso ao Validator, basta recebê-lo no seu construtor:

{% highlight java %}
import br.com.caelum.vraptor.Validator;
...

@Resource
class FuncionarioController {
    private Validator validator;

    public FuncionarioController(Validator validator) {
        this.validator = validator;
    }
}
{% endhighlight %}

<h3>Estilo clássico</h3>

A forma clássica é semelhante à forma como as validações eram feitas no VRaptor2. Dentro da sua lógica de negócios, basta fazer a verificação que deseja e caso haja um erro de validação, adicionar esse erro na lista de erros de validação. Por exemplo, para validar que o nome do funcionario deve ser Fulano, faça:

{% highlight java %}
public void adiciona(Funcionario funcionario) {
    if (!funcionario.getNome().equals("Fulano")) {
        validator.add(new ValidationMessage("nome.invalido", "erro"));
    }

    validator.onErrorUsePageOf(FuncionarioController.class).formulario();

    dao.adiciona(funcionario);
}
{% endhighlight %}

Ao chamar o validator.onErrorUse, se existirem erros de validação, o VRaptor para a execução e redireciona à página que você indicou. O redirecionamento funciona da mesma forma que o result.use(..).

<h3>Estilo fluente</h3>

No estilo fluente, a ideia é que o código para fazer a validação seja algo muito parecido com a linguagem natural. Por exemplo, caso queiramos obrigar que seja informado o nome do funcionário:

{% highlight java %}
public adiciona(Funcionario funcionario) {
    validator.checking(new Validations() { {
        that(!funcionario.getNome().isEmpty(), "erro", "nome.nao.informado");
    } });

    validator.onErrorUsePageOf(FuncionarioController.class).formulario();

    dao.adiciona(funcionario);
}
{% endhighlight %}

Você pode ler esse código como: "Validador, cheque as minhas validações. A primeira validação é que o nome do funcionário não pode ser vazio". Bem mais próximo a linguagem natural.
Assim sendo, caso o nome do funcionário seja vazio, ele vai ser redirecionado novamente para a lógica "formulario", para que o funcionário seja adicionado novamente. Além disso, ele devolve para o formulario a mensagem de erro que aconteceu na validação.
Muitas vezes, algumas validações só precisam acontecer se uma outra deu certo, por exemplo, eu só vou checar a idade do usuário se o usuário não for null. O método that retorna um boolean dizendo se o que foi passado pra ele é válido ou não:

{% highlight java %}
validator.checking(new Validations() { {
    if (that(usuario != null, "usuario", "usuario.nulo")) {
        that(usuario.getIdade() >= 18, "usuario.idade", "usuario.menor.de.idade");
    }
} });
{% endhighlight %}

Desse jeito a segunda validação só acontece se a primeira não falhou.

<h3>Validação com parâmetros nas mensagens</h3>

Você pode colocar parâmetros nas suas mensagens internacionalizadas:

{% highlight jsp %}
maior.que = {0} deveria ser maior que {1}
{% endhighlight %}

E usá-los no seu código de validação:

{% highlight java %}
validator.checking(new Validations() { {
    that(usuario.getIdade() >= 18, "usuario.idade", "maior.que", "Idade", 18);
    // Idade deveria ser maior que 18
} });
{% endhighlight %}

Você pode também internacionalizar os parâmetros, usando o método i18n:

{% highlight jsp %}
usuario.idade = Idade do usuário
{% endhighlight %}

{% highlight java %}
validator.checking(new Validations() { {
    that(usuario.getIdade() >= 18, "usuario.idade", "maior.que", i18n("usuario.idade"), 18);
    // Idade do usuário deveria ser maior que 18
} });
{% endhighlight %}

<h3>Validação usando matchers do Hamcrest</h3>

Você pode também usar matchers do Hamcrest para deixar a validação mais legível, e ganhar a vantagem da composição de matchers e da criação de novos matchers que o Hamcrest lhe oferece:

{% highlight java %}
public admin(Funcionario funcionario) {
    validator.checking(new Validations() { {
        that(funcionario.getRoles(), hasItem("ADMIN"), "admin", "funcionario.nao.eh.admin");
    } });

    validator.onErrorUsePageOf(LoginController.class).login();

    dao.adiciona(funcionario);
}
{% endhighlight %}

<h3>Bean Validation</h3>

O VRaptor também suporta integração com o Bean Validation e o Hibernate Validator. Para usar as validações basta adicionar no seu classpath qualquer implementação do Bean Validation.
No exemplo anterior, para validar o objeto Funcionario basta uma adicionar uma linha de código:

{% highlight java %}
public adiciona(Funcionario funcionario) {
    // Validação do Funcionario com o Bean Validator ou Hibernate Validator
    validator.validate(funcionario);

    validator.checking(new Validations() { {
        that(!funcionario.getNome().isEmpty(), "erro", "nome.nao.informado");
    } });

    validator.onErrorUsePageOf(FuncionarioController.class).formulario();

    dao.adiciona(funcionario);
}
{% endhighlight %}

A partir da versão 3.5 o VRaptor possui suporte para validações nos métodos. Para isso é necessário ter no classpath alguma implementação do Bean Validator 1.1. Desta forma qualquer parâmetro pode ser validado usando anotações conforme o exemplo abaixo:

{% highlight java %}
public adiciona(@NotNull String name, @Future dueDate) {
    ...
}
{% endhighlight %}

<h3>Para onde redirecionar no caso de erro?</h3>

Outro ponto importante que deve ser levado em consideração no momento de fazer validações é o redirecionamento quando ocorrer um erro. Como enviamos o usuário para outro recurso com o VRaptor3, caso haja erro na validação?
Simples, apenas diga no seu código que quando correr um erro, é para o usuário ser enviado para algum recurso. Como no exemplo:

{% highlight java %}
public adiciona(Funcionario funcionario) {
    // Validação na forma fluente
    validator.checking(new Validations() { {
        that("erro","nome.nao.informado", !funcionario.getNome().isEmpty());
    } });

    // Validação na forma clássica
    if (!funcionario.getNome().equals("Fulano")) {
        validator.add(new ValidationMessage("erro","nomeInvalido"));
    }

    validator.onErrorUse(page()).of(FuncionarioController.class).formulario();

    dao.adiciona(funcionario);
}
{% endhighlight %}

Note que se sua lógica adiciona algum erro de validação você <strong>precisa</strong> dizer pra onde o VRaptor deve ir. O validator.onErrorUse funciona do mesmo jeito que o result.use: você pode usar qualquer view da classe Results.

<h3>Atalhos no Validator</h3>

Alguns redirecionamentos são bastante utilizados, então foram criados atalhos para eles. Os atalhos disponíveis são:

<ul>
	<li>validator.onErrorForwardTo(ClientController.class).list() ==> validator.onErrorUse(logic()).forwardTo(ClientController.class).list();</li>

	<li>validator.onErrorRedirectTo(ClientController.class).list() ==> validator.onErrorUse(logic()).redirectTo(ClientController.class).list();</li>

	<li>validator.onErrorUsePageOf(ClientController.class).list() ==> validator.onErrorUse(page()).of(ClientController.class).list();</li>

	<li>validator.onErrorSendBadRequest()	 ==> validator.onErrorUse(status()).badRequest(errors);</li>
</ul>

Além disso, se o redirecionamento é para um método do mesmo controller, podemos usar:

<ul>
	<li>validator.onErrorForwardTo(this).list() ==> validator.onErrorUse(logic()).forwardTo(this.getClass()).list();</li>

	<li>validator.onErrorRedirectTo(this).list() ==> validator.onErrorUse(logic()).redirectTo(this.getClass()).list();</li>

	<li>validator.onErrorUsePageOf(this).list() ==> validator.onErrorUse(page()).of(this.getClass()).list();</li>
</ul>

<h3>L10N com o bundle do Localization</h3>

Para forçar a tradução dos parâmetros do método i18n(), basta injetar o Localization e passar o seu bundle para o contrutor do Validations():

{% highlight java %}
private final Validator validator;
private final Localization localization;

public UsuarioController(Validator validator, Localization localization) {
    this.validator = validator;
    this.localization = localization;
}
validator.checking(new Validations(localization.getBundle()) { {
    that(usuario.getIdade() >= 18, "usuario.idade", "maior.que", i18n("usuario.idade"), 18);
} });
{% endhighlight %}

Repare que aqui está sendo passado manualmente o ResourceBundle, dessa forma a key "usuario.idade" será traduzida corretamente na troca do Locale.

<h3>Mostrando os erros de validação no JSP</h3>

Quando existem erros de validação, o VRaptor coloca um atributo na requisição chamado errors contendo a lista de erros, então você pode mostrá-los na sua JSP de um jeito parecido com:

{% highlight jsp %}
<c:forEach var="error" items="${errors}">
    ${error.category} - ${error.message}<br />
</c:forEach>
{% endhighlight %}
