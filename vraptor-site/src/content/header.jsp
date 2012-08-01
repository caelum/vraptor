<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:setLocale value="${locale}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="author" content="Caelum"/>
	<meta name="reply-to" content="contato@caelum.com.br"/>
	<meta name="author" content="Design"/>
	<c:if test="${param.docs}">
		<c:set var="path">../..</c:set>
		<link href="${path }/documentacao/includes/css/java.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="${path }/documentacao/includes/css/xml2html.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="${path }/documentacao/includes/css/style.css" rel="stylesheet" type="text/css" media="screen" />
	</c:if>
	<meta name="description" content="<fmt:message key="meta.description"/>"/>
	<meta name="keywords" content="sites, web, desenvolvimento, development, java, opensource"/>
	<title>V|Raptor - <fmt:message key="title.slogan"/></title>
	<link href="${path }/screen.css" rel="stylesheet" type="text/css" media="screen" />
    <link href="${path }/menu.css" rel="stylesheet" type="text/css" media="screen" />
    <!--[if lt IE 7]>
    <script src="http://ie7-js.googlecode.com/svn/version/2.0(beta3)/IE7.js" type="text/javascript"></script>
    <![endif]-->
</head>

<body>
	<div id="headerWrap">
    	<div id="headerContent">
        	<h1 id="logoVraptor"><span>V|Raptor</span></h1><!-- vraptorlogo-->
            
            <ul id="langMenu">
            	<li><a id="engBtn" href="${path }/en/"><span>ENGLISH</span></a></li>
                <li><a id="ptBtn" href="${path }/pt/"><span>PORTUGUÊS</span></a></li>
            </ul><!-- langMenu-->            
        </div><!-- header content -->
    </div><!-- header wrap-->
    
    <div id="menuWrap">
    	<ul id="<fmt:message key='menu.id'/>">
        	<li><a id="<fmt:message key='home.id'/>" href="${path }/<fmt:message key='home.link'/>"><span>home</span></a></li>
        	<li><a id="<fmt:message key='download.id'/>" href="${path }/<fmt:message key='download.link'/>"><span>download</span></a></li>
        	<li><a id="<fmt:message key='docs.id'/>" href="<fmt:message key='documentacao.link'/>"><span><fmt:message key="documentacao"/></span></a></li>
        	<li><a id="<fmt:message key='beneficios.id'/>" href="${path }/<fmt:message key='beneficios.link'/>"><span><fmt:message key="beneficios"/></span></a></li>
        	<li><a id="<fmt:message key='suporte.id'/>" href="${path }/<fmt:message key='suporte.link'/>"><span><fmt:message key="suporte"/></span></a></li>
        	<li><a id="<fmt:message key='vraptor2.id'/>" href="${path }/<fmt:message key='vraptor2.link'/>"><span>vraptor2</span></a></li>
        </ul><!-- menuElements-->
    </div><!-- menuWrap-->
