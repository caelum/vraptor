---
title: Conversores
layout: page
section: 5
category: [pt, docs]
---

<h3>Padrão</h3>

Por padrão o VRaptor já registra diversos conversores para o seu uso no dia a dia.

<h3>Tipos primitivos</h3>

Todos os tipos primitivos (int, long etc) são suportados.
Caso o parametro da requisição seja nulo ou a string vazia, variáveis de tipo primitivo serão alterados para o valor padrão como se essa variável fosse uma variável membro, isto é:

<ul>
	<li>boolean - false</li>
	<li>short, int, long, double, float, byte - 0</li>
	<li>char - caracter de código 0</li>
</ul>

<h3>Wrappers de tipos primitivos</h3>

Todos os wrappers dos tipos primitivos (Integer, Long, Character, Boolean etc) são suportados.

<h3>Enum</h3>

Todas as enumerações são suportadas através do nome do elemento ou de seu ordinal. No exemplo a seguir, tanto o valor 1 como o valor DEBITO são traduzidos para Tipo.DEBITO:

{% highlight java %}
public enum Tipo {
    CREDITO, DEBITO
}
{% endhighlight %}

<h3>BigInteger e BigDecimal</h3>

Ambos são suportados utilizando o padrão de localização da virtual machine que serve a sua aplicação.

<h3>BigDecimal, Double e Float localizados</h3>

A partir da versão 3.1.2 o VRaptor suporta esses tipos baseados na localização da virtual machine. Para ativar esses componentes é necessário adicionar as seguintes linhas no seu web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        ...valor anterior..., 
        br.com.caelum.vraptor.converter.l10n
    </param-value>
</context-param>
{% endhighlight %}

A localização dos componentes pode ser alterada utilizando a seguinte configuração no web.xml:

{% highlight xml %}
<context-param>
    <param-name>javax.servlet.jsp.jstl.fmt.locale</param-name>
    <param-value>pt_BR</param-value>
</context-param>
{% endhighlight %}

<h3>Calendar e Date</h3>

LocaleBasedCalendarConverter e LocaleBasedDateConverter utilizam o locale do usuário, definido seguindo o padrão do jstl para entender a formatação que foi utilizada no parâmetro.
Por exemplo, se o locale é pt-br, o formato "18/09/1981" representa 18 de setembro de 1981 enquanto para o locale en, o formato "09/18/1981" representa a mesma data.

<h3>LocalDate, LocalTime e LocalDateTime do joda-time</h3>

Existem conversores para esses dois tipos no VRaptor e eles só serão carregados se você tiver o joda-time.jar no seu classpath

<h3>Interface</h3>

Todos os conversores devem implementar a interface Converter do vraptor. A classe concreta definirá o tipo que ela é capaz de converter, e será invocada com o valor do parâmetro do request, o tipo alvo e um bundle com as mensagens de internacionalização para que você possa retornar uma ConversionException no caso de algum erro de conversão.

{% highlight java %}
public interface Converter<T> {
    T convert(String value, Class<? extends T> type, ResourceBundle bundle);
}
{% endhighlight %}

Além disso, seu conversor deve ser anotado dizendo agora para o VRaptor (e não mais para o compilador java) qual o tipo que seu conversor é capaz de converter, para isso utilize a anotação @Convert:

{% highlight java %}
@Convert(Long.class)
public class LongConverter implements Converter<Long> {
    // ...
}
{% endhighlight %}

Por fim, lembre-se de dizer se seu conversor pode ser instanciado em um escopo de Application, Session ou Request, assim como recursos e componentes quaisquer do VRaptor. Por exemplo, um conversor que não requer nenhuma informação do usuário logado pode ser registrado no escopo de Application e instanciado uma única vez:

{% highlight java %}
@Convert(Long.class)
@ApplicationScoped
public class LongConverter implements Converter<Long> {
    // ...
}
{% endhighlight %}

A seguir, a implementação de LongConverter mostra como tudo isso pode ser utilizado de maneira bem simples:

{% highlight java %}
@Convert(Long.class)
@ApplicationScoped
public class LongConverter implements Converter<Long> {

    public Long convert(String value, Class<? extends Long> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
                    throw new ConversionError(MessageFormat
                        .format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
{% endhighlight %}

<h3>Registrando um novo conversor</h3>

Não é necessária nenhuma configuração além do @Convert e implementar a interface Converter para que o conversor seja registrado no Container do VRaptor.
