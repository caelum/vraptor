<%@ include file="/header.jsp" %> 

<h1><fmt:message key="search_results"/></h1>

<table>
	<thead>
		<tr>
			<td></td>
			<td>Title</td>
			<td>Description</td>
			<td>Type</td>
			<td>Owners</td>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="dvd" items="${dvds}">
			<tr class="${s.count % 2 == 0? 'even': 'odd' }">
			    <td>
			        <form action="<c:url value="/users/${userInfo.user.login}/dvds/${dvd.id}" />" method="post" class="buttonForm">
	       				<input type="hidden" name="_method" value="PUT"/>
			            <button type="submit" class="link"><fmt:message key="add_to_my_list"/></button>
			        </form>
				</td>
				<td>${dvd.title}</td>
				<td>${dvd.description}</td>
				<td><fmt:message key="${dvd.type}"/></td>
				<td>
					<c:forEach var="copy" items="${dvd.rents}">
						${rent.owner.name}<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 