<%@ include file="/header.jsp" %> 
<div id="blue-box">
<h1>${user.name}</h1>
<hr/>
<table>
<c:forEach var="dvd" items="${user.dvds}">
	<tr>
	   <td>
	       <form action="<c:url value="/dvds/addToList/${dvd.id}" />" method="post" class="buttonForm">
	           <button type="submit" class="link"><fmt:message key="add_to_my_list"/></button>
	       </form>
	   </td>
	   <td>${dvd.title}</td>
	   <td>${dvd.description}</td>
	   <td><fmt:message key="${dvd.type}"/></td>
    </tr>
</c:forEach>
</table>
</div>
<%@ include file="/footer.jsp" %>