<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<c:forEach var="error" items="${errors }">
${error.message }
</c:forEach>
</html>