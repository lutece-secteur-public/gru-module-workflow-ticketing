package fr.paris.lutece.plugins.workflow.modules.ticketing.service.sphinx;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonObject;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SphinxService
{
    public static final String BEAN_NAME = "workflow-ticketing.sphinxService";

    private static final String API_URL = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.url" );

    private static final String TOKEN_URL = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.token_url" );
    private static final String USERNAME = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.username" );
    private static final String PASSWORD = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.password" );

    private OkHttpClient _client = new OkHttpClient( );
    public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );
    public static final MediaType TEXT = MediaType.parse( "text/plain; charset=utf-8" );

    public String accessToken( ) throws IOException
    {
        String authParams = "username=" + USERNAME + "&password=" + PASSWORD + "&lang=fr&grant_type=password&client_id=sphinxapiclient";

        RequestBody body = RequestBody.create( TEXT, authParams );
        Request request = new Request.Builder( ).url( TOKEN_URL ).post( body ).build( );
        Response response = _client.newCall(request).execute();
        return response.body( ).string( );
    }

    public String post( String endpoint, String json ) throws IOException
    {
        RequestBody body = RequestBody.create( JSON, json );
        Request request = new Request.Builder( ).url( API_URL + endpoint ).addHeader( "Authorization", "bearer " + accessToken( ) ).post( body ).build( );
        Response response = _client.newCall( request ).execute( );
        return response.body( ).string( );
    }

    public void postTicketData( Ticket ticket ) throws IOException
    {
        JsonObject ticketJson = new JsonObject( );

        ticketJson.addProperty( "email", ticket.getEmail( ) );

        String creationDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateCreate( ) );
        ticketJson.addProperty( "Date_de_creation", creationDate );

        if ( ticket.getCategoryDepth( 0 ) != null )
        {
            ticketJson.addProperty( "domaine", ticket.getCategoryDepth( 0 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 1 ) != null )
        {
            ticketJson.addProperty( "thematique", ticket.getCategoryDepth( 1 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 2 ) != null )
        {
            ticketJson.addProperty( "sous_thematique", ticket.getCategoryDepth( 2 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 3 ) != null )
        {
            ticketJson.addProperty( "localisation", ticket.getCategoryDepth( 3 ).getLabel( ) );
        }
        if ( ticket.getChannel( ) != null )
        {
            ticketJson.addProperty( "Canal", ticket.getChannel( ).getLabel( ) );
        }
        if ( ticket.getAssigneeUnit( ) != null )
        {
            ticketJson.addProperty( "Entite_d_assignation", ticket.getAssigneeUnit( ).getName( ) );
        }

        if ( ticket.getDateClose( ) != null )
        {
            String closeDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateClose( ) );
            ticketJson.addProperty( "Date_de_cloture", closeDate );

            ticketJson.addProperty( "delai_en_jours", ( ticket.getDateClose( ).getTime( ) - ticket.getDateCreate( ).getTime( ) ) / ( 60 * 60 * 1000 ) );
        }

        post( "/api/survey/Reservoir_des_donnees_GRU/data", ticketJson.toString( ) );
    }


}
