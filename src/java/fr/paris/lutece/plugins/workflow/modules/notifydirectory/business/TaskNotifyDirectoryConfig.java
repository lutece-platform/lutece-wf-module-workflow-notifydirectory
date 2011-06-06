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


/**
 *
 * TaskNotifyDirectoryConfig
 *
 */
public class TaskNotifyDirectoryConfig
{
    private int _nIdTask;
    private int _nIdDirectory;
    private int _nPositionEntryDirectoryEmail;
    private int _nPositionEntryDirectorySms;
    private String _strSubject;
    private String _strMessage;
    private String _strSenderName;
    private boolean _bIsNotifyByEmail;
    private boolean _bIsNotifyBySms;
    private boolean _bEmailValidation;
    private int _nIdStateAfterValidation;
    private String _strLabelLink;
    private String _strMessageValidation;
    private int _nPeriodValidity;

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
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * set id directory
     * @param idTaskWorkflow id directory
     */
    public void setIdDirectory( int idDirectory )
    {
        _nIdDirectory = idDirectory;
    }

    /**
    *
    * @return position Entry directory Email
    */
    public int getPositionEntryDirectoryEmail(  )
    {
        return _nPositionEntryDirectoryEmail;
    }

    /**
     * set position Entry directory Email
     * @param idEntryDirectoryEmail position of Entry directory Email
     */
    public void setPositionEntryDirectoryEmail( int nPositionEntryDirectoryEmail )
    {
        _nPositionEntryDirectoryEmail = nPositionEntryDirectoryEmail;
    }

    /**
     *
     * @return position Entry directory Sms
     */
    public int getPositionEntryDirectorySms(  )
    {
        return _nPositionEntryDirectorySms;
    }

    /**
     * set position of Entry directory Sms
     * @param idTaskWorkflow position of Entry directory Sms
     */
    public void setPositionEntryDirectorySms( int nPositionEntryDirectorySms )
    {
        _nPositionEntryDirectorySms = nPositionEntryDirectorySms;
    }

    /**
    *
    * @return the subject of the message
    */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * set the subject of the message
     * @param subject the subject of the message
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }

    /**
     *
     * @return the message of the notification
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * set the message of the notification
     * @param message the message of the notifictaion
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     *
     * @return the sender name
     */
    public String getSenderName(  )
    {
        return _strSenderName;
    }

    /**
     * set the sender name
     * @param senderName  the sender name
     */
    public void setSenderName( String senderName )
    {
        _strSenderName = senderName;
    }

    /**
    *
    * @return true if notify by Email
    */
    public boolean isNotifyByEmail(  )
    {
        return _bIsNotifyByEmail;
    }

    /**
     * set true if notify by Email
     * @param true if notify by Email
     */
    public void setNotifyByEmail( boolean bIsNotifyByEmail )
    {
        _bIsNotifyByEmail = bIsNotifyByEmail;
    }

    /**
    *
    * @return true if notify by Sms
    */
    public boolean isNotifyBySms(  )
    {
        return _bIsNotifyBySms;
    }

    /**
     * set true if notify by Sms
     * @param enable true if notify by Sms
     */
    public void setNotifyBySms( boolean bIsNotifyBySms )
    {
        _bIsNotifyBySms = bIsNotifyBySms;
    }

    /**
    *
    * @return true if the email is email validation
    */
    public boolean isEmailValidation(  )
    {
        return _bEmailValidation;
    }

    /**
     * set true if the email is email validation
     * @param enable true if the email is email validation
     */
    public void setEmailValidation( boolean bEmailValidation )
    {
        _bEmailValidation = bEmailValidation;
    }

    /**
     *
     * @return id Entry directory
     */
    public int getIdStateAfterValidation(  )
    {
        return _nIdStateAfterValidation;
    }

    /**
     * set id of State Workflow after validation
     * @param idTaskWorkflow id of Task Workflow
     */
    public void setIdStateAfterValidation( int idStateAfterValidation )
    {
        _nIdStateAfterValidation = idStateAfterValidation;
    }

    /**
     *
     * @return the label link
     */
    public String getLabelLink(  )
    {
        return _strLabelLink;
    }

    /**
     * set the label link
     * @param strLabelLink the label link
     */
    public void setLabelLink( String strLabelLink )
    {
        _strLabelLink = strLabelLink;
    }

    /**
     *
     * @return the message of validation
     */
    public String getMessageValidation(  )
    {
        return _strMessageValidation;
    }

    /**
     * set the message of the validation
     * @param message the message of the validation
     */
    public void setMessageValidation( String messageValidation )
    {
        _strMessageValidation = messageValidation;
    }

    /**
    *
    * @return nPeriodValidity
    */
    public int getPeriodValidity(  )
    {
        return _nPeriodValidity;
    }

    /**
     * set nPeriodValidity
     * @param nPeriodValidity period of validity
     */
    public void setPeriodValidity( int nPeriodValidity )
    {
        _nPeriodValidity = nPeriodValidity;
    }
}
