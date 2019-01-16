package fr.paris.lutece.plugins.workflow.modules.ticketing.service.sphinx;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

public class SphinxService
{
    public static final String BEAN_NAME = "workflow-ticketing.sphinxService";

    private static final String API_URL = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.url" );

    private static final String TOKEN_URL = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.token_url" );
    private static final String USERNAME = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.username" );
    private static final String PASSWORD = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.password" );
    private static final String SURVEY = AppPropertiesService.getProperty( "workflow-ticketing.workflow.sphinx.survey" );

    private static OkHttpClient _client;
    public static final MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );
    public static final MediaType FORM = MediaType.parse( "application/x-www-form-urlencoded; charset=utf-8" );

    private static final String ACCESS_TOKEN = "access_token";

    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_CREATION_DATE = "Date_de_creation";
    private static final String COLUMN_CATEGORY_1 = "domaine";
    private static final String COLUMN_CATEGORY_2 = "thematique";
    private static final String COLUMN_CATEGORY_3 = "sous_thematique";
    private static final String COLUMN_CATEGORY_4 = "localisation";
    private static final String COLUMN_CHANNEL = "Canal";
    private static final String COLUMN_ASSIGN_ENTITY = "Entite_d_assignation";
    private static final String COLUMN_CLOSE_DATE = "Date_de_cloture";
    private static final String COLUMN_DAYS_OPENED = "delai_en_jours";

    private static boolean USE_SSL = true;

    public static OkHttpClient getHttpClient( )
    {
        if ( _client == null )
        {
            if ( USE_SSL )
            {
                ConnectionSpec spec = new ConnectionSpec.Builder( ConnectionSpec.MODERN_TLS )
                        .tlsVersions( TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2 )
                        .cipherSuites( CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                                CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256,
                                CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,
                                CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_DHE_DSS_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA,
                                CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA,
                                CipherSuite.TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_EMPTY_RENEGOTIATION_INFO_SCSV ).build( );

                _client = new OkHttpClient.Builder( ).connectionSpecs( Collections.singletonList( spec ) ).build( );
            }
            else
            {
                _client = new OkHttpClient( );
            }
        }

        return _client;
    }

    public String accessToken( ) throws IOException
    {
        String authParams = "username=" + USERNAME + "&password=" + PASSWORD + "&lang=fr&grant_type=password&client_id=sphinxapiclient";

        RequestBody body = RequestBody.create( FORM, authParams );
        Request request = new Request.Builder( ).url( TOKEN_URL ).post( body ).build( );
        Response response = getHttpClient( ).newCall( request ).execute( );

        String rawData = response.body( ).string( );
        JsonObject dataJson = new JsonParser( ).parse( rawData ).getAsJsonObject( );
        response.body( ).close( );
        return dataJson.get( ACCESS_TOKEN ).getAsString( );
    }

    public String post( String endpoint, String json ) throws IOException
    {
        RequestBody body = RequestBody.create( JSON, json );
        Request request = new Request.Builder( ).url( API_URL + endpoint ).addHeader( "Authorization", "bearer " + accessToken( ) ).post( body ).build( );
        Response response = getHttpClient( ).newCall( request ).execute( );
        String bodyResponse = response.body( ).string( );
        response.body( ).close( );
        return bodyResponse;
    }

    public void postTicketData( Ticket ticket ) throws IOException
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

            ticketJson.addProperty( COLUMN_DAYS_OPENED, (int) ( ticket.getDateClose( ).getTime( ) - ticket.getDateCreate( ).getTime( ) )
                    / ( 24 * 60 * 60 * 1000 ) );
        }

        JsonArray ticketsJson = new JsonArray( );
        ticketsJson.add( ticketJson );

        post( "/api/survey/" + SURVEY + "/data", ticketsJson.toString( ) );
    }

}
