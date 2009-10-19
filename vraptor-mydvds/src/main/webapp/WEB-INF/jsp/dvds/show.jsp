<%@ include file="/header.jsp" %> 

<div class="blue-box" id="message">
<h1>${dvd.title} <fmt:message key="dvd_added"/></h1>
<hr/>
<a href="<c:url value="/home" />"><fmt:message key="home"/></a>
</div>

<%@ include file="/footer.jsp" %> 