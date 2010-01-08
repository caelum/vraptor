<%@ include file="/header.jsp" %> 
<h1>${user.name}</h1>
<table>
<c:forEach var="dvd" items="${user.dvds}">
	<tr>
	   <td>
	       <form action="<c:url value="/users/${userInfo.user.login}/dvds/${dvd.id}" />" method="post" class="buttonForm">
	       		<input type="hidden" name="_method" value="PUT"/>
	           <button type="submit" class="link"><fmt:message key="add_to_my_list"/></button>
	       </form>
	   </td>
	   <td>${dvd.title}</td>
	   <td>${dvd.description}</td>
	   <td><fmt:message key="${dvd.type}"/></td>
    </tr>
</c:forEach>
</table>
<%@ include file="/footer.jsp" %>