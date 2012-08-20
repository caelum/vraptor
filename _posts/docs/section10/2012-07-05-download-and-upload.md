---
title: Download and Upload
layout: page
section: 10
category: [en, docs]
---

<h3>One minute example: download</h3>

The following example shows how to expose the file to be downloaded to its client.
Again, see how simple this code is:

{% highlight java %}
@Resource
public class ProfileController {

    public File picture(Profile profile) {
        return new File("/path/to/the/picture." + profile.getId()+ ".jpg");
    }
}
{% endhighlight %}

<h3>Adding more info to download</h3>

If you want to add more information to download, you can return a FileDownload:

{% highlight java %}
@Resource
public class ProfileController {

    public Download picture(Profile profile) {
        File file = new File("/path/to/the/picture." + profile.getId()+ ".jpg");
        String contentType = "image/jpg";
        String filename = profile.getName() + ".jpg";
        
        return new FileDownload(file, contentType, filename); 
    }
}
{% endhighlight %}

You can also use the InputStreamDownload implementation to work with Streams or ByteArrayDownload to work with byte array (since 3.2-snapshot).

<h3>Upload</h3>

The upload component is optional. To enable this feature, you only need to add the commons-upload library in your classpath.
Since VRaptor 3.2, if you're in a container that implements the JSR-315 (Servlet 3.0), you don't need commons-upload nor commons-io libraries because the servlet container already have this.

<h3>One minute example: upload</h3>

The first example is based on the multipart upload feature.

{% highlight java %}
@Resource
public class ProfileController {

    private final ProfileDao dao;

    public ProfileController(ProfileDao dao) {
        this.dao = dao;
    }

    public void updatePicture(Profile profile, UploadedFile picture) {
        dao.update(picture.getFile(), profile);
    }
}
{% endhighlight %}

<h3>More about Upload</h3>

UploadedFile returns the file content as a InputStream. If you want to save this file on disk in an easy way, you can use the commons-io IOUtils:

{% highlight java %}
public void updatePicture(Profile profile, UploadedFile picture) {
    File pictureOnDisk = new File();    
    IOUtils.copyLarge(picture.getFile(), new FileOutputStream(pictureOnDisk));
    dao.atribui(pictureOnDisk, profile);
}
{% endhighlight %}

<h3>Overriding upload settings</h3>

You can also change the default upload settings overriding the class MultipartConfig. In example below, the size limit of upload is changed.

{% highlight java %}
@Component
@ApplicationScoped
public class CustomMultipartConfig extends DefaultMultipartConfig {

    public long getSizeLimit() {
        return 50 * 1024 * 1024; // 50MB
    }

}
{% endhighlight %}

<h3>Changes in form</h3>

You need to add the parameter enctype with multipart/form-data value. Without this attribute, the browser cannot upload files:

{% highlight jsp %}
<form action="minha-action" method="post" enctype="multipart/form-data">
{% endhighlight %}

<h3>Validating upload</h3>

When the maximum size for uploaded file exceeds the configured value, VRaptor add a message on the Validator object.
