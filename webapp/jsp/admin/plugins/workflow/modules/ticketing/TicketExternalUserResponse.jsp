<jsp:useBean id="ticketExternalUserResponse" scope="session" class="fr.paris.lutece.plugins.workflow.modules.ticketing.web.TicketExternalUserResponseJspBean" />

<% String strContent = ticketExternalUserResponse.processController ( request , response ); %>

<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../../AdminFooter.jsp" %>

<script>
setTimeout(function () {
	   window.location.href = "TicketExternalUserResponseTimeout.jsp"; //will redirect to error page in 28 minutes (just before WSSO 30 minutes)
	}, 28*60*1000);

</script>
