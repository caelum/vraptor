---
section: 9
title: Poupando recursos&#58; lazy dependency injection
language: pt
category: cookbook
layout: page
---

<h4>por Tomaz Lavieri no <a href="http://blog.tomazlavieri.com.br/2009/vraptor-3-poupando-recursos-lazy-dependence-injection/">blog</a></h4>

Objetivo: Ao final deste artigo espera-se que você saiba como poupar recursos caros, trazendo eles de forma lazy, ou como prefiro chamar Just-in-Time (no momento certo).
No VRaptor3 a injeção de dependência ficou bem mais fácil, os interceptadores que eram os responsáveis para injetar a dependência sumiram e agora fica tudo a cargo do container, que pode ser o Spring ou o Pico.
A facilidade na injeção de dependencia tem um custo, como não é mais controlado pelo programador que cria o interceptor sempre que declaramos uma dependência no construtor de um @Component, @Resource ou @Intercepts ele é injetado no início, logo na construção, porém às vezes o fluxo de um requisição faz com que não usemos algumas destas injeções de dependência, disperdiçando recursos valiosos.
Por exemplo, vamos supor o seguinte @Resource abaixo, que cadastra produtos

{% highlight java %}
import java.util.List;
import org.hibernate.Session;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Resource
public class ProdutoController {
    /**
     * O recurso que queremos poupar.
     */
    private final Session session;
    private final Result result;
   
    public ProdutoController(final Session session, final Result result) {
        this.session = session;
        this.result = result;
    }
   
    /**
     * apenas renderiza o formulário
     */
    public void form() {}
   
    public List<Produto> listar() {
        return session.createCriteria(Produto.class).list();
    }
   
    public Produto adiciona(Produto produto) {
        session.persist(produto);
        result.use(Results.logic()).redirectTo(getClass()).listar();
        return produto;
    }
}
{% endhighlight %}

Sempre que alguem faz uma requisição a qualquer lógica dentro do recurso ProdutoController uma Session é aberta, porem note que abrir o formulário para adicionar produtos não requer sessão com o banco, ele apenas renderiza uma página, cada vez que o formulário de produtos é aberto um importante e caro recurso do sistema esta sendo requerido, e de forma totalmente ociosa.

<div class="nota">
<h4>Nota do editor</h4>
É criada no máximo uma Session por requisição. A mesma coisa para qualquer componente Request scoped.
</div>

Como agir neste caso? Isolar o formulário poderia resolver este problema mais recairia em outro, da mantenabilidade.
O ideal é que este recurso só fosse realmente injetado no tempo certo (Just in Time) como seria possível fazer isso? A solução é usar proxies dinâmicos, enviando uma Session que só realmente abrirá a conexão com o banco quando um de seus métodos for invocado.

{% highlight java %}
import java.lang.reflect.Method;
import javax.annotation.PreDestroy;
import org.hibernate.classic.Session;
import org.hibernate.SessionFactory;
import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
* <b>JIT (Just-in-Time) {@link Session} Creator</b> fábrica para o 
* componente {@link Session} gerado de forma LAZY ou JIT(Just-in-Time) 
* a partir de uma {@link SessionFactory}, que normalmente se encontra 
* em um ecopo de aplicativo @{@link ApplicationScoped}.
*
* @author Tomaz Lavieri
* @since 1.0
*/
@Component
@RequestScoped
public class JITSessionCreator implements ComponentFactory<Session> {
       
    private static final Method CLOSE = 
            new Mirror().on(Session.class).reflect().method("close").withoutArgs();
    private static final Method FINALIZE = 
            new Mirror().on(Object.class).reflect().method("finalize").withoutArgs();
            
    private final SessionFactory factory;
    /** Guarda a Proxy Session */
    private final Session proxy;
    /** Guarada a Session real. */
    private Session session;
   
    public JITSessionCreator(SessionFactory factory, Proxifier proxifier) {
        this.factory = factory;
        this.proxy = proxify(Session.class, proxifier); // *1*
    }
   
    /**
     * Cria o JIT Session, que repassa a invocação de qualquer método, exceto
     * {@link Object#finalize()} e {@link Session#close()}, para uma session real, 
     * criando uma se necessário.
     */
    private Session proxify(Class<? extends Session> target, Proxifier proxifier) {
        return proxifier.proxify(target, new MethodInvocation<Session>() {
            @Override // *2*
            public Object intercept(Session proxy, Method method, Object[] args, 
                                                            SuperMethod superMethod) {
                if (method.equals(CLOSE) 
                        || (method.equals(FINALIZE) && session == null)) {
                    return null; //skip
                }
                return new Mirror().on(getSession()).invoke().method(method)
                                    .withArgs(args);
            }
        });
    }
   
    public Session getSession() {
        if (session == null) // *3*
                session = factory.openSession();
        return session;
    }
   
    @Override
    public Session getInstance() {
        return proxy; // *4*
    }
   
    @PreDestroy
    public void destroy() { // *5*
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
{% endhighlight %}

<h1>Explicando alguns pontos chaves, comentados com // *N*</h1>

<ul class="cookbook">
<li>O Proxfier é um objeto das libs do VRaptor que auxilia na criação de objetos proxys ele é responsável por escolher a biblioteca que implementa o proxy dinâmico, e então invocar via callback um método interceptor, como falamos abaixo.</li>

<li>Neste ponto temos a implementação do nosso interceptor, sempre que um método for invocado em nosso proxy, esse intereptor é invocado primeiro, ele filtra as chamadas ao método finalize caso a Session real ainda não tenha sido criada, isso evita criar a Session apenas para finaliza-la. O método close também é filtrado, isso é feito para evitar criar uma session apenas para fechá-la, e também por que o nosso SessionCreator é que é o responsavel por fechar a session ao final do scopo, quando a request acabar. Todos os outros métodos são repassados para uma session através do método getSession() onde é realmente que acontece o LAZY ou JIT.</li>

<li>Aqui é onde acontece a mágica, da primeira vez que getSession() é invocado a sessão é criada, e então repassada, todas as outras chamadas a getSession() repassam a sessão anteriormente criada, assim, se getSession() nunca for invocado, ou seja, se nenhum método for invocado no proxy, getSession() nunca será invocado, e a sessão real não será criada.</li>

<li>O retorno desse ComponentFactory é a Session proxy, que só criará a session real se um de seus métodos for invocado.</li>

<li>Ao final do escopo o destroy é invocado, ele verifica se a session real existe, existindo verifica se esta ainda esta aberta, e estando ele fecha, desta forma é possivel garantir que o recurso será sempre liberado. Assim podemos agora pedir uma Session sempre que acharmos que vamos precisar de uma, sabendo que o recurso só será realmente solicitado quando formos usar um de seus métodos, salvando assim o recurso.</li>
</ul>

Esta mesma abordagem pode ser usada para outros recursos caros do sistema.

Os códigos fonte para os ComponentFactory de EntityManager e Session que utilizo podem ser encontrados no <a href="http://guj.com.br/posts/list/141500.java">fórum do GUJ</a>.
