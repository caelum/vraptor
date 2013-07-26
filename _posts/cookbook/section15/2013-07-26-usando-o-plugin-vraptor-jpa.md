---
section: 15
title: Usando o plugin VRaptor JPA
categories: [pt, cookbook]
layout: page
---

<h4>por Willian Aquino</h4>

<h6>Downloads</h6>

- VRaptor Blank Project  3.5.1 https://code.google.com/p/vraptor3/downloads/list
- Hibernate 3.6 Final http://sourceforge.net/projects/hibernate/files/hibernate3/3.6.0.Final/
- vraptor-jpa-1.0.0.jar http://repo1.maven.org/maven2/br/com/caelum/vraptor/vraptor-jpa/1.0.0/vraptor-jpa-1.0.0.jar
- JSTL http://jstl.java.net/download.html


Extraia o Vraptor, dentro desta pasta que foi extraída selecionei todos os aquivos e criei um novo zip.

Crie um projeto novo *Dynamic Web Project* no eclipse.

No seu projeto de um botão direito e selecione *import* e depois *import* novamente.

Na janela que se abre selecione *General* e depois clique em *Archive File*.

Selecione o zip do VRaptor Blank Project e click em *Finish*.

Agora no seu projeto entre na pasta *WebContent/WEB-INF/lib*

Coloque na pasta *lib* o jar *vraptor-jpa-1.0.0.jar* e os jars do *Hibernate*

Esse tutorial ajuda:
http://blog.caelum.com.br/as-dependencias-do-hibernate-3-5/

No seu web.xml coloque essa anotação:

{% highlight xml%}
    <context-param>
        <param-name>br.com.caelum.vraptor.packages</param-name>
        <param-value>br.com.caelum.vraptor.util.jpa</param-value>
    </context-param>
{% endhighlight %}


Na pasta *src* do seu projeto crie uma nova pasta chamada *WEB-INF*,  e dentro dela crie um arquivo xml chamado *persistence.xml*

E coloque esse script. Eu estou usando o MYSQL, se você usa outro banco use outro formato.

{% highlight xml%}
    <?xml version="1.0" encoding="UTF-8"?>
    <persistence version="1.0" xmlns="http://java.sun.com/xml/ns/persistence"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd">

      <persistence-unit name="default" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <class>br.com.caelum.model.Usuario</class>
        <properties>
          <property name="hibernate.connection.username" value="USUARIO"/>
          <property name="hibernate.connection.password" value="SENHA"/>
          <property name="hibernate.connection.driver_class" value="com.mysql.jdbc.Driver"/>
          <property name="hibernate.connection.url" value="jdbc:mysql://localhost:3306/NOME_DO_BANCO"/>
          <property name="hibernate.cache.provider_class" value="org.hibernate.cache.NoCacheProvider"/>
          <property name="hibernate.hbm2ddl.auto" value="update"/>
          <property name="hibernate.show_sql" value="true"/>
        </properties>
      </persistence-unit>
    </persistence>
{% endhighlight %}


<h6>Classes para teste</h6>

Dentro da pasta *src* faça o seguinte.

Dentro do pacote *br.com.caelum.vraptor.blank*, abra o *IndexController* e coloque a class abaixo.

{% highlight java%}
    import br.com.caelum.dao.UsuarioDao;
    import br.com.caelum.model.Usuario;
    import br.com.caelum.vraptor.Path;
    import br.com.caelum.vraptor.Post;
    import br.com.caelum.vraptor.Resource;
    import br.com.caelum.vraptor.Result;

    @Resource
    public class IndexController {

      private final Result result;
      private final UsuarioDao userDao;

      public IndexController(Result result, UsuarioDao userDao) {
        this.result = result;
        this.userDao= userDao;
      }

      @Path("/")
      public void index() {}

      @Post @Path("/cadastro")
      public void cadastro(Usuario usuario){


          //System.out.println(usuario);

          this.userDao.InsertUser(usuario);
          this.result.forwardTo(IndexController.class).index();
      }
    }
{% endhighlight %}


Crie um pacote chamada *br.com.caelum.model* e crie essa classe dentro.

{% highlight java%}
    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.Id;


    @Entity
    public class Usuario {

        @Id @GeneratedValue
        private long idUsuario;
        private String nome;
        private String sobreNome;

        public long getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(long idUsuario) {
            this.idUsuario = idUsuario;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getSobreNome() {
            return sobreNome;
        }

        public void setSobreNome(String sobreNome) {
            this.sobreNome = sobreNome;
        }
    }
{% endhighlight %}


Crie um pacote chamada *br.com.caelum.dao* e crie essa classe dentro.

{% highlight java%}
    import javax.persistence.EntityManager;
    import br.com.caelum.model.Usuario;
    import br.com.caelum.vraptor.ioc.Component;


    @Component
    public class UsuarioDao {

        //private EntityManager em = new EntityManagerCreator().getInstance();

        private EntityManager em;

        public UsuarioDao(EntityManager em){
            this.em = em;
        }

        public void InsertUser(Usuario usuario) {
            try{
                this.em.persist(usuario);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
{% endhighlight %}


Agora dentro da pasta *WebContent/WEB-INF/jsp/index*

Abra o jsp *index.jsp* e coloque esse HTML.

{% highlight xml%}
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    </head>
    <body>

    <form action="<c:url value='/cadastro'/>" method="post">
          Nome:         <input type="text" name="usuario.nome" /><br/>
          SobreNome:    <input type="text" name="usuario.sobreNome" /><br/>
          <input type="submit" value="Salvar" />
      </form>


    </body>
    </html>
{% endhighlight %}
