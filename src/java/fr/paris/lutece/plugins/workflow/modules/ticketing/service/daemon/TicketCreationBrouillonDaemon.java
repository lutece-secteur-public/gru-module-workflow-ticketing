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
package fr.paris.lutece.plugins.workflow.modules.ticketing.service.daemon;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.erreurscannerstrois.ErreurScannerStrois;
import fr.paris.lutece.plugins.ticketing.business.erreurscannerstrois.ErreurScannerStroisHome;
import fr.paris.lutece.plugins.ticketing.business.profilstrois.Profilstrois;
import fr.paris.lutece.plugins.ticketing.business.quartier.Quartier;
import fr.paris.lutece.plugins.ticketing.business.quartier.QuartierHome;
import fr.paris.lutece.plugins.ticketing.business.referentielscanner.ReferentielScanner;
import fr.paris.lutece.plugins.ticketing.business.referentielscanner.ReferentielScannerHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPj;
import fr.paris.lutece.plugins.ticketing.business.ticketpj.TicketPjHome;
import fr.paris.lutece.plugins.ticketing.service.TicketInitService;
import fr.paris.lutece.plugins.ticketing.service.TicketTransfertPjService;
import fr.paris.lutece.plugins.ticketing.service.strois.StockageService;
import fr.paris.lutece.plugins.ticketing.service.util.FileUtils;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.plugins.workflow.modules.ticketing.service.WorkflowTicketingPlugin;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;

/**
 * Daemon used to Pj Migration Tickets
 */
public class TicketCreationBrouillonDaemon extends Daemon
{

    // Services
    private static WorkflowService _workflowService                        = WorkflowService.getInstance( );

    private static final String    PROPERTY_CHANNEL_SCAN_NAME = "ticketing.channelScan.name";
    private static final String    PROPERTY_ID_ADMIN_USER_FOR_DRAFT_DAEMON = "ticketing.draft.daemon.admin.user.id";
    private static final String    MENTION_A_PRECISER         = "A préciser";

    private static final String    DAEMON_INSERTION_ERROR_MAIL_SUBJECT     = "module.workflow.ticketing.daemon.creationBrouillonDaemon.error.mail.insertion.subject";
    private static final String    DAEMON_INSERTION_ERROR_MAIL_BODY        = "module.workflow.ticketing.daemon.creationBrouillonDaemon.error.mail.insertion.body";
    private static final String    DAEMON_SUPPRESSION_ERROR_MAIL_SUBJECT   = "module.workflow.ticketing.daemon.creationBrouillonDaemon.error.mail.suppression.subject";
    private static final String    DAEMON_SUPPRESSION_ERROR_MAIL_BODY      = "module.workflow.ticketing.daemon.creationBrouillonDaemon.error.mail.suppression.body";


    private static Plugin          _plugin                                 = WorkflowTicketingPlugin.getPlugin( );

    private final StockageService  _stockageS3DaemonMinio                  = new StockageService( Profilstrois.PROFIL_MINIO_DAEMON_NAME );
    private final StockageService  _stockageS3ScannerDaemonMinio           = new StockageService( Profilstrois.PROFIL_MINIO_COURRIER_SCANNER_DAEMON_NAME );

    /**
     * Statut "Supprimé"
     */
    private int                    _nIdStateDeleted                        = AppPropertiesService.getPropertyInt( "workflow.ticketing.state.id.deleted", TicketingConstants.PROPERTY_UNSET_INT );

    private String                 _strchannelScanName        = AppPropertiesService.getProperty( PROPERTY_CHANNEL_SCAN_NAME );
    private String                 _strAdminUserId                         = AppPropertiesService.getProperty( PROPERTY_ID_ADMIN_USER_FOR_DRAFT_DAEMON );
    private static final int       MAX_FILES_BY_DOSSIERS3                  = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_MAX_FILES_BY_DOSSIERS3_FOR_DRAFT_CREATION, 10 );

    // Errors
    private static final String    ERROR_RESOURCE_NOT_FOUND                = "Resource not found";
    private static Locale          local                                   = I18nService.getDefaultLocale( );

    /**
     * Constructor
     */
    public TicketCreationBrouillonDaemon( )
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

        sb.add( "Début de la migration" );
        purgeDeletedTicketOrDraft( sb );
        createBrouillon( sb );
        sb.add( "Fin de la migration" );
        setLastRunLogs( sb.toString( ) );
    }

    /**
     * Create a ticket draft if file exist from a postal mail
     *
     * @param sb
     *            the logs
     */
    private void createBrouillon( StringJoiner sb )
    {

        List<ReferentielScanner> referentielScannerList = ReferentielScannerHome.getReferentielScannersList( );

        Iterable<Result<Item>> contenuList = _stockageS3ScannerDaemonMinio.findAllFileOrFolder( _stockageS3ScannerDaemonMinio );
        Iterator<Result<Item>> iteration = contenuList.iterator( );
        List<ErreurScannerStrois> erreurList = ErreurScannerStroisHome.getErreurScannerStroisList( );
        List<String> erreurPathsList = erreurList.stream( ).map( e -> e.getPath( ) ).collect( Collectors.toList( ) );

        List<String> erreurPathsInsertion = erreurList.stream( ).filter( e -> e.getPbInsertion( ) ).map( e -> e.getPath( ) ).collect( Collectors.toList( ) );
        List<String> erreurPathsSuppression = erreurList.stream( ).filter( e -> !e.getPbInsertion( ) ).map( e -> e.getPath( ) ).collect( Collectors.toList( ) );

        while ( iteration.hasNext( ) )
        {
            Item i;
            try
            {
                i = iteration.next( ).get( );
                for ( ReferentielScanner referentielScanner : referentielScannerList )
                {
                    Iterable<Result<Item>> filesForDossierS3List = null;
                    String iFolderRoot = i.objectName( ).split( "/" )[0];
                    // Appel récupération des fichiers du dossierS3 avec prefix du dossier
                    if ( iFolderRoot.equals( referentielScanner.getDossierStrois( ) ) )
                    {
                        filesForDossierS3List = _stockageS3ScannerDaemonMinio.findAllFileInPrefix( _stockageS3ScannerDaemonMinio, referentielScanner.getDossierStrois( ) );
                    }
                    if ( null != filesForDossierS3List )
                    {
                        int n = 1;
                        String filepath = "";
                        for ( Result<Item> result : filesForDossierS3List )
                        {
                            List<Integer> idFileList = new ArrayList<>( );
                            if ( n < MAX_FILES_BY_DOSSIERS3 )
                            {
                                boolean insertionSuccess = false;
                                boolean suppressionSuccess = false;

                                int idCoreFile = 0;
                                boolean fileAllowed = false;
                                try
                                {
                                    TransactionManager.beginTransaction( _plugin );

                                    filepath = result.get( ).objectName( );
                                    ZonedDateTime zoneDateModified = result.get( ).lastModified( );
                                    Date dateModified = java.util.Date.from( zoneDateModified.toInstant( ) );
                                    java.sql.Timestamp timestampDate = new java.sql.Timestamp( dateModified.getTime( ) );
                                    String fileName = StringUtils.substringAfterLast( filepath, "/" );
                                    String extension = StringUtils.substringAfterLast( fileName, "." );

                                    boolean isAnOldErreurPath = erreurPathsList.contains( filepath );

                                    if ( !isAnOldErreurPath )
                                    {
                                        if ( extension.equals( "exe" ) )
                                        {
                                            addScannerS3Erreur( filepath, true );
                                        } else
                                        {
                                            idCoreFile = TicketPjHome.insertPjFromScannerAndGetIdCoreFile( fileName, timestampDate );
                                            idFileList.add( idCoreFile );
                                            fileAllowed = true;
                                        }
                                    }
                                } catch ( MinioException e )
                                {
                                    TransactionManager.rollBack( _plugin );
                                    sb.add( "rollback core" );
                                    e.printStackTrace( );
                                    idFileList.remove( Integer.valueOf( idCoreFile ) );
                                    addScannerS3Erreur( filepath, true );
                                }
                                TransactionManager.commitTransaction( _plugin );

                                if ( fileAllowed )
                                {
                                    Ticket ticket = createDraftDefault( sb, referentielScanner.getIdCategory( ) );

                                    if ( ( null != ticket ) )
                                    {
                                        int idResponseCreated = 0;
                                        try
                                        {
                                            TransactionManager.beginTransaction( _plugin );

                                            int idTicketPj = insertTicketPjScanner( idFileList, ticket, true );

                                            byte[] file = _stockageS3ScannerDaemonMinio.loadFileFromS3Serveur( filepath );
                                            String technicalName = createTechnicalFileName( idFileList.get( 0 ), ticket.getId( ) );

                                            String destination = FileUtils.cheminDepotFichierUsager( TicketingConstants.CODE_APPLI );

                                            String fileSolenS3Path = _stockageS3DaemonMinio.saveFileToS3Server( file,
                                                    destination + technicalName );

                                            insertionSuccess = _stockageS3DaemonMinio.isObjectExistWithStockageService( _stockageS3DaemonMinio, fileSolenS3Path );

                                            if ( !insertionSuccess )
                                            {
                                                TicketHome.deleteCoreFile( idFileList );
                                                ResponseHome.remove( idResponseCreated );
                                                TicketHome.remove( ticket.getId( ) );
                                                addScannerS3Erreur( filepath, true );
                                                sendMail( filepath, _stockageS3ScannerDaemonMinio, destination, _stockageS3DaemonMinio, true );
                                                sb.add( "ajout table erreur suppression" );
                                            } else
                                            {
                                                idResponseCreated = TicketPjHome.insertResponseAndGetIdCoreFile( idCoreFile );

                                                TicketHome.insertTicketResponse( ticket.getId( ), idResponseCreated );

                                                if ( idTicketPj != 0 )
                                                {
                                                    TicketPj pj = TicketPjHome.findByPrimaryKey( idTicketPj );
                                                    pj.setTechnicalName( technicalName );
                                                    pj.setUrlTicketing( fileSolenS3Path );
                                                    pj.setStockageTicketing( 1 );
                                                    pj.setIdResponse( idResponseCreated );
                                                    TicketPjHome.update( pj );
                                                }
                                            }
                                        } catch ( Exception e )
                                        {
                                            TransactionManager.rollBack( _plugin );
                                            sb.add( "rollback file" );
                                            e.printStackTrace( );
                                            TicketHome.deleteCoreFile( idFileList );
                                            ResponseHome.remove( idResponseCreated );
                                            TicketHome.remove( ticket.getId( ) );
                                            addScannerS3Erreur( filepath, true );
                                        }
                                        TransactionManager.commitTransaction( _plugin );

                                        try
                                        {
                                            TransactionManager.beginTransaction( _plugin );
                                            suppressionSuccess = _stockageS3ScannerDaemonMinio.deleteFileOnS3Serveur( filepath );
                                            if ( !suppressionSuccess )
                                            {
                                                addScannerS3Erreur( filepath, false );
                                                sb.add( "ajout table erreur suppression" );
                                            }
                                        } catch ( Exception e )
                                        {
                                            TransactionManager.rollBack( _plugin );
                                            sb.add( "rollback supp" );
                                            e.printStackTrace( );
                                        }
                                        TransactionManager.commitTransaction( _plugin );
                                    }
                                }

                            }
                            n++;
                        }

                    }
                }

                System.out.println( "Object : " + i.objectName( ) );
            } catch ( InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException | InvalidResponseException | NoSuchAlgorithmException
                    | ServerException | XmlParserException | IOException e )
            {
                e.printStackTrace( );
            }

        }


    }

    private void sendMail( String filepath, StockageService _stockageS3ScannerDaemonMinio, String destination, StockageService _stockageS3DaemonMinio, boolean isInsertionProblem )
    {
        String message = "";
        String subject = "";
        /*
         * if ( isInsertionProblem ) { subject = MessageFormat.format( I18nService.getLocalizedString( DAEMON_INSERTION_ERROR_MAIL_SUBJECT, Locale.FRENCH ), AppPropertiesService.getProperty( "lutece.prod.url" ) ); message = MessageFormat.format( I18nService.getLocalizedString( DAEMON_INSERTION_ERROR_MAIL_BODY, Locale.FRENCH ), AppPropertiesService.getProperty( "strois.url.minio.scanner" ), AppPropertiesService.getProperty( "strois.url.minio.scanner" ), AppPropertiesService.getProperty( "strois.url.minio" ), destination ); } else { subject = I18nService.getLocalizedString( DAEMON_SUPPRESSION_ERROR_MAIL_SUBJECT, local ); message = MessageFormat.format( I18nService.getLocalizedString( DAEMON_SUPPRESSION_ERROR_MAIL_BODY, Locale.FRENCH ), AppPropertiesService.getProperty( "strois.url.minio.scanner" ), filepath ); }
         */

        // MailService.sendMailHtml( to, atname, atemail, subject, message );
    }

    // Purge ticket ou brouillon au statut supprimé
    private void purgeDeletedTicketOrDraft( StringJoiner sb )
    {
        // commencer par récupérer les tickets au status supprimé
        List<Integer> listResourceDeletedId = _workflowService.getResourceIdListByIdState( _nIdStateDeleted, Ticket.TICKET_RESOURCE_TYPE );

        if ( !listResourceDeletedId.isEmpty( ) )
        {
            for ( Integer idTicket : listResourceDeletedId )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( idTicket );

                if ( null != ticket )
                {
                    deleteDraftAndAttchement( ticket, sb );
                }
            }

        }
    }

    private void deleteDraftAndAttchement( Ticket ticket, StringJoiner sb )
    {
        try
        {
            TransactionManager.beginTransaction( _plugin );
            deleteDraftAttachmentTicket( ticket );
            WorkflowCapableJspBean.doRemoveWorkFlowResource( ticket.getId( ) );

            IndexerActionHome.removeByIdTicket( ticket.getId( ) );

            TicketHome.remove( ticket.getId( ) );
            WorkflowCapableJspBean.immediateRemoveTicketFromIndex( ticket.getId( ) );

        } catch ( Exception e )
        {
            TransactionManager.rollBack( _plugin );
            AppLogService.error( e );
        }
        TransactionManager.commitTransaction( _plugin );
        sb.add( "Brouillon supprimé id : " + ticket.getId( ) );
    }

    /**
     * Find attachment for usager
     *
     * @param ticket
     *            the ticket to clean
     * @param usager
     *            boolean true if the attachement is from usager
     *
     */
    private Map<Integer, Integer> findDraftAttachment( Ticket ticket, boolean usager )
    {
        return TicketPjHome.getIdFileToDeleteAndStockage( ticket.getId( ), usager );
    }

    /**
     * Delete attachemant for a ticket core_file and core_physical_fle
     *
     * @param ticket
     *            the ticket to delete
     */
    private void deleteDraftAttachment( Map<Integer, Integer> coreFileAndIdStockage )
    {
        if ( !coreFileAndIdStockage.isEmpty( ) )
        {
            for ( Entry<Integer, Integer> entry : coreFileAndIdStockage.entrySet( ) )
            {
                TicketPj pj = TicketPjHome.findByIdFile( entry.getKey( ) );
                if ( pj.getStockageTicketing( ) == 1 )
                {
                    _stockageS3DaemonMinio.deleteFileOnS3Serveur( pj.getUrlTicketing( ) );
                    FileHome.remove( entry.getKey( ) );
                    TicketPjHome.remove( pj.getId( ) );
                }
            }
        }
    }

    /**
     * Delete Draft attachement
     *
     * @param ticket
     *            the ticket to delete
     */
    private void deleteDraftAttachmentTicket( Ticket ticket )
    {
        Map<Integer, Integer> usagerAttachment = findDraftAttachment( ticket, true );

        deleteDraftAttachment( usagerAttachment );
    }

    /**
     * Create Draft attachement
     *
     * @param sb
     */
    private Ticket createDraftDefault( StringJoiner sb, int idCategory )
    {
        Ticket ticket = new Ticket( );

        try
        {
            TransactionManager.beginTransaction( _plugin );

            TicketInitService ticketInitService = SpringContextService.getBean( TicketInitService.BEAN_NAME );

            TicketCategory category = TicketCategoryHome.findByPrimaryKey( idCategory );
            TicketAddress address = new TicketAddress( );
            Optional<Quartier> optQuartier = QuartierHome.findByPrimaryKey( 1 );
            Quartier quartier = optQuartier.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
            address.setAddress( MENTION_A_PRECISER );
            address.setPostalCode( "00" );
            address.setCity( MENTION_A_PRECISER );
            address.setQuartier( quartier );
            ticket.setTicketCategory( category );
            ticket.setIdUserTitle( 0 );
            ticket.setUserTitle( "" );
            ticket.setFirstname( MENTION_A_PRECISER );
            ticket.setLastname( MENTION_A_PRECISER );
            ticket.setEmail( "" );
            ticket.setTicketComment( "" );
            ticket.setTicketAddress( address );
            ticket.setDateUpdate( new Timestamp( new Date( ).getTime( ) ) );
            ticket.setDateCreate( new Timestamp( new Date( ).getTime( ) ) );
            ticket.setIdContactMode( 2 );
            Channel channel = ChannelHome.findByName( _strchannelScanName );
            ticket.setChannel( channel );

            TicketHome.create( ticket );

            User user = AdminUserHome.findByPrimaryKey( Integer.parseInt( _strAdminUserId ) );

            ticketInitService.doProcessNextWorkflowActionInit( ticket, null, local, user );

            // Immediate indexation of the Ticket
            WorkflowCapableJspBean.immediateTicketIndexing( ticket.getId( ) );

            sb.add( "Brouillon créé id : " + ticket.getId( ) );

            TransactionManager.commitTransaction( _plugin );

        } catch ( Exception e )
        {
            TransactionManager.rollBack( _plugin );
            e.printStackTrace( );
        }
        return ticket;
    }

    /**
     * Insert pj in ticketing_ticket_pj with id file list from scanner and get the id
     *
     * @param idFileList
     *            the id file list
     * @param ticket
     *            the ticket
     * @param isUsagerPj
     *            true if the pj is from usager otherwise false
     * @return the id pj
     */
    private int insertTicketPjScanner( List<Integer> idFileList, Ticket ticket, boolean isUsagerPj )
    {
        int idPj = 0;
        if ( ( null != idFileList ) && !idFileList.isEmpty( ) )
        {
            for ( Integer idFile : idFileList )
            {
                TicketPj pj = new TicketPj( );
                pj.setIdTicket( ticket.getId( ) );
                pj.setIdFile( idFile );
                pj.setUrlTicketing( "" );
                pj.setStockageTicketing( -1 );
                pj.setUsager( isUsagerPj );
                idPj = TicketPjHome.createPjAndGetId( pj );
            }
        }
        return idPj;
    }

    /**
     * Update the name of file in core_file
     *
     * @param idFileList
     *            the id file list
     * @param ticket
     *            the ticket
     */
    private String createTechnicalFileName( int idFile, int idTicket )
    {
        String newNameForS3 = "";
        File file = FileHome.findByPrimaryKey( idFile );
        if ( null != file )
        {
            newNameForS3 = TicketTransfertPjService.nomDepotFichierUsager( idTicket, file.getTitle( ) );
        }
        return newNameForS3;
    }

    // Gestion des erreurs

    /**
     * Insert a record in table ticketing_erreur_scanner_strois
     *
     * @param filepath
     *            the
     * @param isInsertion
     *            true if it is an insertion error
     */
    private void addScannerS3Erreur( String filepath, boolean isInsertion )
    {
        boolean suppressionOk = false;
        if ( !isInsertion )
        {
            suppressionOk = _stockageS3ScannerDaemonMinio.deleteFileOnS3Serveur( filepath );
            if ( suppressionOk )
            {
                ErreurScannerStroisHome.removeByFilePath( filepath );
            }
        }
        List<ErreurScannerStrois> erreursTotalesList = ErreurScannerStroisHome.getErreurScannerStroisList( );
        List<String> erreursTotalesPathsList = erreursTotalesList.stream( ).map( e -> e.getPath( ) ).collect( Collectors.toList( ) );

        if ( !erreursTotalesPathsList.contains( filepath ) )
        {
            ErreurScannerStrois erreurSuppression = new ErreurScannerStrois( );
            erreurSuppression.setPath( filepath );
            erreurSuppression.setFileName( StringUtils.substringAfterLast( filepath, "/" ) );
            erreurSuppression.setDateErreur( new Timestamp( new java.util.Date( ).getTime( ) ) );
            erreurSuppression.setPbInsertion( isInsertion );
            ErreurScannerStroisHome.create( erreurSuppression );

            // TODO Ajout notification agent
        }

    }

}