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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.business;

import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.NotifyDirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * TaskNotifyDirectoryConfigHome
 */
public final class TaskNotifyDirectoryConfigHome
{
    // Static variable pointed at the DAO instance
    private static ITaskNotifyDirectoryConfigDAO _dao = (ITaskNotifyDirectoryConfigDAO) SpringContextService.getPluginBean( NotifyDirectoryPlugin.PLUGIN_NAME,
            "taskNotifyDirectoryConfigDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TaskNotifyDirectoryConfigHome(  )
    {
    }

    /**
     * Insert new configuration
     *
     * @param config object configuration
     * @param plugin the plugin
     */
    public static void create( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        _dao.insert( config, plugin );

        if ( config.getListPositionEntryFile(  ) != null )
        {
            for ( Integer nPositionEntryFile : config.getListPositionEntryFile(  ) )
            {
                _dao.insertListPositionEntryFile( config.getIdTask(  ), nPositionEntryFile, plugin );
            }
        }
    }

    /**
     * Update a configuration
     *
     * @param config object configuration
     * @param plugin the plugin
     */
    public static void update( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        _dao.store( config, plugin );
        _dao.deleteListPositionEntryFile( config.getIdTask(  ), plugin );

        if ( config.getListPositionEntryFile(  ) != null )
        {
            for ( Integer nPositionEntryFile : config.getListPositionEntryFile(  ) )
            {
                _dao.insertListPositionEntryFile( config.getIdTask(  ), nPositionEntryFile, plugin );
            }
        }
    }

    /**
     * Delete a configuration
     * @param nIdTask id task
     * @param plugin the plugin
     */
    public static void remove( int nIdTask, Plugin plugin )
    {
        _dao.deleteListPositionEntryFile( nIdTask, plugin );
        _dao.delete( nIdTask, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Delete a configuration
     * @param nIdTask id task
     * @param plugin the plugin
     * @return a configuration
     *
     */
    public static TaskNotifyDirectoryConfig findByPrimaryKey( int nIdTask, Plugin plugin )
    {
        TaskNotifyDirectoryConfig taskNotifyDirectoryConfig = _dao.load( nIdTask, plugin );

        if ( taskNotifyDirectoryConfig != null )
        {
            taskNotifyDirectoryConfig.setListPositionEntryFile( _dao.loadListPositionEntryFile( nIdTask, plugin ) );
        }

        return taskNotifyDirectoryConfig;
    }

    /**
     * Load All Directorty
     * @param plugin the plugin
     * @return a configuration
     *
     */
    public static List<TaskNotifyDirectoryConfig> getAll( Plugin plugin )
    {
        List<TaskNotifyDirectoryConfig> listTaskNotifyDirectoryConfig = _dao.loadAll( plugin );

        if ( listTaskNotifyDirectoryConfig != null )
        {
            for ( TaskNotifyDirectoryConfig taskNotifyDirectoryConfig : listTaskNotifyDirectoryConfig )
            {
                taskNotifyDirectoryConfig.setListPositionEntryFile( _dao.loadListPositionEntryFile( 
                        taskNotifyDirectoryConfig.getIdTask(  ), plugin ) );
            }
        }

        return listTaskNotifyDirectoryConfig;
    }
}
