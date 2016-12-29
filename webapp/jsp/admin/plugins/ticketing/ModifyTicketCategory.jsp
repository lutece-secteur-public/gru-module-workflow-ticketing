<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="fr.paris.lutece.portal.service.i18n.I18nService"%>
<jsp:useBean id="modifyTicketCategory" scope="session" class="fr.paris.lutece.plugins.workflow.modules.ticketing.web.ModifyTicketCategoryJspBean" />
<% String strContent = modifyTicketCategory.processController ( request , response ); %>

<% if ( StringUtils.isNotEmpty( strContent ) ) { %>
	<div class="form-group">
		<label class="col-xs-12 col-sm-3 col-md-3 col-lg-3 control-label" for="generic_attributes"><%=I18nService.getLocalizedString("module.workflow.ticketing.task_modify_ticket_category.labelListEntries", request.getLocale() )%>&nbsp;:</label>
		<div class="col-xs-12 col-sm-9 col-md-6 col-lg-6" style="padding-left: 30px">
			<%= strContent %>
		</div>
	</div>
<% } %>
