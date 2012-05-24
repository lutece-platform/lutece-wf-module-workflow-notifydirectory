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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.mail.FileAttachment;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 */
public interface INotifyDirectoryService
{
    // CHECKS

    /**
     * Check if the given entry type id is accepted for the email or the sms
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeEmailSMSAccepted( int nIdEntryType );

    /**
     * Check if the given entry type id is accepted for the user guid
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeUserGuidAccepted( int nIdEntryType );

    /**
     * Check if the given entry type id is accepted for file
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeFileAccepted( int nIdEntryType );

    /**
     * Check if the entry is refused (values set in the
     * workflow-notifydirectory.properties)
     * @param nIdEntryType the id entry type
     * @return true if it is refused, false otherwise
     */
    boolean isEntryTypeRefused( int nIdEntryType );

    // GETS

    /**
     * Get the list of states
     * @param nIdAction  the id action
     * @return a ReferenceList
     */
    ReferenceList getListStates( int nIdAction );

    /**
     * Get the list of directorise
     * @return a ReferenceList
     */
    ReferenceList getListDirectories(  );

    /**
     * Get the mailing list
     * @param request the HTTP request
     * @return a ReferenceList
     */
    ReferenceList getMailingList( HttpServletRequest request );

    /**
     * Get the list of entries from a given id task
     * @param nIdTask the id task
     * @return a list of IEntry
     */
    List<IEntry> getListEntries( int nIdTask );

    /**
     * Get the list of entries that have the accepted type (which are defined in
     * <b>workflow-notifydirectory.properties</b>)
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a ReferenceList
     */
    ReferenceList getListEntriesUserGuid( int nIdTask, Locale locale );

    /**
     * Get the list of entries that have the accepted type (which are defined in
     * <b>workflow-notifydirectory.properties</b>)
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a ReferenceList
     */
    ReferenceList getListEntriesEmailSMS( int nIdTask, Locale locale );

    /**
     * Get the list of entries that have not the refused type (which are defined
     * in the <b>workflow-notifydirectory.properties</b>). <br />
     * This list will be displayed as a freemarker label that the webmaster can
     * use to write the notifications.
     * @param nIdTask the id task
     * @return a list of {@link IEntry}
     */
    List<IEntry> getListEntriesFreemarker( int nIdTask );

    /**
     * Get the list of entries that have the accepted type for file)
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a List of entries
     */
    List<IEntry> getListEntriesFile( int nIdTask, Locale locale );

    /**
     * Get the email from either an entry containing the email, or an entry
     * containing the user guid
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the email
     */
    String getEmail( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory );

    /**
     * Get the sms phone number
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the sms phone number
     */
    String getSMSPhoneNumber( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory );

    /**
     * The files Attachments to insert in the mail
     * @param config the configuration
     * @param nIdRecord the record id
     * @param nIdDirectory the  directory id
     * @return the files Attachments to insert in the mail
     */
    List<FileAttachment> getFilesAttachment( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory );

    /**
     * Get the user guid
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the user guid, an empty string if the position is not set
     */
    String getUserGuid( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory );

    /**
     * Get the list of tasks
     * @param nIdAction the id action
     * @param locale the locale
     * @return a list of {@link ITask}
     */
    List<ITask> getListTasks( int nIdAction, Locale locale );

    // OTHERS

    /**
     * Send the message
     * @param config the config
     * @param strEmail the email
     * @param strSms the sms
     * @param strSenderEmail the sender email
     * @param strSubject the subject
     * @param strEmailContent the email content
     * @param strSMSContent the sms content
     * @param listFileAttachment the list of attachments
     */
    void sendMessage( TaskNotifyDirectoryConfig config, String strEmail, String strSms, String strSenderEmail,
        String strSubject, String strEmailContent, String strSMSContent, List<FileAttachment> listFileAttachment );

    /**
     * Fill the model
     * @param config the config
     * @param resourceHistory the resource history
     * @param record the record
     * @param directory the directory
     * @param request the HTTP request
     * @param nIdAction the id action
     * @param nIdHistory the id history
     * @return the model
     */
    Map<String, Object> fillModel( TaskNotifyDirectoryConfig config, ResourceHistory resourceHistory, Record record,
        Directory directory, HttpServletRequest request, int nIdAction, int nIdHistory );
}
