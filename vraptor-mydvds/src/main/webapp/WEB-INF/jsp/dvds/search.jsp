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
			        <form action="<c:url value="/dvds/addToList/${dvd.id}" />" method="post" class="buttonForm">
			            <button type="submit" class="link"><fmt:message key="add_to_my_list"/></button>
			        </form>
				</td>
				<td>${dvd.title}</td>
				<td>${dvd.description}</td>
				<td><fmt:message key="${dvd.type}"/></td>
				<td>
					<c:forEach var="copy" items="${dvd.copies}">
						${copy.owner.name}<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.jsp" %> 