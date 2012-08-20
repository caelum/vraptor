---
title: Interceptadores
layout: page
section: 6
category: [pt, docs]
---

<h3>Para quê interceptar?</h3>

O uso de interceptadores é feito para executar alguma tarefa antes e/ou depois de uma lógica de negócios, sendo os usos mais comuns para validação de dados, controle de conexão e transação do banco, log e criptografia/compactação de dados.

<h3>Como interceptar?</h3>

No VRaptor 3 utilizamos uma abordagem onde o interceptador define quem será interceptado, muito mais próxima a abordagens de interceptação que aparecem em sistemas baseados em AOP (programação orientada a aspectos) do que a abordagem da versão anterior do vraptor.
Portanto, para interceptar uma requisição basta implementar a interface <strong>Interceptor</strong> e anotar a classe com a anotação <strong>@Intercepts</strong>.
Assim como qualquer outro componente, você pode dizer em que escopo o interceptador, será armazenado através das anotações de escopo.

{% highlight java %}
public interface Interceptor {

    void intercept(InterceptorStack stack, ResourceMethod method, 
                    Object resourceInstance) throws InterceptionException;

    boolean accepts(ResourceMethod method);

}
{% endhighlight %}

<h3>Exemplo simples</h3>

A classe a seguir mostra um exemplo de como interceptar todas as requisições em um escopo de requisição e simplesmente mostrar na saída do console o que está sendo invocado.
Lembre-se que o interceptador é um componente como qualquer outro e pode receber em seu construtor quaisquer dependencias atraves de Injecao de Dependencias.

{% highlight java %}
@Intercepts
@RequestScoped
public class Log implements Interceptor {

    private final HttpServletRequest request;

    public Log(HttpServletRequest request) {
        this.request = request;
    }

    /*
     * Este interceptor deve interceptar o method dado? Neste caso vai interceptar
     * todos os métodos.
     * method.getMethod() retorna o método java que está sendo executado
     * method.getResourceClass().getType() retorna a classe que está sendo executada
     */
    public boolean accepts(ResourceMethod method) {
        return true; 
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, 
                        Object resourceInstance) throws InterceptionException {
        System.out.println("Interceptando " + request.getRequestURI());
        // código a ser executado antes da lógica
        
        stack.next(method, resourceInstance); // continua a execução
        
        // código a ser executádo depois da lógica
    }

}
{% endhighlight %}

<h3>Exemplo com Hibernate</h3>

Provavelmente, um dos usos mais comuns do Interceptor é para a implementação do famigerado pattern Open Session In View, que fornece uma conexão com o banco de dados sempre que há uma requisição para sua aplicação. E ao fim dessa requisição, a conexão é liberada. O grande ganho disso é evitar exceções como LazyInitializationException no momento da renderização dos jsps.
Abaixo, está um simples exemplo, que para todas as requisições abre uma transação com o banco de dados. E ao fim da execução da lógica e da exibição da página para o usuário, commita a transação e logo em seguida fecha a conexão com o banco.

{% highlight java %}
@RequestScoped
@Intercepts
public class DatabaseInterceptor implements br.com.caelum.vraptor.Interceptor {

    private final Database controller;

    public DatabaseInterceptor(Database controller) {
        this.controller = controller;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object instance)
        throws InterceptionException {
        try {
            controller.beginTransaction();
            stack.next(method, instance);
            controller.commit();
        } finally {
            if (controller.hasTransaction()) {
                controller.rollback();
            }
            controller.close();
        }
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
{% endhighlight %}

Dessa forma, no seu Recurso, bastaria o seguinte código para utilizar a conexão disponível:

{% highlight java %}
@Resource
public class FuncionarioController {
    
    public FuncionarioController(Result result, Database controller) {
        this.result = result;
        this.controller = controller;
    }
    
    @Post
    @Path("/funcionario")
    public void adiciona(Funcionario funcionario) {
        controller.getFuncionarioDao().adiciona(funcionario);
        ...
    }
}
{% endhighlight %}

<h3>Como garantir ordem: after e before</h3>

Se você precisa garantir a ordem de execução de um conjunto de interceptors, você pode usar os atributos <strong>after</strong> e <strong>before</strong> da @Intercepts. Suponha que o PrimeiroInterceptor tenha que rodar antes do SegundoInterceptor, então você pode configurar isso tanto com:

{% highlight java %}
@Intercepts(before=SegundoInterceptor.class)
public class PrimeiroInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

quanto com:

{% highlight java %}
@Intercepts(after=PrimeiroInterceptor.class)
public class SegundoInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

Você pode especificar mais de um Interceptor:

{% highlight java %}
@Intercepts(after={PrimeiroInterceptor.class, SegundoInterceptor.class}, 
            before={QuartoInterceptor.class, QuintoInterceptor.class})
public class TerceiroInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

O VRaptor lançará uma exception se existir um ciclo na ordem dos interceptors, então tenha cuidado.

<h3>Interagindo com os interceptors do VRaptor</h3>

O VRaptor possui sua própria sequência de interceptor, e você pode definir a ordem dos seus interceptors baseados na do VRaptor.
Aqui estão os principais interceptors do VRaptor e o que eles produzem:

<ul>
	<li><strong>ResourceLookupInterceptor</strong> - o primeiro interceptor. Procura o ResourceMethod correto, que será passado como argumento nos métodos accepts() e intercept(). É o after padrão.</li>

	<li><strong>ParametersInstantiatorInterceptor</strong> - instancia os parâmetros do método baseado no request. Os parâmetros estarão disponíveis via MethodInfo#getParameters().</li>

	<li><strong>InstantiateInterceptor</strong> - instancia o controller, que será passado como último parâmetro do método intercept().</li>

	<li><strong>ExecuteMethodInterceptor</strong> - executa o ResourceMethod. O retorno do método estará disponível via MethodInfo#getResult(). É o before padrão.</li>

	<li><strong>OutjectResult</strong> - inclui o retorno do ResourceMethod na view.</li>

	<li><strong>ForwardToDefaultViewInterceptor</strong> - redireciona (forward) para a jsp padrão se nenhuma outra view foi usada.</li>
</ul>

Se você precisa executar um Interceptor após o ExecuteMethodInterceptor, você <strong>deve</strong> setar o atributo before, para evitar ciclos. O ForwardToDefaultViewInterceptor é um bom valor, já que nenhum outro interceptor pode rodar depois dele:

{% highlight java %}
@Intercepts(after=ExecuteMethodInterceptor.class, 
            before=ForwardToDefaultViewInterceptor.class)
{% endhighlight %}
