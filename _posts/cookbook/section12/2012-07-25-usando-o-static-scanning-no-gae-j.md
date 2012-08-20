---
section: 12
title: Usando o Static Scanning no GAE/J
category: [pt, cookbook]
layout: page
---

<h4>por Otávio Scherer Garcia</h4>

Uma das formas para diminuir o tempo de inicialização do VRaptor no GAE/J é utilizar o Static Scanning. Com esta funcionalidade o VRaptor busca pelos componentes no tempo de build. Desta forma o Scanning Dinâmico não é necessário.
Basta adicionar as seguintes linhas no seu build.xml e logo após incluir esta task como dependência da task update.

{% highlight xml%}
<target name="vraptor-scanning" depends="compile">
    <path id="build.classpath">
        <fileset dir="war/WEB-INF/lib" includes="*.jar" />
    </path>

    <java classpathref="build.classpath" 
            classname="br.com.caelum.vraptor.scan.VRaptorStaticScanning" fork="true">
        <arg value="war/WEB-INF/web.xml" />
        <classpath refid="build.classpath" />
        <classpath path="war/WEB-INF/classes" />
    </java>
</target>
{% endhighlight %}
