---
title: Components
layout: page
section: 4
category: [en, docs]
---

<h3>What are components?</h3>

Components are object instances that your application need to execute tasks or to keep state in different situations.
DAOs and e-mail senders are classic component examples.
The best practices suggest you should always create interfaces for your components to implement. This makes your code much easier to unit test.
The following example shows a VRaptor-managed component:

{% highlight java %}
@Component
public class ClientDao {

  private final Session session;
  public ClientDao(HibernateControl control) {
      this.session = control.getSession()
  }
  
  public void add(Client client) {
    session.save(client);
  }
  
}
{% endhighlight %}

<h3>Scopes</h3>

Just like resources, components live in specific scopes and follow the same rules. The default scope for a component is the request scope, meaning that a new instance will be created for each request.
The following example shows a Hibernate-based connection provider. The application scope is specified for the provider, so only one instance per application context will be created:

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

The implemented scopes are:

<ul>
	<li>@RequestScoped - component is the same during a request</li>
	<li>@SessionScoped - component is the same during an Http session</li>
	<li>@ApplicationScoped - component is a singleton, only one per application</li>
	<li>@PrototypeScoped - component is instantiated whenever it is requested.</li>
</ul>

<h3>ComponentFactory</h3>

It can happen that one of your class dependencies doesn't belong to your project, like the Session from Hibernate or EntityManager from JPA.
In order to do that you can create a ComponentFactory:

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

Note that you can add listeners like @PostConstruct and @PreDestroy to manage creation and destruction of you factory resources. You can use these listeners on any component that you register on VRaptor.
Dependency injection
VRaptor uses one of its own dependency injection providers to control what it needs in order to create new instances of your components and resources.
For that reason, the former two examples allow any of your resources or components to receive a ClientDao in its constructor. For example:

{% highlight java %}
@Resource
public class ClientController {
    private final ClientDao dao;
    
    public ClientController(ClientDao dao) {
        this.dao = dao;
    }

    @Post
    public void add(Client client) {
        this.dao.add(client);
    }
    
}
{% endhighlight %}

You can also use method injection. But keep in mind that with this way, all redirect/forwards to these methods will inject NULL.

{% highlight java %}
@Resource
public class ClienteController {

    @Post
    public void adiciona(Cliente cliente, ClienteDao dao) {
        dao.adiciona(cliente);
    }
}
{% endhighlight %}
