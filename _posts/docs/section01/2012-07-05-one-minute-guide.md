---
title: VRaptor3 - One minute guide
layout: page
section: 1
categories: [en, docs]
---

VRaptor 3 focuses in simplicity and, therefore, all of its functionalities have as main goal solve the developer's problem in the less intrusive way.
Either CRUD operations or more complex functionalities such as download, upload, results in different formats (xml, json, xhtml etc), everything is done through VRaptor3's simple and easy-to-understand functionalities. You don't have to deal directly with HttpServletRequest, Responses or any javax.servlet API, although you still have the control of all Web operations.

<h3>Starting up</h3>

You can start your project based on vraptor-blank-project, available on <a href="https://code.google.com/p/vraptor3/downloads/list">https://code.google.com/p/vraptor3/downloads/list</a> . It contains all required jar dependencies, and the minimal web.xml configuration for working with VRaptor.
The vraptor-blank-project project is configured to work with Eclipse. But if you use Netbeans IDE you can import project using the guide avaliable on <a href="http://netbeans.org/kb/docs/java/import-eclipse.html">http://netbeans.org/kb/docs/java/import-eclipse.html</a>. If you use IntelliJ IDEA you can import the blank project using the guide avaliable on <a href="http://www.jetbrains.com/idea/webhelp/importing-eclipse-project-to-intellij-idea.html">http://www.jetbrains.com/idea/webhelp/importing-eclipse-project-to-intellij-idea.html</a>.
If you want to use Maven, you can add VRaptor's Maven artifact on your pom.xml:

{% highlight xml %}
<dependency>
    <groupId>br.com.caelum</groupId>
    <artifactId>vraptor</artifactId>
    <version>3.2.0</version><!--or the latest version-->
</dependency>
{% endhighlight %}

<h3>A simple controller</h3>

Having VRaptor properly configured on your web.xml, you can create your controllers for dealing with web requests and start building your system.
A simple controller would be:

{% highlight java %}
/*
* You should annotate your controller with @Resource, so all of its public methods will
* be ready to deal with web requests.
*/
@Resource
public class ClientsController {

    private ClientDao dao;

    /*
     * You can get your class dependencies through constructor, and VRaptor will be in charge
     * of creating or locating these dependencies and manage them to create your controller.
     * If you want that VRaptor3 manages creation of ClientDao, you should annotate it with
     * @Component
     */
    public ClientsController(ClientDao dao) {
        this.dao = dao;
    }

    /*
     * All public methods from your controller will be reachable through web.
     * For example, form method can be accessed by URI /clients/form,
     * and will render the view /WEB-INF/jsp/clients/form.jsp
     */
    public void form() {
        // code that loads data for checkboxes, selects, etc
    }

    /*
     * You can receive parameters on your method, and VRaptor will set your parameters
     * fields with request parameters. If the request have:
     * custom.name=Lucas
     * custom.address=Vergueiro Street
     * VRaptor will set the fields name and address of Client custom with values
     * "Lucas" and "Vergueiro Street", using the fields setters.
     * URI: /clients/add
     * view: /WEB-INF/jsp/clients/add.jsp
     */
    public void add(Client custom) {
        dao.save(custom);
    }

    /*
     * VRaptor will export your method return value to the view. In this case,
     * since your method return type is List<Clients>, then you can access the
     * returned value on your jsp with the variable ${clientList}
     * URI: /clients/list
     * view: /WEB-INF/jsp/clients/list.jsp
     */
    public List<Client> list() {
        return dao.listAll():
    }

    /*
     * If the return type is a simple type, the name of exported variable will be
     * the class name with the first letter in lower case. Since this method return
     * type is Client, the variable will be ${client}.
     * A request parameter would be something like id=5, and then VRaptor is able
     * to get this value, convert it to Long, and pass it as parameter to your method.
     * URI: /clients/view
     * view: /WEB-INF/jsp/clients/view.jsp
     */
    public Client view(Long id) {
        return dao.load(id);
    }
}
{% endhighlight %}

Note this class is independent of javax.servlet API. The code is also very simple and can be unit tested easily. VRaptor will make associations with these URIs by default:

{% highlight jsp %}
/client/form   invokes form()
/client/add    invokes add(client) populating the client with request parameters
/clients/list  invokes list() and returns ${clientList} to JSP
/clients/view?id=3  invokes view(3l) and returns ${client} to JSP
{% endhighlight %}

We'll see later how easy it is to change the URI /clients/view?id=3 to the more elegant /clients/view/3.
ClientDao will also be injected by VRaptor, as we'll see. You can see now the Ten minutes guide.
