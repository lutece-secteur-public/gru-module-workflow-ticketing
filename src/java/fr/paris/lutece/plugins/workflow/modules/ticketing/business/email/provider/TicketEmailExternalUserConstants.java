/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.provider;

/**
 * Constants class for the task EmailExternalUser
 */
public final class TicketEmailExternalUserConstants
{
    // Path for JspBean
    public static final String ADMIN_EXTERNAL_USER_CONTROLLLER_PATH = "jsp/admin/plugins/workflow/modules/ticketing/";

    // Mark
    public static final String MARK_GUID = "identification_guid";
    public static final String MARK_USER_TITLE = "civility";
    public static final String MARK_USER_FIRSTNAME = "firstname";
    public static final String MARK_USER_LASTNAME = "lastname";
    public static final String MARK_USER_CONTACT_MODE = "contact_mode";
    public static final String MARK_USER_FIXED_PHONE = "fixed_phone";
    public static final String MARK_USER_MOBILE_PHONE = "mobile_phone";
    public static final String MARK_USER_EMAIL = "email";
    public static final String MARK_USER_UNIT_NAME = "unit_name";
    public static final String MARK_USER_MESSAGE = "user_message";
    public static final String MARK_TICKET_REFERENCE = "reference";
    public static final String MARK_TICKET_CHANNEL = "ticket_channel";
    public static final String MARK_TICKET_COMMENT = "comment";
    public static final String MARK_TECHNICAL_URL_COMPLETED = "url_completed";
    public static final String MARK_TECHNICAL_LIST_FORM = "list_form";
    public static final String MARK_TICKET = "ticket";
    public static final String MARK_TECHNICAL_LIST_MARKER = "list_markers";
    public static final String MARK_EMAIL_RECIPIENTS = "email_recipients";
    public static final String MARK_EMAIL_RECIPIENTS_CC = "email_recipients_cc";
    public static final String MARK_MESSAGE = "message";
    public static final String MARK_LINK = "ticketing_ticket_link";
    public static final String MARK_SUBJECT = "email_subject";
    public static final String MARK_CATEGORY = "ticket_category_level_";

    // nb relance
    public static final String MARK_NB_AUTOMATIC_NOTIFICATION = "nb_automatic_notification";
    // date derniere relance
    public static final String MARK_LAST_AUTOMATIC_NOTIFICATION_DATE = "last_automatic_notification_date";

    // Parameters URL
    public static final String PARAMETER_ID_MESSAGE_EXTERNAL_USER = "id_message_external_user";
    public static final String PARAMETER_SIGNATURE = "signature";
    public static final String PARAMETER_ID_TIMETAMP = "timestamp";

    /**
     * Private constructor
     */
    private TicketEmailExternalUserConstants( )
    {
    }
}
