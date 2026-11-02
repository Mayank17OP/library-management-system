<%--
    Start page of the application.
    The container serves index.jsp for "/" and we immediately forward the
    request to the HomeServlet, so the MVC flow always starts at a controller.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:forward page="/home"/>
