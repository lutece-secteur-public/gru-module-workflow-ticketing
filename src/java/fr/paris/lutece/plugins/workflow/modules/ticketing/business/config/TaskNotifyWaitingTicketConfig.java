package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflow.modules.ticketing.business.email.config.MessageDirectionExternalUser;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

import javax.validation.constraints.NotNull;

public class TaskNotifyWaitingTicketConfig extends TaskConfig
{
    @NotNull
    private MessageDirectionExternalUser _messageDirectionExternalUser;

    private Integer _nIdFollowingAction;

    private Integer _nIdContactAttribute;

    private String _strDefaultSubject;

    /**
     * Gives the message direction
     *
     * @return the message direction
     */
    public MessageDirectionExternalUser getMessageDirectionExternalUser( )
    {
        return _messageDirectionExternalUser;
    }

    /**
     * Sets the message direction
     *
     * @param messageDirectionExternalUser
     *            the message direction to set
     */
    public void setMessageDirectionExternalUser( MessageDirectionExternalUser messageDirectionExternalUser )
    {
        _messageDirectionExternalUser = messageDirectionExternalUser;
    }

    /**
     * @return the idFollowingAction
     */
    public Integer getIdFollowingAction( )
    {
        return _nIdFollowingAction;
    }

    /**
     * @param nIdFollowingAction
     *            the idFollowingAction to set
     */
    public void setIdFollowingAction( Integer nIdFollowingAction )
    {
        _nIdFollowingAction = nIdFollowingAction;
    }

    /**
     * @return true if the direction is agent to external user
     */
    public boolean isMessageToExternalUser( )
    {
        return MessageDirectionExternalUser.AGENT_TO_EXTERNAL_USER.equals( _messageDirectionExternalUser );
    }

    /**
     * @return the idContactAttribute
     */
    public Integer getIdContactAttribute( )
    {
        return _nIdContactAttribute;
    }

    /**
     * @param nIdContactAttribute
     *            Id Contact Attribute
     */
    public void setIdContactAttribute( Integer nIdContactAttribute )
    {
        _nIdContactAttribute = nIdContactAttribute;
    }

    public String getDefaultSubject( )
    {
        return _strDefaultSubject;
    }

    public void setDefaultSubject( String defaultSubject )
    {
        _strDefaultSubject = defaultSubject;
    }
}
