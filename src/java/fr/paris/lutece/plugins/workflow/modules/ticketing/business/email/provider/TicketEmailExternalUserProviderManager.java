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

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.AbstractProviderManager;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * Manager for the TicketEmailExternalUser provider
 */
public class TicketEmailExternalUserProviderManager extends AbstractProviderManager
{
    private static final String PROVIDER_ID = "ticket";

    private static final String MESSAGE_PROVIDER_LABEL = "module.workflow.ticketing.task_ticket_email_external_user.notifygru_provider";

    /**
     * constructor
     *
     * @param strId
     *            id
     */
    public TicketEmailExternalUserProviderManager( String strId )
    {
        super( strId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( ITask task )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );

        collectionProviderDescriptions.add( getProviderDescription( null ) );

        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProviderDescription getProviderDescription( String strProviderId )
    {
        ProviderDescription providerDescription = new ProviderDescription( PROVIDER_ID,
                I18nService.getLocalizedString( MESSAGE_PROVIDER_LABEL, I18nService.getDefaultLocale( ) ) );

        providerDescription.setMarkerDescriptions( TicketEmailExternalUserProvider.getProviderMarkerDescriptions( ) );

        return providerDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IProvider createProvider( String strProviderId, ResourceHistory resourceHistory, HttpServletRequest request )
    {
        return new TicketEmailExternalUserProvider( resourceHistory );
    }
}
