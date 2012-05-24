/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;


/**
 *
 * TaskNotifyDirectoryConfigService
 *
 */
public class TaskNotifyDirectoryConfigService implements ITaskNotifyDirectoryConfigService
{
    public static final String BEAN_SERVICE = "workflow-notifydirectory.taskNotifyDirectoryConfigService";
    @Inject
    private ITaskNotifyDirectoryConfigDAO _taskNotifyDirectoryConfigDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-notifydirectory.transactionManager" )
    public void create( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        if ( config != null )
        {
            _taskNotifyDirectoryConfigDAO.insert( config, plugin );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-notifydirectory.transactionManager" )
    public void update( TaskNotifyDirectoryConfig config, Plugin plugin )
    {
        if ( config != null )
        {
            _taskNotifyDirectoryConfigDAO.store( config, plugin );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-notifydirectory.transactionManager" )
    public void remove( int nIdTask, Plugin plugin )
    {
        _taskNotifyDirectoryConfigDAO.delete( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskNotifyDirectoryConfig findByPrimaryKey( int nIdTask, Plugin plugin )
    {
        return _taskNotifyDirectoryConfigDAO.load( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskNotifyDirectoryConfig> findAll( Plugin plugin )
    {
        return _taskNotifyDirectoryConfigDAO.loadAll( plugin );
    }
}
