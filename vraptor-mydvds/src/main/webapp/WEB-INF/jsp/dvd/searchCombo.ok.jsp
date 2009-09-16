<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach var="dvd" items="${dvds}">
${dvd.title}
</c:forEach>
