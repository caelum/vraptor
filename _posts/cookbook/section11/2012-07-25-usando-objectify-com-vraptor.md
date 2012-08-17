---
section: 11
title: Usando Objectify com VRaptor
language: pt
category: [pt, cookbook]
layout: page
---

<h4>por Otávio Scherer Garcia</h4>

Para trabalhar de forma simples no VRaptor com a Objectify é necessário criar uma classe que inicie a instância da ObjectifyFactory. Como precisamos apenas de uma única instância esta classe pode possuir escopo de aplicação:

{% highlight java %}
@Component
@ApplicationScoped
public class ObjectifyFactoryCreator
    implements ComponentFactory<ObjectifyFactory> {

    private final ObjectifyFactory instance = new ObjectifyFactory();

    @PostConstruct
    public void create() {
        instance.register(MyFirstEntity.class);
        instance.register(MySecondEntity.class);
    }

    public ObjectifyFactory getInstance() {
        return instance;
    }
}
{% endhighlight %}

Após criamos a classe que vai ser responsável por criar as intâncias de Objectify. Como precisamos de uma única instância para cada requisição, o escopo deste componente será request.

{% highlight java %}
@Component
@RequestScoped
public class ObjectifyCreator
    implements ComponentFactory<Objectify> {

    private final ObjectifyFactory factory;
    private Objectify ofy;

    public ObjectifyCreator(ObjectifyFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void create() {
        ofy = factory.begin();
    }

    public Objectify getInstance() {
        return ofy;
    }
}
{% endhighlight %}

Para utilizar a Objectify basta injetá-la no construtor conforme o exemplo abaixo:

{% highlight java %}
public class MyController {
    private final Objectify ofy;

    public MyController(Objectify ofy) {
        this.ofy = ofy;
    }

    public void anything() {
        ofy.get(new Key<MyFirstEntity>(MyFirstEntity.class, 1L));
    }
}
{% endhighlight %}
