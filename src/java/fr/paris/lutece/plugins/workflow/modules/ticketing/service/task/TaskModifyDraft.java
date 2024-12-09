/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.task;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.arrondissement.Arrondissement;
import fr.paris.lutece.plugins.ticketing.business.arrondissement.ArrondissementHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.quartier.Quartier;
import fr.paris.lutece.plugins.ticketing.business.quartier.QuartierHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.ticketing.service.strois.STroisService;
import fr.paris.lutece.plugins.ticketing.service.strois.StockageService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.bean.BeanUtil;

/**
 * This class represent a task to modify a ticket
 */
public class TaskModifyDraft extends AbstractTicketingTask
{
    // Messages
    private static final String MESSAGE_TASK_MODIFY_DRAFT                             = "module.workflow.ticketing.task_modify_draft.labelModifyDraft";
    private static final String MESSAGE_MODIFY_TICKET_USER_TITLE_INFORMATION          = "module.workflow.ticketing.task_modify_ticket.user_title_information";
    private static final String MESSAGE_MODIFY_TICKET_LASTNAME_INFORMATION            = "module.workflow.ticketing.task_modify_ticket.lastname_information";
    private static final String MESSAGE_MODIFY_TICKET_FIRSTNAME_INFORMATION           = "module.workflow.ticketing.task_modify_ticket.firstname_information";
    private static final String MESSAGE_MODIFY_TICKET_EMAIL_INFORMATION               = "module.workflow.ticketing.task_modify_ticket.email_information";
    private static final String MESSAGE_MODIFY_TICKET_FIXED_PHONE_NUMBER_INFORMATION  = "module.workflow.ticketing.task_modify_ticket.fixed_phone_number_information";
    private static final String MESSAGE_MODIFY_TICKET_MOBILE_PHONE_NUMBER_INFORMATION = "module.workflow.ticketing.task_modify_ticket.mobile_phone_number_information";
    private static final String MESSAGE_MODIFY_TICKET_ARRONDISSEMNT_INFORMATION       = "module.workflow.ticketing.task_modify_ticket.arrondissement_information";
    private static final String MESSAGE_MODIFY_TICKET_QUARTIER_INFORMATION            = "module.workflow.ticketing.task_modify_ticket.quartier_information";
    private static final String MESSAGE_MODIFY_TICKET_ADDRESS_INFORMATION             = "module.workflow.ticketing.task_modify_ticket.address_information";
    private static final String MESSAGE_MODIFY_TICKET_ADDRESS_DETAIL_INFORMATION      = "module.workflow.ticketing.task_modify_ticket.address_detail_information";
    private static final String MESSAGE_MODIFY_TICKET_CITY_INFORMATION                = "module.workflow.ticketing.task_modify_ticket.city_information";
    private static final String MESSAGE_MODIFY_TICKET_POSTAL_CODE_INFORMATION         = "module.workflow.ticketing.task_modify_ticket.postal_code_information";
    private static final String MESSAGE_MODIFY_TICKET_CONTACT_MODE_INFORMATION        = "module.workflow.ticketing.task_modify_ticket.contact_mode_information";
    private static final String MESSAGE_MODIFY_TICKET_COMMENT_INFORMATION             = "module.workflow.ticketing.task_modify_ticket.comment_information";
    private static final String MESSAGE_MODIFY_TICKET_NO_MODIFICATIONS_INFORMATION    = "module.workflow.ticketing.task_modify_ticket.no_modifications_information";
    private static final String MESSAGE_MODIFY_TICKET_ATTACHMENT                      = "module.workflow.ticketing.task_modify_ticket_attachment.information";

    // Constant
    private static final String NOT_FILLED_INFORMATION                                = "module.workflow.ticketing.task_modify_ticket.no_information";
    private static final String SERVEUR_SIDE                                          = AppPropertiesService.getProperty( TicketingConstants.PROPERTY_STROIS_SERVEUR );

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND                              = "Resource not found";

    @Inject
    private TicketFormService   _ticketFormService;

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_TASK_MODIFY_DRAFT, locale );
    }

    @Override
    protected String processTicketingTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strTaskInformation = StringUtils.EMPTY;
        Ticket ticket = getTicket( nIdResourceHistory );

        // save current values, clear ticket
        List<Response> listCurrentResponse = ticket.getListResponse( );


        // Gets Map of Response by idEntry
        Map<Integer, List<Response>> currentResponsesByIdEntry = listCurrentResponse.stream( )
                .filter( r -> StringUtils.equals( r.getEntry( ).getEntryType( ).getBeanName( ), EntryTypeFile.BEAN_NAME ) ).collect( Collectors.groupingBy( r -> r.getEntry( ).getIdEntry( ) ) );

        for ( Map.Entry<Integer, List<Response>> mapEntry : currentResponsesByIdEntry.entrySet( ) )
        {

            Entry entry = EntryHome.findByPrimaryKey( mapEntry.getKey( ) );
            _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), locale, ticket );
            List<Response> newResponsesForEntry = ticket.getListResponse( ).stream( ).filter( r -> ( r.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) ) && ( r.getIdResponse( ) == 0 ) )
                    .collect( Collectors.toList( ) );

            // Create new responses
            newResponsesForEntry.forEach( response ->
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
            } );

            // Delete old responses
            mapEntry.getValue( ).forEach( response ->
            {
                TicketPj pj = TicketPjHome.findIdPjFromIdResponse( response.getIdResponse( ) );
                if ( ( null != pj ) && ( pj.getStockageTicketing( ) != 0 ) )
                {
                    deletePj( pj );
                }
                TicketHome.removeTicketResponse( ticket.getId( ), response.getIdResponse( ) );
            } );

            strTaskInformation += I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_ATTACHMENT, locale );
        }
        TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        _ticketFormService.removeTicketFromSession( request.getSession( ) );

        // Populate the Ticket
        Ticket ticketWithNewData = new Ticket( );
        ticketWithNewData.setTicketCategory( new TicketCategory( ) ); // -- to not generate validation error on this field
        BeanUtil.populate( ticketWithNewData, request );

        int idArrondissement = Integer.parseInt( request.getParameter( "id_arrondissement" ) );
        Arrondissement arr = ArrondissementHome.findByPrimaryKey( idArrondissement );

        ticketWithNewData.setArrondissement( arr );

        // Update the ticket adress
        TicketAddress ticketAdressToValidate = new TicketAddress( );
        BeanUtil.populate( ticketAdressToValidate, request );


        if ( ticket != null )
        {
            // Update the user title
            int nNewUserTitleId = ticketWithNewData.getIdUserTitle( );
            if ( nNewUserTitleId != ticket.getIdUserTitle( ) )
            {
                String strCurrentUserTitle = ticket.getUserTitle( );
                String strNewUserTitle = UserTitleHome.findByPrimaryKey( nNewUserTitleId ).getLabel( );
                ticket.setIdUserTitle( nNewUserTitleId );
                ticket.setUserTitle( strNewUserTitle );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_USER_TITLE_INFORMATION, strCurrentUserTitle, strNewUserTitle, locale );
            }

            // Update the lastname
            String strNewLatsname = ticketWithNewData.getLastname( );
            String strCurrentLastname = ticket.getLastname( );
            if ( !strCurrentLastname.equals( strNewLatsname ) )
            {
                ticket.setLastname( strNewLatsname );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_LASTNAME_INFORMATION, strCurrentLastname, strNewLatsname, locale );
            }

            // Update the firstname
            String strNewFirstname = ticketWithNewData.getFirstname( );
            String strCurrentFirstname = ticket.getFirstname( );
            if ( !strCurrentFirstname.equals( strNewFirstname ) )
            {
                ticket.setFirstname( strNewFirstname );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_FIRSTNAME_INFORMATION, strCurrentFirstname, strNewFirstname, locale );
            }

            // Update the email
            String strNewEmail = ticketWithNewData.getEmail( );
            String strCurrentEmail = ticket.getEmail( );
            if ( ( strNewEmail != null ) && !strNewEmail.equals( strCurrentEmail ) )
            {
                ticket.setEmail( strNewEmail );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_EMAIL_INFORMATION, strCurrentEmail, strNewEmail, locale );
            }

            // Update the fixed phone number
            String strNewFixedPhoneNumer = ticketWithNewData.getFixedPhoneNumber( );
            String strCurrentFixedPhoneNumber = ticket.getFixedPhoneNumber( );
            if ( ( strNewFixedPhoneNumer != null ) && !strNewFixedPhoneNumer.equals( strCurrentFixedPhoneNumber ) )
            {
                ticket.setFixedPhoneNumber( strNewFixedPhoneNumer );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_FIXED_PHONE_NUMBER_INFORMATION, strCurrentFixedPhoneNumber, strNewFixedPhoneNumer, locale );
            }

            // Update the mobile phone number
            String strNewMobilePhoneNumber = ticketWithNewData.getMobilePhoneNumber( );
            String strCurrentMobilePhoneNumber = ticket.getMobilePhoneNumber( );
            if ( ( strNewMobilePhoneNumber != null ) && !strNewMobilePhoneNumber.equals( strCurrentMobilePhoneNumber ) )
            {
                ticket.setMobilePhoneNumber( strNewMobilePhoneNumber );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_MOBILE_PHONE_NUMBER_INFORMATION, strCurrentMobilePhoneNumber, strNewMobilePhoneNumber, locale );
            }

            // Update the Address
            String strNewAdress = ticketAdressToValidate.getAddress( );
            String strNewAdressDetail = ticketAdressToValidate.getAddressDetail( );
            String strNewPostalCode = ticketAdressToValidate.getPostalCode( );
            String strNewCity = ticketAdressToValidate.getCity( );
            int idQuartier = Integer.parseInt( request.getParameter( "id_quartier" ) );
            Quartier newQuartier = QuartierHome.getQuartierByCode( String.valueOf( idQuartier ) );
            TicketAddress currentTicketAddress = ticket.getTicketAddress( );

            if ( currentTicketAddress != null )
            {
                // Update the address
                if ( ( null != currentTicketAddress.getAddress( ) ) && !currentTicketAddress.getAddress( ).isEmpty( ) && !currentTicketAddress.getAddress( ).equals( strNewAdress ) )
                {
                    String strCurrentAddress = currentTicketAddress.getAddress( );
                    currentTicketAddress.setAddress( strNewAdress );
                    strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_ADDRESS_INFORMATION, strCurrentAddress, strNewAdress, locale );
                }
                // Update the address detail
                if ( ( null != currentTicketAddress.getAddressDetail( ) ) && !currentTicketAddress.getAddressDetail( ).isEmpty( )
                        && !currentTicketAddress.getAddressDetail( ).equals( strNewAdressDetail ) )
                {
                    String strCurrentAddressDetail = currentTicketAddress.getAddressDetail( );
                    currentTicketAddress.setAddressDetail( strNewAdressDetail );
                    strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_ADDRESS_DETAIL_INFORMATION, strCurrentAddressDetail, strNewAdressDetail, locale );
                }
                // Update the city
                if ( ( null != currentTicketAddress.getCity( ) ) && !currentTicketAddress.getCity( ).isEmpty( ) && !currentTicketAddress.getCity( ).equals( strNewCity ) )
                {
                    String strCurrentCity = currentTicketAddress.getCity( );
                    currentTicketAddress.setCity( strNewCity );
                    strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_CITY_INFORMATION, strCurrentCity, strNewCity, locale );
                }
                // Update the postal code
                if ( ( null != currentTicketAddress.getPostalCode( ) ) && !currentTicketAddress.getPostalCode( ).isEmpty( ) && !currentTicketAddress.getPostalCode( ).equals( strNewPostalCode ) )
                {
                    String strCurrentPostalCode = currentTicketAddress.getPostalCode( );
                    currentTicketAddress.setPostalCode( strNewPostalCode );
                    strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_POSTAL_CODE_INFORMATION, strCurrentPostalCode, strNewPostalCode, locale );
                }
                // Update the quartier
                if ( ( null != newQuartier ) && ( newQuartier.getId( ) != 0 ) )
                {
                    String strCurrentQuartier = currentTicketAddress.getQuartier( ).getLabel( );
                    currentTicketAddress.setQuartier( newQuartier );
                    strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_QUARTIER_INFORMATION, strCurrentQuartier, newQuartier.getLabel( ), locale );
                }
                ticket.setTicketAddress( currentTicketAddress );
            } else
            {
                if ( StringUtils.isNotBlank( strNewAdress ) || StringUtils.isNotBlank( strNewAdressDetail ) || StringUtils.isNotBlank( strNewPostalCode ) || StringUtils.isNotBlank( strNewCity ) )
                {
                    TicketAddress newTicketAddress = new TicketAddress( );

                    // Update the address
                    newTicketAddress.setAddress( strNewAdress );
                    if ( StringUtils.isNotBlank( strNewAdress ) )
                    {
                        strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_ADDRESS_INFORMATION, StringUtils.EMPTY, strNewAdress, locale );
                    }

                    // Update the address detail
                    newTicketAddress.setAddressDetail( strNewAdressDetail );
                    if ( StringUtils.isNotBlank( strNewAdressDetail ) )
                    {
                        strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_ADDRESS_DETAIL_INFORMATION, StringUtils.EMPTY, strNewAdressDetail, locale );
                    }

                    // Update the city
                    newTicketAddress.setCity( strNewCity );
                    if ( StringUtils.isNotBlank( strNewCity ) )
                    {
                        strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_CITY_INFORMATION, StringUtils.EMPTY, strNewCity, locale );
                    }

                    // Update the postal code
                    newTicketAddress.setPostalCode( strNewPostalCode );
                    if ( StringUtils.isNotBlank( strNewPostalCode ) )
                    {
                        strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_POSTAL_CODE_INFORMATION, StringUtils.EMPTY, strNewPostalCode, locale );
                    }

                    ticket.setTicketAddress( newTicketAddress );
                }
            }

            // Update the arrondissement
            Arrondissement newArrondissement = ticketWithNewData.getArrondissement( );
            Arrondissement currentArrondissement = ticket.getArrondissement( );
            if ( ( newArrondissement != null ) && ( newArrondissement.getId( ) != currentArrondissement.getId( ) ) )
            {
                ticket.setArrondissement( newArrondissement );
                String strNewArrondissement = newArrondissement.getId( ) < 5 ? "Paris Centre" : String.valueOf( newArrondissement.getId( ) );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_ARRONDISSEMNT_INFORMATION, String.valueOf( currentArrondissement.getId( ) ), strNewArrondissement, locale );
            }

            // Update the contact mode
            int nNewContactModeId = ticketWithNewData.getIdContactMode( );
            if ( ticket.getIdContactMode( ) != nNewContactModeId )
            {
                String strCurrentContactMode = ticket.getContactMode( );
                String strNewContactMode = ContactModeHome.findByPrimaryKey( nNewContactModeId ).getCode( );
                ticket.setIdContactMode( nNewContactModeId );
                ticket.setContactMode( strNewContactMode );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_CONTACT_MODE_INFORMATION, strCurrentContactMode, strNewContactMode, locale );
            }

            // Update the comment
            String strNewComment = ticketWithNewData.getTicketComment( );
            String strCurrentTicketComment = ticket.getTicketComment( );
            if ( ( strNewComment != null ) && !strNewComment.equals( strCurrentTicketComment ) )
            {
                ticket.setTicketComment( strNewComment );
                strTaskInformation += formatInfoMessage( MESSAGE_MODIFY_TICKET_COMMENT_INFORMATION, strCurrentTicketComment, strNewComment, locale );
            }

            if ( ( ticket.getIdAdminBOInit( ) < 1 ) && ( request != null ) )
            {
                AdminUser user = AdminUserService.getAdminUser( request );
                ticket.setIdAdminBOInit( user.getUserId( ) );
            }

            // Update the ticket
            TicketHome.update( ticket );
        }

        // In the case when there are no modifications
        if ( strTaskInformation.equals( StringUtils.EMPTY ) )
        {
            strTaskInformation = I18nService.getLocalizedString( MESSAGE_MODIFY_TICKET_NO_MODIFICATIONS_INFORMATION, locale );
        }

        return strTaskInformation;
    }

    /**
     * Return the message formated for the modification of informations of the ticket
     *
     * @param strKey
     *            : the key of the message
     * @param strOldValue
     *            : the value which has been replaced
     * @param strNewValue
     *            : the new value
     * @param locale
     * @return the message formated
     */
    private String formatInfoMessage( String strKey, String strOldValue, String strNewValue, Locale locale )
    {
        return MessageFormat.format( I18nService.getLocalizedString( strKey, locale ), evaluateValue( strOldValue, locale ), evaluateValue( strNewValue, locale ) );
    }

    /**
     * Return the value if it's not empty otherwise return a default message
     *
     * @param strValue
     *            : the value to evaluate
     * @param locale
     * @return the value or the default message
     */
    private String evaluateValue( String strValue, Locale locale )
    {
        return ( StringUtils.isBlank( strValue ) ? I18nService.getLocalizedString( NOT_FILLED_INFORMATION, locale ) : strValue );
    }

    private void deletePj( TicketPj pj )
    {
        String profil = STroisService.findTheProfilAndServerS3( pj.getStockageTicketing( ), SERVEUR_SIDE );
        StockageService stockageService = new StockageService( profil );
        stockageService.deleteFileOnS3Serveur( pj.getUrlTicketing( ) );
    }
}
