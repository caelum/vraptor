---
title: Componentes
layout: page
language: pt
section: 4
category: docs
---

<h3>O que são componentes?</h3>

Componentes são instâncias de classes que seu projeto precisa para executar tarefas ou armazenar estados em diferentes situações.
Exemplos clássicos de uso de componentes seriam os DAOs, enviadores de email etc.
A sugestão de boa prática indica sempre criar uma interface para seus componentes. Dessa maneira seu código também fica mais fácil de ser testado unitariamente.
O exemplo a seguir mostra um componente a ser gerenciado pelo VRaptor:

{% highlight java %}
@Component
public class ClienteDao {

  private final Session session;
  public ClienteDao(Session session) {
      this.session = session;
  }
  
  public void adiciona(Cliente cliente) {
        session.save(cliente);
  }
  
}
{% endhighlight %}

<h3>Escopos</h3>

Assim como os recursos, os componentes vivem em um escopo específico e seguem as mesmas regras, por padrão pertencendo ao escopo de requisicão, isto é, a cada nova requisição seu componente será novamente instanciado.
O exemplo a seguir mostra o fornecedor de conexões com o banco baseado no Hibernate. Esse fornecedor está no escopo de aplicacação, portanto será instanciado somente uma vez por contexto:

{% highlight java %}
@ApplicationScoped
@Component
public class HibernateControl {

  private final SessionFactory factory;
  public HibernateControl() {
      this.factory = new AnnotationConfiguration().configure().buildSessionFactory();
  }
  
  public Session getSession() {
      return factory.openSession();
  }
  
}
{% endhighlight %}

Os escopos implementados são:

<ul>
	<li>@RequestScoped - o componente é o mesmo durante uma requisição</li>
	<li>@SessionScoped - o componente é o mesmo durante uma http session</li>
	<li>@ApplicationScoped - component é um singleton, apenas um por aplicação</li>
	<li>@PrototypeScoped - component é instanciado sempre que requisitado.</li>
</ul>

<h3>ComponentFactory</h3>

Muitas vezes você quer receber como dependência da sua classe alguma classe que não é do seu projeto, como por exemplo uma Session do Hibernate ou um EntityManager da JPA.
Para poder fazer isto, basta criar um ComponentFactory:

{% highlight java %}
@Component
public class SessionCreator implements ComponentFactory<Session> {

    private final SessionFactory factory;
    private Session session;

    public SessionCreator(SessionFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void create() {
        this.session = factory.openSession();
    }

    public Session getInstance() {
        return session;
    }

    @PreDestroy
    public void destroy() {
        this.session.close();
    }

}
{% endhighlight %}

Note que você pode adicionar os listeners @PostConstruct e @PreDestroy para controlar a criação e destruição dos recursos que você usa. Isso funciona para qualquer componente que você registrar no VRaptor.

<h3>Injeção de dependências</h3>

O VRaptor utiliza um de seus provedores de injeção de dependências para controlar o que é necessário para instanciar cada um de seus componentes e recursos.
Sendo assim, os dois exemplos anteriores permitem que quaisquer um dos seus recursos ou componentes recebam um ClienteDao em seu construtor, por exemplo:

{% highlight java %}
@Resource
public class ClienteController {
    private final ClienteDao dao;
    
    public ClienteController(ClienteDao dao) {
        this.dao = dao;
    }

    @Post
    public void adiciona(Cliente cliente) {
        this.dao.adiciona(cliente);
    }
    
}
{% endhighlight %}
