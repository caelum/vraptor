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
	<meta name="reply-to" content="lokidg@gmail.com"/>

	<meta name="description" content="<fmt:message key="meta.description"/>"/>
	<meta name="keywords" content="sites, web, desenvolvimento, development, java, opensource"/>
	<title>VRaptor - MyDvds</title>
	<link href="<c:url value="/mydvds.css"/>" rel="stylesheet" type="text/css" media="screen" />
    <!--[if lt IE 7]>
    <script src="http://ie7-js.googlecode.com/svn/version/2.0(beta3)/IE7.js" type="text/javascript"></script>
    <![endif]-->
</head>
<body>
	<c:if test="${not empty param.language}">
		<fmt:setLocale value="${param.language}" scope="session"/>
	</c:if>
	<div id="headerWrap">
    	<div id="headerContent">
        	<h1 id="logoVraptor"><span>V|Raptor</span></h1><!-- vraptorlogo-->
            <ul id="langMenu">
            	<li><a id="engBtn" href="?language=en"><span>ENGLISH</span></a></li>
                <li><a id="ptBtn" href="?language=pt_BR"><span>PORTUGUÊS</span></a></li>
            </ul><!-- langMenu-->
        </div><!-- header content -->
    </div><!-- header wrap-->
    <c:set var="path"><c:url value="/"/></c:set>
    <c:if test="${not empty userInfo.user}">
	    <div id="userInfo">
	    	<p>${userInfo.user.name } - <a href="${path }home/logout">Logout</a></p>
	    </div>
    </c:if>
    <div id="menuWrap">
    	<form class="busca" action="${path }dvds/search" method="get">
	    	<ul id="menuElementsEn">
	        	<li><a href="${path }"><span>home</span></a></li>
			    <c:if test="${not empty userInfo.user}">
			    	<li><a href="<c:url value="/users"/>"><fmt:message key="list_users"/></a></li>
			    	<li><input type="text" name="dvd.title" value="<fmt:message key="search.dvd"/>" 
			    			onfocus="this.value='';" 
		        			onblur="if (this.value == '') this.value='<fmt:message key="search.dvd"/>';"/>
		        		<button type="submit"><fmt:message key="search"/></button></li>
			    </c:if>
	        </ul><!-- menuElements-->
	    </form>
    </div><!-- menuWrap-->
	<c:if test="${not empty errors}">
		<div id="errors">
			<ul>
				<c:forEach items="${errors }" var="error">
					<li>${error.category } - ${error.message }</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
	<c:if test="${not empty notice}">
		<div id="notice">
			<p>${notice }</p>
		</div>
	</c:if>
	<div id="contentWrap">