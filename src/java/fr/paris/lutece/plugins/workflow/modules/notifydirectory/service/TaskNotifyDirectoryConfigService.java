/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.service;

import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfigHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 *
 * TaskNotifyDirectoryConfigService
 *
 */
public final class TaskNotifyDirectoryConfigService
{
    private static final String BEAN_TASK_NOTIFY_Directory_CONFIG_SERVICE = "workflow-notifydirectory.taskNotifyDirectoryConfigService";

    /**
     * Private constructor
     */
    private TaskNotifyDirectoryConfigService(  )
    {
    }

    /**
     * Get the instance of {@link TaskNotifyDirectoryConfigService}
     * @return the instance of {@link TaskNotifyDirectoryConfigService}
     */
    public static TaskNotifyDirectoryConfigService getService(  )
    {
        return (TaskNotifyDirectoryConfigService) SpringContextService.getPluginBean( NotifyDirectoryPlugin.PLUGIN_NAME,
            BEAN_TASK_NOTIFY_Directory_CONFIG_SERVICE );
    }

    /**
     * Create a new config
     * @param plugin the Plugin
     * @param config the config
     */
    public void create( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        if ( config != null )
        {
            TaskNotifyDirectoryConfigHome.create( config, plugin );
        }
    }

    /**
     * Update a config
     * @param plugin the Plugin
     * @param config the config
     */
    public void update( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        if ( config != null )
        {
            TaskNotifyDirectoryConfigHome.update( config, plugin );
        }
    }

    /**
     * Remove a config
     * @param plugin the Plugin
     * @param nIdTask the task id
     */
    public void remove( int nIdTask, Plugin plugin )
    {
        TaskNotifyDirectoryConfigHome.remove( nIdTask, plugin );
    }

    /**
     * Find a config
     * @param nIdTask the id task
     * @param plugin the Plugin
     * @return an instance of {@link TaskNotifyDirectoryConfig}
     */
    public TaskNotifyDirectoryConfig findByPrimaryKey( int nIdTask, Plugin plugin )
    {
        return TaskNotifyDirectoryConfigHome.findByPrimaryKey( nIdTask, plugin );
    }

    /**
     * Get all configs
     * @param plugin the Plugin
     * @return a list of {@link TaskNotifyDirectoryConfig}
     */
    public List<TaskNotifyDirectoryConfig> findAll( Plugin plugin )
    {
        return TaskNotifyDirectoryConfigHome.getAll( plugin );
    }
}
