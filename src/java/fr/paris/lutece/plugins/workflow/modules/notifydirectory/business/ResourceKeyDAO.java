/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *class   TaskCommentConfig
 *
 */
public class ResourceKeyDAO implements IResourceKeyDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT key_email,id_task,id_resource,resource_type,date_expiry" +
        " FROM task_notify_directory_key  WHERE key_email=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO task_notify_directory_key( " +
        "key_email,id_task,id_resource,resource_type,date_expiry)" + " VALUES (?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE task_notify_directory_key " +
        "SET key_email=?,id_task=?,id_resource=?,resource_type=?,date_expiry=?" + " WHERE key_email=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM task_notify_directory_key WHERE key_email=?";
    private static final String SQL_QUERY_DELETE_EXPIRY = "SELECT key_email,id_task,id_resource,resource_type,date_expiry FROM task_notify_directory_key WHERE date_expiry<NOW()";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.IResourceKeyDAO#insert(fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKey, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( ResourceKey resourceKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setString( ++nPos, resourceKey.getKey(  ) );
        daoUtil.setInt( ++nPos, resourceKey.getIdTask(  ) );
        daoUtil.setInt( ++nPos, resourceKey.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceKey.getResourceType(  ) );
        daoUtil.setTimestamp( ++nPos, resourceKey.getDateExpiry(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.IResourceKeyDAO#store(fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKey, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( ResourceKey resourceKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setString( ++nPos, resourceKey.getKey(  ) );
        daoUtil.setInt( ++nPos, resourceKey.getIdTask(  ) );
        daoUtil.setInt( ++nPos, resourceKey.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceKey.getResourceType(  ) );
        daoUtil.setTimestamp( ++nPos, resourceKey.getDateExpiry(  ) );

        daoUtil.setString( ++nPos, resourceKey.getKey(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.IResourceKeyDAO#load(String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public ResourceKey load( String strKey, Plugin plugin )
    {
        ResourceKey resourceKey = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setString( 1, strKey );
        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            resourceKey = new ResourceKey(  );
            resourceKey.setKey( daoUtil.getString( ++nPos ) );
            resourceKey.setIdTask( daoUtil.getInt( ++nPos ) );
            resourceKey.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceKey.setResourceType( daoUtil.getString( ++nPos ) );
            resourceKey.setDateExpiry( daoUtil.getTimestamp( ++nPos ) );
        }

        daoUtil.free(  );

        return resourceKey;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.IResourceKeyDAO#delete(String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( String strKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setString( 1, strKey );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.IResourceKeyDAO#deleteExpiry(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<ResourceKey> selectResourceExpiry( Plugin plugin )
    {
        int nPos = 0;
        List<ResourceKey> listResourceExpiry = new ArrayList<ResourceKey>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_EXPIRY, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;

            ResourceKey resourceKey = new ResourceKey(  );
            resourceKey.setKey( daoUtil.getString( ++nPos ) );
            resourceKey.setIdTask( daoUtil.getInt( ++nPos ) );
            resourceKey.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceKey.setResourceType( daoUtil.getString( ++nPos ) );
            resourceKey.setDateExpiry( daoUtil.getTimestamp( ++nPos ) );
            listResourceExpiry.add( resourceKey );
        }

        daoUtil.free(  );

        return listResourceExpiry;
    }
}
