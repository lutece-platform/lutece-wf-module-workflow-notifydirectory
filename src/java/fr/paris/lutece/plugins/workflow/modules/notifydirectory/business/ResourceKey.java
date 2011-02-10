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

import java.sql.Timestamp;


/**
 *
 * ResourceKey
 *
 */
public class ResourceKey
{
    private int _nIdTask;
    private String _strKey;
    private int _nIdResource;
    private String _strResourceType;
    private Timestamp _tDateExpiry;

    /**
     *
     * @return id Task
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * set id Task
     * @param idTask id task
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /**
    *
    * @return id directory
    */
    public int getIdResource(  )
    {
        return _nIdResource;
    }

    /**
     * set id directory
     * @param idTaskWorkflow id directory
     */
    public void setIdResource( int IdResource )
    {
        _nIdResource = IdResource;
    }

    /**
    *
    * @return id directory
    */
    public String getKey(  )
    {
        return _strKey;
    }

    /**
     * set id directory
     * @param idTaskWorkflow id directory
     */
    public void setKey( String strKey )
    {
        _strKey = strKey;
    }

    /**
    *
    * @return id directory
    */
    public String getResourceType(  )
    {
        return _strResourceType;
    }

    /**
     * set id directory
     * @param idTaskWorkflow id directory
     */
    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    /**
    *
    * @return id Task
    */
    public Timestamp getDateExpiry(  )
    {
        return _tDateExpiry;
    }

    /**
    * set id Task
    * @param idTask id task
    */
    public void setDateExpiry( Timestamp tDateExpiry )
    {
        _tDateExpiry = tDateExpiry;
    }
}
