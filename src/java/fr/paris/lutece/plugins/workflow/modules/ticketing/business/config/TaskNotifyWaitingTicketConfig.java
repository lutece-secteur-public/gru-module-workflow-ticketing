package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskNotifyWaitingTicketConfig extends TaskConfig
{
    private String _strSenderName;
    private String _strSubject;
    private String _strMessage;

    /**
     * Get value of _strSenderName
     *
     * @return _strSenderName
     **/
    public String getSenderName( )
    {
        return _strSenderName;
    }

    /**
     * Sets a value to _strSenderName
     *
     * @param strSenderName the strSenderName to set
     **/
    public void setSenderName( String strSenderName )
    {
        this._strSenderName = strSenderName;
    }

    /**
     * Get value of _strSubjet
     *
     * @return _strSubjet
     **/
    public String getSubject( )
    {
        return _strSubject;
    }

    /**
     * Sets a value to _strSubjet
     *
     * @param strSubjet the strSubjet to set
     **/
    public void setSubject( String strSubjet )
    {
        this._strSubject = strSubjet;
    }

    /**
     * Get value of _strMessage
     *
     * @return _strMessage
     **/
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * Sets a value to _strMessage
     *
     * @param strMessage the strMessage to set
     **/
    public void setMessage( String strMessage )
    {
        this._strMessage = strMessage;
    }
}
