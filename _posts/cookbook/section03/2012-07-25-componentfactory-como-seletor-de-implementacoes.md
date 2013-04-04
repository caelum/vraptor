---
section: 3
title: ComponentFactory como seletor de implementações
categories: [pt, cookbook]
layout: page
---

O uso típico para as ComponentFactories é para quando a dependência que você quer usar não faz parte do VRaptor nem da sua aplicação, como é o caso da Session do Hibernate.
Mas você pode usar as ComponentFactories para disponibilizar implementações de interfaces de acordo com alguma condição, por exemplo alguma configuração adicional.
Vamos supor que você quer fazer um enviador de Email, mas só quer usar o enviador de verdade quando o sistema estiver em produção, em desenvolvimento você quer um enviador falso:

{% highlight java %}
public interface EnviadorDeEmail {
    void enviaEmail(Email email);
}

public class EnviadorDeEmailPadrao implements EnviadorDeEmail {
    //envia o email de verdade
}

public class EnviadorDeEmailFalso implements EnviadorDeEmail {
    //não faz nada, ou apenas loga o email
}
{% endhighlight %}

Sem anotar nenhuma dessas classes com @Component, você pode criar um ComponentFactory de EnviadorDeEmail, e anotá-lo com @Component:

{% highlight java %}
@Component
@ApplicationScoped //ou @RequestScoped se fizer mais sentido
public class EnviadorDeEmailFactory implements ComponentFactory<EnviadorDeEmail> {

    private EnviadorDeEmail enviador;
    public EnviadorDeEmail(ServletContext context) {
        if ("producao".equals(context.getInitParameter("tipo.de.ambiente"))) {
            enviador = new EnviadorDeEmailPadrao();
        } else {
            enviador = new EnviadorDeEmailFalso();
        }

    }

    public EnviadorDeEmail getInstance() {
        return this.enviador;
    }
}
{% endhighlight %}
