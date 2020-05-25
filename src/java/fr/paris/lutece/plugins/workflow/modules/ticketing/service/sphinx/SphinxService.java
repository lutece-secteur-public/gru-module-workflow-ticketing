package fr.paris.lutece.plugins.workflow.modules.ticketing.service.sphinx;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.web.rs.SphinxRest;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class SphinxService
{

    private static final String API_URL              = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.url" );

    private static final String TOKEN_URL            = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.token_url" );
    private static final String USERNAME             = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.username" );
    private static final String PASSWORD             = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.password" );
    private static final String SURVEY               = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.survey" );

    private static final String ACCESS_TOKEN         = "access_token";

    private static final String COLUMN_EMAIL         = "email";
    private static final String COLUMN_CREATION_DATE = "Date_de_creation";
    private static final String COLUMN_CATEGORY_1    = "domaine";
    private static final String COLUMN_CATEGORY_2    = "thematique";
    private static final String COLUMN_CATEGORY_3    = "sous_thematique";
    private static final String COLUMN_CATEGORY_4    = "localisation";
    private static final String COLUMN_CHANNEL       = "Canal";
    private static final String COLUMN_ASSIGN_ENTITY = "Entite_d_assignation";
    private static final String COLUMN_CLOSE_DATE    = "Date_de_cloture";
    private static final String COLUMN_DAYS_OPENED   = "delai_en_jours";


    public void post( String endpoint, String json ) throws HttpAccessException
    {
        HttpAccess httpAccess = new HttpAccess( );
        Map<String, String> headersRequest = new HashMap<String, String>( );
        headersRequest.put( "Authorization", "bearer " + SphinxRest.getTokenAccess( ) );
        httpAccess.doPostJSON( API_URL + endpoint, json, headersRequest, null );
    }

    public void postTicketData( Ticket ticket ) throws HttpAccessException
    {
        JsonObject ticketJson = new JsonObject( );

        ticketJson.addProperty( COLUMN_EMAIL, ticket.getEmail( ) );

        String creationDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateCreate( ) );
        ticketJson.addProperty( COLUMN_CREATION_DATE, creationDate );

        if ( ticket.getCategoryDepth( 0 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_1, ticket.getCategoryDepth( 0 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 1 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_2, ticket.getCategoryDepth( 1 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 2 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_3, ticket.getCategoryDepth( 2 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 3 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_4, ticket.getCategoryDepth( 3 ).getLabel( ) );
        }
        if ( ticket.getChannel( ) != null )
        {
            ticketJson.addProperty( COLUMN_CHANNEL, ticket.getChannel( ).getLabel( ) );
        }
        if ( ticket.getAssigneeUnit( ) != null )
        {
            ticketJson.addProperty( COLUMN_ASSIGN_ENTITY, ticket.getAssigneeUnit( ).getName( ) );
        }

        if ( ticket.getDateClose( ) != null )
        {
            String closeDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateClose( ) );
            ticketJson.addProperty( COLUMN_CLOSE_DATE, closeDate );

            ticketJson.addProperty( COLUMN_DAYS_OPENED, ( int ) ( ticket.getDateClose( ).getTime( ) - ticket.getDateCreate( ).getTime( ) ) / ( 24 * 60 * 60 * 1000 ) );
        }

        JsonArray ticketsJson = new JsonArray( );
        ticketsJson.add( ticketJson );

        post( "/api/v4.0/survey/" + SURVEY + "/data", ticketsJson.toString( ) );
    }

}
