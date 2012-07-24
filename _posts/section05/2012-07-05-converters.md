---
title: Converters
layout: page
language: en
section: 5
---

<h3>Default</h3>

VRaptor registers a default set of converters for your day-to-day use.

<h3>Primitive types</h3>

All primitive types (int, long etc) are supported.
If the request parameter is empty or null, primitive type variables will be set to its default value, as if it was a class attribute. In general:

<ul>
	<li>boolean - false</li>
	<li>short, int, long, double, float, byte - 0</li>
	<li>char - caracter de código 0</li>
</ul>

<h3>Primitive type wrappers</h3>

All primitive type wrappers (Integer, Long, Character, Boolean etc) are supported.

<h3>Enum</h3>

Enums are also supported using the element's name or ordinal value. In the following example, either 1 or DEBIT values are converted to Type.DEBIT:

{% highlight java %}
public enum Type {
	CREDIT, DEBIT
}
{% endhighlight %}

<h3>BigInteger and BigDecimal</h3>

Both are supported using your JVM's default locale.


<h3>Localized BigDecimal, Double e Float</h3>

Since version 3.1.2, VRaptor also supports localization for these types. In order to use them you must add to your web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <param-value>
        ...previous value...,
        br.com.caelum.vraptor.converter.l10n
    </param-value>
</context-param>
{% endhighlight %}

The converters uses the JVM's default locale. You can override the JVM locale adding these lines in your web.xml:

{% highlight xml %}
<context-param>
    <param-name>javax.servlet.jsp.jstl.fmt.locale</param-name>
    <param-value>pt_BR</param-value>
</context-param>
{% endhighlight %}

<h3>Calendar and Date</h3>

Both LocaleBasedCalendarConverter and LocaleBasedDateConverter are based on the user's locale, defined using JSTL pattern to understand the parameter's format.
For example, if the locale is pt-br, then "18/09/1981" stands for September 18th 1981. On the other hand, if the locale is en, the same date is formatted as "09/18/1981".

<h3>Interface</h3>

All converters must implement VRaptor's Converter interface. The concrete class will define which type it is able to convert, and will be invoked with a request parameter, the target type and a resource bundle containing i18n messages, useful if you wish to raise a ConversionException in case of convertion errors.

{% highlight java %}
public interface Converter<T> {
    T convert(String value, Class<? extends T> type, ResourceBundle bundle);
}
{% endhighlight %}

Also, your must tell VRaptor (not the compiler) which type your converter is able to handle. You do that by annotating your converter class with @Convert:

{% highlight java %}
@Convert(Long.class)
public class LongConverter implements Converter<Long> {
	// ...
}
{% endhighlight %}

Finally, don't forget to specify the scope of your converter, just like you do with any other resource in VRaptor. For example, if your converter doesn't need any user specific information, it can be registered as application scoped and only one instance of that converter will be created:

{% highlight java %}
@Convert(Long.class)
@ApplicationScoped
public class LongConverter implements Converter<Long> {
	// ...
}
{% endhighlight %}

In the following lines, you can see a LongConverter implementation, showing how simple it is to assemble all the information mentioned above:

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
			throw new 
	ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
{% endhighlight %}

<h3>Registering a new converter</h3>

No further configuration is needed except implementing the Converter interface and annotating the converter class with @Convert for your custom converter to be registered in VRaptor's container.
