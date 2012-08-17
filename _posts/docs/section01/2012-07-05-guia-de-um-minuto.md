---
title: VRaptor3 - O guia inicial de 1 minuto
layout: page
language: pt
section: 1
category: [pt, docs]
---

O VRaptor 3 foca em simplicidade e, portanto, todas as funcionalidades que você verá têm como primeira meta resolver o problema do programador da maneira menos intrusiva possível em seu código.

Tanto para salvar, remover, buscar e atualizar ou ainda funcionalidades que costumam ser mais complexas como upload e download de arquivos, resultados em formatos diferentes (xml, json, xhtml etc), tudo isso é feito através de funcionalidades simples do VRaptor 3, que sempre procuram encapsular HttpServletRequest, Response, Session e toda a API do javax.servlet.

<h3>Começando um projeto</h3>

Você pode começar seu projeto a partir do vraptor-blank-project, que contem as dependências necessárias e a configuração no web.xml. Ele pode ser baixado no <a href="http://vraptor.caelum.com.br/download.jsp">site oficial do VRaptor</a>.

O projeto vraptor-blank-project é configurado para a IDE Eclipse. Se você usa Netbeans, é possível importar o projeto facilmente conforme a documentação disponível na <a href="http://netbeans.org/kb/docs/java/import-eclipse.html">seção de suporte do NetBeans</a>. Se você utiliza a IDE IntelliJ IDEA você pode importar o projeto seguindo o guia disponível na <a href="http://www.jetbrains.com/idea/webhelp/importing-eclipse-project-to-intellij-idea.html">página de ajuda do IntelliJ IDEA</a>.

Se você quiser usar o Maven, você pode adicionar o artefato do VRaptor como dependência no seu pom.xml:

{% highlight xml %}
<dependency>
    <groupId>br.com.caelum</groupId>
    <artifactId>vraptor</artifactId>
    <version>3.2.0</version><!--ou a última versão disponível-->
</dependency>
{% endhighlight %}

<h3>Um Controller simples</h3>

Chamaremos de <strong>Controller</strong> as classes contendo a lógica de negócio do seu sistema. São as classes que alguns frameworks podem vir a chamar de actions ou services, apesar de não significarem exatamente a mesma coisa.

Com o VRaptor configurado no seu web.xml, basta criar os seus controllers para receber as requisições e começar a construir seu sistema.

Um controller simples seria:


{% highlight java %}
/*
* anotando seu controller com @Resource, seus métodos públicos ficarão disponíveis
* para receber requisições web.
*/
@Resource
public class ClientsController {
   
    private ClientDao dao;
   
    /*
     * Você pode receber as dependências da sua classe no construtor, e o VRaptor vai
     * se encarregar de criar ou localizar essas dependências pra você e usá-las pra
     * criar o seu controller. Para que o VRaptor saiba como criar o ClientDao você
     * deve anotá-lo com @Component.
     */
    public ClientsController(ClientDao dao) {
        this.dao = dao;   
    }
   
    /*
     * Todos os métodos públicos do seu controller estarão acessíveis via web.
     * Por exemplo, o método form pode ser acessado pela URI /clients/form e
     * vai redirecionar para a jsp /WEB-INF/jsp/clients/form.jsp
     */
    public void form() {
        // código que carrega dados para checkboxes, selects, etc
    }
   
    /*
     * Você pode receber parâmetros no seu método, e o VRaptor vai tentar popular os
     * campos dos parâmetro de acordo com a requisição. Se houver na requisição:
     * custom.name=Lucas
     * custom.address=R.Vergueiro
     * então teremos os campos name e address do Client custom estarão populados com
     * Lucas e R.Vergueiro via getters e setters
     * URI: /clients/add
     * view: /WEB-INF/jsp/clients/add.jsp
     */
    public void add(Client custom) {
        dao.save(custom);
    }
   
    /*
     * O retorno do seu método é exportado para a view. Nesse caso, como o retorno é
     * uma lista de clientes, a variável acessível no jsp será ${clientList}.
     * URI: /clients/list
     * view: /WEB-INF/jsp/clients/list.jsp
     */
    public List<Client> list() {
        return dao.listAll():
    }
   
    /*
     * Se o retorno for um tipo simples, o nome da variável exportada será o nome da
     * classe com a primeira letra minúscula. Nesse caso, como retornou um Client, a
     * variável na jsp será ${client}.
     * Devemos ter um parâmetro da requisição id=5 por exemplo, e o VRaptor vai
     * fazer a conversão do parâmetro em Long, e passar como parâmetro do método.
     * URI: /clients/view
     * view: /WEB-INF/jsp/clients/view.jsp
     */
    public Client view(Long id) {
        return dao.load(id);
    }
}
{% endhighlight %}

Repare como essa classe está completamente independente da API de javax.servlet. O código também está extremamente simples e fácil de ser testado como unidade. O VRaptor já faz várias associações para as URIs como default:

{% highlight jsp %}
/client/form   invoca form()
/client/add    invoca add(client) populando o objeto client com os parâmetros da requisição
/clients/list  invoca list() e devolve ${clientList} ao JSP
/clients/view?id=3  invoca view(3l) e devolve ${client} ao JSP
{% endhighlight %}

Mais para a frente veremos como é fácil trocar a URI /clients/view?id=3 para /clients/view/3, deixando a URI mais elegante.

O ClientDao será injetado pelo VRaptor, como também veremos adiante. Você já pode passar para o Guia inicial de 10 minutos.
