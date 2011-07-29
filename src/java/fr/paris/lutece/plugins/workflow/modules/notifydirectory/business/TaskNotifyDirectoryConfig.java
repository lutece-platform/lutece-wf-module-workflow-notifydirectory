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
    private int _nPositionEntryDirectoryUserGuid;
    private String _strSubject;
    private String _strMessage;
    private String _strSenderName;
    private boolean _bIsNotifyByEmail;
    private boolean _bIsNotifyBySms;
    private boolean _bIsNotifyByMailingList;
    private boolean _bIsNotifyByUserGuid;
    private boolean _bEmailValidation;
    private int _nIdStateAfterValidation;
    private String _strLabelLink;
    private String _strMessageValidation;
    private int _nPeriodValidity;
    private String _strRecipientsBcc;
    private int _nIdMailingList;

    /**
     * Get the id task
     * @return id Task
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * Set id Task
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
     * Set id directory
     * @param idDirectory id directory
     */
    public void setIdDirectory( int idDirectory )
    {
        _nIdDirectory = idDirectory;
    }

    /**
     * Get the position of the entry directory form email
     * @return position Entry directory Email
     */
    public int getPositionEntryDirectoryEmail(  )
    {
        return _nPositionEntryDirectoryEmail;
    }

    /**
     * Set position Entry directory Email
     * @param nPositionEntryDirectoryEmail position of Entry directory Email
     */
    public void setPositionEntryDirectoryEmail( int nPositionEntryDirectoryEmail )
    {
        _nPositionEntryDirectoryEmail = nPositionEntryDirectoryEmail;
    }

    /**
     * Get the position of the entry directory for sms
     * @return position Entry directory Sms
     */
    public int getPositionEntryDirectorySms(  )
    {
        return _nPositionEntryDirectorySms;
    }

    /**
     * Set position of Entry directory Sms
     * @param nPositionEntryDirectorySms position of Entry directory Sms
     */
    public void setPositionEntryDirectorySms( int nPositionEntryDirectorySms )
    {
        _nPositionEntryDirectorySms = nPositionEntryDirectorySms;
    }

    /**
     * Get the position of the entry directory associated to the user guid
     * @return position Entry directory user guid
     */
    public int getPositionEntryDirectoryUserGuid(  )
    {
        return _nPositionEntryDirectoryUserGuid;
    }

    /**
     * Set position Entry directory user guid
     * @param nPositionEntryDirectoryUserGuid position of Entry directory user guid
     */
    public void setPositionEntryDirectoryUserGuid( int nPositionEntryDirectoryUserGuid )
    {
        _nPositionEntryDirectoryUserGuid = nPositionEntryDirectoryUserGuid;
    }

    /**
     * Get the subject
     * @return the subject of the message
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * Set the subject of the message
     * @param subject the subject of the message
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }

    /**
     * Get the message
     * @return the message of the notification
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * Set the message of the notification
     * @param message the message of the notifictaion
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     * Get the sender name
     * @return the sender name
     */
    public String getSenderName(  )
    {
        return _strSenderName;
    }

    /**
     * Set the sender name
     * @param senderName the sender name
     */
    public void setSenderName( String senderName )
    {
        _strSenderName = senderName;
    }

    /**
     * Check if it is notify by mail
     * @return true if notify by Email
     */
    public boolean isNotifyByEmail(  )
    {
        return _bIsNotifyByEmail;
    }

    /**
     * Set true if notify by Email
     * @param bIsNotifyByEmail true if notify by Email
     */
    public void setNotifyByEmail( boolean bIsNotifyByEmail )
    {
        _bIsNotifyByEmail = bIsNotifyByEmail;
    }

    /**
     * Check if it is notify by sms
     * @return true if notify by Sms
     */
    public boolean isNotifyBySms(  )
    {
        return _bIsNotifyBySms;
    }

    /**
     * Set true if notify by Sms
     * @param bIsNotifyBySms enable true if notify by Sms
     */
    public void setNotifyBySms( boolean bIsNotifyBySms )
    {
        _bIsNotifyBySms = bIsNotifyBySms;
    }

    /**
     * Check if it is notify by MailingList
     * @return true if notify by MailingList
     */
    public boolean isNotifyByMailingList(  )
    {
        return _bIsNotifyByMailingList;
    }

    /**
     * Set true if notify by MailingList
     * @param bIsNotifyByMailingList enable true if notify by Sms
     */
    public void setNotifyByMailingList( boolean bIsNotifyByMailingList )
    {
        _bIsNotifyByMailingList = bIsNotifyByMailingList;
    }

    /**
     * Set true if notify by user guid
     * @param bIsNotifyByUserGuid true if notify by user guid
     */
    public void setNotifyByUserGuid( boolean bIsNotifyByUserGuid )
    {
        _bIsNotifyByUserGuid = bIsNotifyByUserGuid;
    }

    /**
     * Check if notify by user guid
     * @return true if notify by user guid
     */
    public boolean isNotifyByUserGuid(  )
    {
        return _bIsNotifyByUserGuid;
    }

    /**
     * Check if it must have an email validation
     * @return true if the email is email validation
     */
    public boolean isEmailValidation(  )
    {
        return _bEmailValidation;
    }

    /**
     * Set true if the email is email validation
     * @param bEmailValidation enable true if the email is email validation
     */
    public void setEmailValidation( boolean bEmailValidation )
    {
        _bEmailValidation = bEmailValidation;
    }

    /**
     * Get the id state after validation
     * @return id Entry directory
     */
    public int getIdStateAfterValidation(  )
    {
        return _nIdStateAfterValidation;
    }

    /**
     * Set id of State Workflow after validation
     * @param nIdStateAfterValidation the id state after validation
     */
    public void setIdStateAfterValidation( int nIdStateAfterValidation )
    {
        _nIdStateAfterValidation = nIdStateAfterValidation;
    }

    /**
     * Get the label link
     * @return the label link
     */
    public String getLabelLink(  )
    {
        return _strLabelLink;
    }

    /**
     * Set the label link
     * @param strLabelLink the label link
     */
    public void setLabelLink( String strLabelLink )
    {
        _strLabelLink = strLabelLink;
    }

    /**
     * Get the message validation
     * @return the message of validation
     */
    public String getMessageValidation(  )
    {
        return _strMessageValidation;
    }

    /**
     * Set the message of the validation
     * @param messageValidation the message of the validation
     */
    public void setMessageValidation( String messageValidation )
    {
        _strMessageValidation = messageValidation;
    }

    /**
     * Get the period validity
     * @return nPeriodValidity
     */
    public int getPeriodValidity(  )
    {
        return _nPeriodValidity;
    }

    /**
     * Set nPeriodValidity
     * @param nPeriodValidity period of validity
     */
    public void setPeriodValidity( int nPeriodValidity )
    {
        _nPeriodValidity = nPeriodValidity;
    }

    /**
     * Returns the Recipient
     * @return The Recipient
     */
    public String getRecipientsBcc(  )
    {
        return _strRecipientsBcc;
    }

    /**
     * Sets the Recipient
     * @param strRecipient The Recipient
     */
    public void setRecipientsBcc( String strRecipient )
    {
        _strRecipientsBcc = strRecipient;
    }

    /**
     * Get the id mailing list
     * @return the id mailing list
     */
    public int getIdMailingList(  )
    {
        return _nIdMailingList;
    }

    /**
    * Set the id mailing list
    * @param nIdMailingList the id mailing list
    */
    public void setIdMailingList( int nIdMailingList )
    {
        _nIdMailingList = nIdMailingList;
    }
}
