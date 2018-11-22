<%@page import="fr.paris.lutece.portal.service.admin.AdminAuthenticationService"%>
<jsp:useBean id="ticketExternalUserResponseTimeout" scope="session" class="fr.paris.lutece.plugins.workflow.modules.ticketing.web.TicketExternalUserResponseJspBean" />
<% ticketExternalUserResponseTimeout.init( request , "TICKETING_EXTERNAL_USER" ); %>

<% String strContent = ticketExternalUserResponseTimeout.getTimeout( request ); %>

<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../../AdminFooter.jsp" %>

<% AdminAuthenticationService.getInstance( ).logoutUser( request ); %>
