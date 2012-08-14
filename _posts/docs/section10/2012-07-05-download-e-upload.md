---
title: Download e Upload
layout: page
language: pt
section: 10
category: docs
---

<h3>Exemplo de 1 minuto: download</h3>

O exemplo a seguir mostra como disponibilizar o download para seu cliente.
Note novamente a simplicidade na implementação:

{% highlight java %}
@Resource
public class PerfilController {

    public File foto(Perfil perfil) {
        return new File("/path/para/a/foto." + perfil.getId()+ ".jpg"); 
    }
}
{% endhighlight %}

<h3>Adicionando mais informações no download</h3>

Se você quiser adicionar mais informações ao download você pode retornar um FileDownload:

{% highlight java %}
@Resource
public class PerfilController {

    // dao ...

    public Download foto(Perfil perfil) {
        File file = new File("/path/para/a/foto." + perfil.getId()+ ".jpg");
        String contentType = "image/jpg";
        String filename = perfil.getNome() + ".jpg";
        
        return new FileDownload(file, contentType, filename); 
    }
}
{% endhighlight %}

Você pode também usar a implementação InputStreamDownload para trabalhar diretamente com Streams ou ByteArrayDownload para array de bytes (desde a 3.2-snapshot).

<h3>Upload</h3>

Para ativar o suporte a upload é necessário adicionar as bibliotecas commons-upload e commons-io em seu classpath.
Na versão 3.2 do VRaptor, caso você estiver em um container que implemente a JSR-315 (Servlet 3.0), não serão necessários os JARs commons-upload e commons-io, pois o próprio container já implementa a funcionalidade de upload.

<h3>Exemplo de 1 minuto: upload</h3>

O primeiro exemplo será baseado na funcionalidade de upload multipart.

{% highlight java %}
@Resource
public class PerfilController {

    private final PerfilDao dao;

    public PerfilController(PerfilDao dao) {
        this.dao = dao;
    }

    public void atualizaFoto(Perfil perfil, UploadedFile foto) {
        dao.atribui(foto.getFile(), perfil);
    }
}
{% endhighlight %}

<h3>Mais sobre Upload</h3>

O UploadedFile retorna o arquivo como um InputStream. Se você quiser copiar para um arquivo no disco facilmente, basta usar o IOUtils do commons-io:

{% highlight java %}
public void atualizaFoto(Perfil perfil, UploadedFile foto) {
    File fotoSalva = new File();    
    IOUtils.copyLarge(foto.getFile(), new FileOutputStream(fotoSalva));
    dao.atribui(fotoSalva, perfil);
}
{% endhighlight %}

<h3>Sobrescrevendo as configurações de upload</h3>

Você pode alterar as configurações de upload sobrescrevendo a classe MultipartConfig. No exemplo abaixo é alterado o tamanho máximo de upload.

{% highlight java %}
@Component
@ApplicationScoped
public class CustomMultipartConfig extends DefaultMultipartConfig {

    public long getSizeLimit() {
        return 50 * 1024 * 1024; // 50MB
    }

}
{% endhighlight %}

<h3>Alterando o formulário de envio</h3>

Para que o browser possa fazer o upload corretamente, você precisa adicionar o atributo enctype para multipart/form-data:

{% highlight jsp %}
<form action="minha-action" method="post" enctype="multipart/form-data">
{% endhighlight %}

<h3>Validando o upload</h3>

Quando o tamanho máximo para upload de arquivo exceder o valor configurado, o VRaptor adiciona uma mensagem no objeto Validator.
