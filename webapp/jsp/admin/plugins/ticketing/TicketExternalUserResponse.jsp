<jsp:useBean id="ticketExternalUserResponse" scope="session" class="fr.paris.lutece.plugins.workflow.modules.ticketing.web.TicketExternalUserResponseJspBean" />

<% String strContent = ticketExternalUserResponse.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
