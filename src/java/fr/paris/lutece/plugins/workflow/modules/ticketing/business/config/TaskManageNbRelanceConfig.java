package fr.paris.lutece.plugins.workflow.modules.ticketing.business.config;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskManageNbRelanceConfig extends TaskConfig
{

    private Boolean isReinit; // true = réinitialiser, false = ajouter 1
    private Boolean isUsager; // true = nb relance usager, false = nb relance agent

    public Boolean getIsUsager( )
    {
        return isUsager;
    }

    public void setIsUsager( Boolean isUsager )
    {
        this.isUsager = isUsager;
    }

    public Boolean getIsReinit( )
    {
        return isReinit;
    }

    public void setIsReinit( Boolean isReinit )
    {
        this.isReinit = isReinit;
    }
}

