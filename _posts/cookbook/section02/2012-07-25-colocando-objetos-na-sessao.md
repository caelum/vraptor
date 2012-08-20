---
section: 2
title: Colocando objetos na sessão
category: [pt, cookbook]
layout: page
---

Se você precisa colocar algum objeto na Sessão (e está usando o Spring como DI provider) você pode criar um componente Session scoped:

{% highlight java %}
@Component
@SessionScoped
public class UserInfo {

    private User user;
    
    // getter e setter
}
{% endhighlight %}

e se você quiser usar ou colocar o usuário na sessão, de dentro de algum Controller, por exemplo, você pode receber essa classe que você acabou de criar no construtor:

{% highlight java %}
@Resource
public class LoginController {
    
    private UserInfo info;
    
    public LoginController(UserInfo info) {
        this.info = info;
    }
    
    //...
    public void login(User user) {
        //valida o usuario
        this.info.setUser(user);
    }
}
{% endhighlight %}

E para acessar esse usuário a partir da view, existe um atributo no HttpSession com o nome "userInfo", assim você pode usar numa jsp:

{% highlight jsp %}
${userInfo.user}
{% endhighlight %}

