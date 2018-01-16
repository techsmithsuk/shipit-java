<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/xml;charset=UTF-8" %>

<shipit>
    <response>
        <success>true</success>
        <c:if test="${not empty content}">
            <jsp:useBean id="content" scope="request" type="com.softwire.training.shipit.model.RenderableAsXML"/>
            ${content.renderXML()}
        </c:if>
    </response>
</shipit>
