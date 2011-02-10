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

import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.NotifyDirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * ResourceKeyHome
 */
public final class ResourceKeyHome
{
    // Static variable pointed at the DAO instance
    private static IResourceKeyDAO _dao = (IResourceKeyDAO) SpringContextService.getPluginBean( NotifyDirectoryPlugin.PLUGIN_NAME,
            "taskNotifyDirectoryResourceKeyDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ResourceKeyHome(  )
    {
    }

    /**
     * Insert new resourceKey
     *
     * @param resourceKey object ResourceKey
     * @param plugin the plugin
     */
    public static void create( ResourceKey resourceKey, Plugin plugin )
    {
        _dao.insert( resourceKey, plugin );
    }

    /**
     * Update a ResourceKey
     *
     * @param resourceKey object ResourceKey
     * @param plugin the plugin
     */
    public static void update( ResourceKey resourceKey, Plugin plugin )
    {
        _dao.store( resourceKey, plugin );
    }

    /**
     * Delete a ResourceKey
     * @param strKey key
     * @param plugin the plugin
     */
    public static void remove( String strKey, Plugin plugin )
    {
        _dao.delete( strKey, plugin );
    }

    /**
     * Delete a ResourceKey
     * @param strKey key
     * @param plugin the plugin
     * @return a ResourceKey
     *
     */
    public static ResourceKey findByPrimaryKey( String strKey, Plugin plugin )
    {
        ResourceKey resourceKey = _dao.load( strKey, plugin );

        return resourceKey;
    }

    /**
     * Delete a ResourceKey expiry
     * @param plugin the plugin
     */
    public static List<ResourceKey> selectResourceExpiry( Plugin plugin )
    {
        return _dao.selectResourceExpiry( plugin );
    }
}
