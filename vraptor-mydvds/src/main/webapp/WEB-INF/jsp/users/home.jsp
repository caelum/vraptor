<%@ include file="/header.jsp" %> 

<script type="text/javascript" src="<c:url value="/"/>js/jquery.js"></script>

<div class="blue-box">
<table id="user">
	<tr><td align="right">${currentUser.name} - <a href="<c:url value="/home/logout" />"><fmt:message key="logout"/></a></td></tr>
</table>
</div>

<c:if test="${not empty errors}">
	<div id="error-box">
	<h1><fmt:message key="errors"/></h1>
	<hr/>
	<c:forEach var="error" items="${errors}">
		${error.message}<br/>
	</c:forEach>
	</div>
</c:if>


<br/><br/>

<div class="blue-box">
<h1>${currentUser.name}: <fmt:message key="your_dvds"/></h1>
<hr/>

<table>
<c:forEach var="dvd" items="${currentUser.dvds}">
	<tr><td>${dvd.title}</td><td>${dvd.description}</td><td><fmt:message key="${dvd.type}"/></td></tr>
</c:forEach>
</table>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="new_dvd"/></h1>
<hr/>
<w:form action="/dvds" type="table" enctype="multipart/form-data" name="dvdRegister" method="post" border="0">
	<w:text name="dvd.title" />
	<w:text name="dvd.description" />
	<w:file name="file" label="sample_file" />
	<w:selectTokens name="dvd.type" tokens="MUSIC,MUSIC,VIDEO,VIDEO,GAME,GAME" var="type">
		<fmt:message key="${type}" />
	</w:selectTokens>
	<w:submit value="send"/>
</w:form>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="search_dvds"/></h1>
<hr/>
<table>
<w:form action="/dvds/search" type="table">
	<w:text name="dvd.title" id="dvdTitle" autocomplete="off" />
	<w:submit value="search"/>
</w:form>
</table>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="list_users"/></h1>
<hr/>
<w:form action="/users" method="get">
	<w:submit value="search"/>
</w:form>
</div>

<%@ include file="/footer.jsp" %> 