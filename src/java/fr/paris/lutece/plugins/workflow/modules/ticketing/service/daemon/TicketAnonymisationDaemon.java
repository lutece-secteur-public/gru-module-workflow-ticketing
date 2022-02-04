package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.message.ITicketEmailExternalUserMessageDAO;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class TicketAnonymisationDaemon extends Daemon
{

    private static final int DELAI_ANONYMISATION = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ANONYMISATION_DELAI, 10 );
    private static final int TICKET_STATUS_ARCHIVE = AppPropertiesService.getPropertyInt( "ticketing.daemon.anonymisation.state.id.archive", 308 );

    private static ITicketEmailExternalUserMessageDAO _dao = SpringContextService.getBean( ITicketEmailExternalUserMessageDAO.BEAN_SERVICE );
    private static Plugin _plugin = WorkflowTicketingPlugin.getPlugin( );

    /*
     * Constructor
     */
    public TicketAnonymisationDaemon( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        StringJoiner sb = new StringJoiner( "\n\r" );

        sb.add( "Début de l'anomymisation" );
        anonymisation( sb );
        sb.add( "Fin de l'anomymisation" );
        setLastRunLogs( sb.toString( ) );

    }

    private void anonymisation( StringJoiner sb )
    {
        String date = dateForAnonymisation( );
        List<Ticket> listTickets = TicketHome.getForAnonymisation( date, TICKET_STATUS_ARCHIVE );

        for( Ticket ticket : listTickets )
        {
            // suppression des données sensibles dans l'historique
            anonymizeTicketHistoryData( ticket );
            // anonymisation du ticket

            String newComment = "";
            newComment = StringUtils.replace( ticket.getTicketComment( ), ticket.getFirstname( ), "" );
            newComment = StringUtils.replace( newComment, ticket.getLastname( ), "" );
            newComment = StringUtils.replace( newComment, ticket.getEmail( ), "" );
            newComment = StringUtils.replace( newComment, ticket.getFixedPhoneNumber( ), "" );
            newComment = StringUtils.replace( newComment, ticket.getMobilePhoneNumber( ), "" );
            ticket.setTicketComment( newComment );

            ticket.setFirstname( ticket.getReference( ) );

            ticket.setLastname( ticket.getReference( ) );
            ticket.setIdUserTitle( 0 );
            ticket.setEmail( ticket.getReference( ) + "@yopmail.com" );
            ticket.setFixedPhoneNumber( null );
            ticket.setMobilePhoneNumber( null );
            ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
            TicketHome.update( ticket );
            sb.add( "Id Ticket anonymisé: " + ticket.getId( ) );
        }
    }

    private String dateForAnonymisation( )
    {
        Date date = new Date( );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        Calendar calDate = Calendar.getInstance( );
        calDate.setTime( date );
        calDate.add( Calendar.DAY_OF_YEAR, -DELAI_ANONYMISATION );
        date = calDate.getTime( );
        return sdf.format( date );
    }

    public static void anonymizeTicketHistoryData( Ticket ticket)
    {
        List<Integer> listEmailExternalUser = _dao.getListIDMessageExternalUser( ticket.getId( ), _plugin );
        for( int idEmailExternalUser : listEmailExternalUser )
        {
            Map<String, String> data = _dao.getHistoryEmailToAnonymize( idEmailExternalUser, _plugin );
            for( Entry<String, String> entry : data.entrySet( ) )
            {
                String newValue = "";
                newValue = StringUtils.replace( entry.getValue( ), ticket.getFirstname( ), "" );
                newValue = StringUtils.replace( newValue, ticket.getLastname( ), "" );
                newValue = StringUtils.replace( newValue, ticket.getEmail( ), "" );
                newValue = StringUtils.replace( newValue, ticket.getFixedPhoneNumber( ), "" );
                newValue = StringUtils.replace( newValue, ticket.getMobilePhoneNumber( ), "" );
                entry.setValue( newValue );
            }
            _dao.update( data, idEmailExternalUser, _plugin );
        }

    }

}
