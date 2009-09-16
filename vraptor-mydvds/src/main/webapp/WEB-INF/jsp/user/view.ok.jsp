<%@ include file="/header.jsp" %> 
<div id="blue-box">
<h1>${user.name}</h1>
<hr/>
<table>
<c:forEach var="dvd" items="${user.dvds}">
	<tr><td><a href="dvd.addToMyList.logic?dvd.id=${dvd.id}"><fmt:message key="add_to_my_list"/></a></td><td>${dvd.title}</td><td>${dvd.description}</td><td><fmt:message key="${dvd.type}"/></td></tr>
</c:forEach>
</table>
</div>
<%@ include file="/footer.jsp" %> 