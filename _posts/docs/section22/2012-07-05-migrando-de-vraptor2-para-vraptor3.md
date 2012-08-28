---
title: Migrando do VRaptor2 para o VRaptor3
layout: page
section: 22
category: [pt, docs]
---

<h3>web.xml</h3>

Para migrar aos poucos, basta colocar no seu web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>br.com.caelum.vraptor.vraptor2</param-value>
</context-param>

<filter>
    <filter-name>vraptor</filter-name>
    <filter-class>br.com.caelum.vraptor.VRaptor</filter-class>
</filter>

<filter-mapping>
    <filter-name>vraptor</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

{% endhighlight %}

e colocar os JARs da pasta lib/mandatory e lib/containers/<um dos containers> do zip do VRaptor no classpath.

Lembre-se de tirar a declaração antiga do VRaptorServlet do VRaptor2, e o seu respectivo mapping.


<h3>Migrando de @org.vraptor.annotations.Component para @br.com.caelum.vraptor.Resource</h3>

O correspondente ao @Component do VRaptor2 é o @Resource do VRaptor3. Portanto, para disponibilizar os métodos de uma classe como lógicas é só anotá-las com @Resource (removendo o @Component).

As convenções usadas são um pouco diferentes:

No VRaptor 2:

{% highlight java %}
@Component
public class ClientsLogic {
   
    public void form() {
   
    }
   
}
{% endhighlight %}

No VRaptor 3:

{% highlight java %}
@Resource
public class ClientsController {

   public void form() {
  
   }
}
{% endhighlight %}

O método form estará acessível pela uri: "/clients/form", e a view padrão será a WEB-INF/jsp/clients/form.jsp. Ou seja, o sufixo Controller é removido do nome da classe e não tem mais o sufixo .logic na URI. Não é colocado o resultado "ok" ou "invalid" no nome do JSP.


<h3>@In</h3>

O VRaptor3 gerencia as dependências para você; logo, o que você usava como @In no vraptor2, basta receber pelo construtor:

No VRaptor 2:

{% highlight java %}
@Component
public class ClientsLogic {
    @In
    private ClientDao dao;
       
    public void form() {
   
    }
   
}
{% endhighlight %}

No VRaptor 3:

{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    public ClientsController(ClientDao dao) {
        this.dao = dao;
    }
   
    public void form() {
  
    }
}
{% endhighlight %}

E, para que isso funcione, você só precisa que o seu ClientDao esteja anotado com o @br.com.caelum.vraptor.ioc.Component do VRaptor3.


<h3>@Out e getters</h3>

No VRaptor2 você usava a anotação @Out ou um getter para disponibilizar um objeto para a view. No VRaptor3 basta retornar o objeto, se for um só, ou usar um objeto especial para expor os objetos para a view. Este objeto é o Result:

No VRaptor 2:

{% highlight java %}
@Component
public class ClientsLogic {
    private Collection<Client> list;
   
    public void list() {
        this.list = dao.list();
    }
   
    public Collection<Client> getClientList() {
        return this.list;
    }

    @Out
    private Client client;
   
    public void show(Long id) {
        this.client = dao.load(id);
    }
   
}
{% endhighlight %}

No VRaptor 3:

{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
   
    public ClientsController(ClientDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }
   
    public Collection<Client> list() {
        return dao.list(); // o nome será clientList
    }
   
    public void listaDiferente() {
        result.include("clients", dao.list());
    }
   
    public Client show(Long id) {
        return dao.load(id); // o nome será "client"
    }
}
{% endhighlight %}

Quando você usa o retorno do método, o vraptor usa o tipo do retorno para determinar qual vai ser o seu nome na view. No caso de uma classe normal, o nome do objeto será o nome da classe com a primeira letra minúscula. No caso de ser uma Collection, o nome será o nome da classe, com a primeira minuscula, seguido da palavra List.


<h3>views.properties</h3>

No VRaptor3 não existe o arquivo views.properties, embora ele seja suportado no modo de compatibilidade com o VRaptor2. Todos os redirecionamentos são feitos na própria lógica, usando o Result:

{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
   
    public ClientsController(ClientDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }

    public Collection<Client> list() {
        return dao.list();
    }

    public void save(Client client) {
        dao.save(client);
       
        result.redirectTo(ClientsController.class).list();
    }
}
{% endhighlight %}

Se o redirecionamento for para uma lógica, você pode referenciá-la diretamente, e os parâmetros passados para o método serão usados para chamar a lógica.

Se for para uma JSP direto você pode usar:

{% highlight java %}
result.forwardTo("/WEB-INF/jsp/clients/save.ok.jsp");
{% endhighlight %}

<h3>Validação</h3>

Você não precisa criar um método validateNomeDaLogica para fazer a validação, basta receber no construtor um objeto do tipo br.com.caelum.vraptor.Validator, e usá-lo para sua validação, especificando qual é a lógica para usar quando a validação dá errado:

{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
    private final Validator validator;
   
    public ClientsController(ClientDao dao, Result result, Validator validator) {
        this.dao = dao;
        this.result = result;
        this.validator = validator;
    }

    public void form() {
   
    }
   
    public void save(Client client) {
        if (client.getName() == null) {
            validator.add(new ValidationMessage("erro","nomeInvalido"));
        }
        validator.onErrorUse(Results.page()).of(ClientsController.class).form();
        dao.save(client);
    }
}
{% endhighlight %}

<h3>Colocando objetos na sessão</h3>

No VRaptor2 bastava colocar um @Out(ScopeType.SESSION) para que o objeto fosse colocado na sessão. Isso não funciona no VRaptor3, pois você perde totalmente o controle sobre as variáveis que estão anotadas desse jeito.

Para colocar objetos na sessão no VRaptor3 você deve fazer uma das duas coisas:

O objeto vai ser acessível apenas por lógicas e componentes da aplicação, não pelos JSPs:

{% highlight java %}
    @Component
    @SessionScoped
    public class MeuObjetoNaSessao {
        private MeuObjeto meuObjeto;
        //getter e setter para meuObjeto
    }
{% endhighlight %}

E nas classes onde você precisa do MeuObjeto basta receber no construtor o MeuObjetoNaSessao e usar o getter e setter pra manipular o MeuObjeto.

O objeto vai ser acessível nos jsps também:

{% highlight java %}
    @Component
    @SessionScoped
    public class MeuObjetoNaSessao {
        private HttpSession session;
        public MeuObjetoNaSessao(HttpSession session) {
            this.session = session;
        }
        public void setMeuObjeto(MeuObjeto objeto) {
            this.session.setAttribute("objeto", objeto);
        }
        public MeuObjeto getMeuObjeto() {
            return this.session.getAttribute("objeto");
        }
    }
{% endhighlight  %}

E nas classes basta receber o MeuObjetoNaSessao no construtor e usar o getter e o setter.
