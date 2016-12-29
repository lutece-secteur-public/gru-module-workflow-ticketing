<jsp:useBean id="modifyTicketCategory" scope="session" class="fr.paris.lutece.plugins.workflow.modules.ticketing.web.ModifyTicketCategoryJspBean" />
<% String strContent = modifyTicketCategory.processController ( request , response ); %>

<%= strContent %>
