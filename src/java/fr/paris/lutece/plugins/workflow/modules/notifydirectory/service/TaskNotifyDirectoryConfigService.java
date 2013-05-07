/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ITaskNotifyDirectoryConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;


/**
 *
 * TaskNotifyDirectoryConfigService
 *
 */
public class TaskNotifyDirectoryConfigService extends TaskConfigService
{
    public static final String BEAN_SERVICE = "workflow-notifydirectory.taskNotifyDirectoryConfigService";
    @Inject
    private ITaskNotifyDirectoryConfigDAO _taskNotifyDirectoryConfigDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( NotifyDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void create( ITaskConfig config )
    {
        super.create( config );

        TaskNotifyDirectoryConfig notifyConfig = getConfigBean( config );

        if ( ( notifyConfig != null ) && ( notifyConfig.getListPositionEntryFile(  ) != null ) )
        {
            for ( int nPositionEntryFile : notifyConfig.getListPositionEntryFile(  ) )
            {
                this.createPositionEntryFile( config.getIdTask(  ), nPositionEntryFile );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( NotifyDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void update( ITaskConfig config )
    {
        super.update( config );

        TaskNotifyDirectoryConfig notifyConfig = getConfigBean( config );

        if ( notifyConfig != null )
        {
            // First remove the links task - entries file
            this.removeListPositionEntryFile( config.getIdTask(  ) );

            // Then add the links
            if ( notifyConfig.getListPositionEntryFile(  ) != null )
            {
                for ( int nPositionEntryFile : notifyConfig.getListPositionEntryFile(  ) )
                {
                    this.createPositionEntryFile( config.getIdTask(  ), nPositionEntryFile );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( NotifyDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void remove( int nIdTask )
    {
        super.remove( nIdTask );
        this.removeListPositionEntryFile( nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T findByPrimaryKey( int nIdTask )
    {
        TaskNotifyDirectoryConfig config = super.findByPrimaryKey( nIdTask );

        if ( config != null )
        {
            List<Integer> listPositionEntryFile = findPositionEntryFile( nIdTask );

            if ( ( listPositionEntryFile != null ) && !listPositionEntryFile.isEmpty(  ) )
            {
                config.setListPositionEntryFile( listPositionEntryFile );
            }
        }

        return (T) config;
    }

    /**
     * Find the positions entry file from a given id task
     * @param nIdTask the id task
     * @return a list of position
     */
    private List<Integer> findPositionEntryFile( int nIdTask )
    {
        return _taskNotifyDirectoryConfigDAO.loadListPositionEntryFile( nIdTask );
    }

    /**
     * Create the link task - entry file
     * @param nIdTask the id task
     * @param nPositionEntryFile the position entry file
     */
    private void createPositionEntryFile( int nIdTask, int nPositionEntryFile )
    {
        _taskNotifyDirectoryConfigDAO.insertListPositionEntryFile( nIdTask, nPositionEntryFile );
    }

    /**
     * Remove all links task - entry file
     * @param nIdTask the id task
     */
    private void removeListPositionEntryFile( int nIdTask )
    {
        _taskNotifyDirectoryConfigDAO.deleteListPositionEntryFile( nIdTask );
    }
}
