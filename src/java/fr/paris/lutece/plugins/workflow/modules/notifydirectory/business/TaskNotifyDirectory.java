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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ActionHome;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.StateFilter;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.business.workflow.Workflow;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;


/**
 *
 * TaskFillingDirectory
 */
public class TaskNotifyDirectory extends Task
{
    //templates
    private static final String TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_config.html";
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_mail.html";
    private static final String TEMPLATE_TASK_NOTIFY_SMS = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_sms.html";

    //Properties
    private static final String PROPERTY_NOTIFY_MAIL_DEFAULT_SENDER_NAME = "workflow-notifydirectory.notification_mail.default_sender_name";
    private static final String PROPERTY_ACCEPT_DIRECTORY_TYPE = "workflow-notifydirectory.accept_directory_type";
    private static final String PROPERTY_REFUSE_DIRECTORY_TYPE = "workflow-notifydirectory.refuse_directory_type";
    private static final String PROPERTY_SERVER_SMS = "workflow-notifydirectory.email_server_sms";
    private static final String PROPERTY_URL = "lutece.prod.url";

    //Marks
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_STATUS = "status";
    private static final String MARK_POSITION = "position_";
    private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";
    private static final String MARK_DIRECTORY_ENTRY_LIST = "list_entry_directory";
    private static final String MARK_DIRECTORY_LIST = "list_directory";
    private static final String MARK_CONFIG = "config";
    private static final String MARK_STATE_LIST = "list_state";
    private static final String MARK_LINK = "link";
    private static final String MARK_ENTRY_LIST_FREEMARKER = "entry_list_freemarker";
    private static final String MARK_DIRECTORY_TITLE = "directory_title";
    private static final String MARK_DIRECTORY_DESCRIPTION = "directory_description";

    //Parameters
    private static final String PARAMETER_NOTIFY = "notify";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_POSITION_ENTRY_DIRECTORY_SMS = "position_entry_directory_sms";
    private static final String PARAMETER_POSITION_ENTRY_DIRECTORY_EMAIL = "position_entry_directory_email";
    private static final String PARAMETER_ID_STATE = "id_state";
    private static final String PARAMETER_EMAIL_VALIDATION = "email_validation";
    private static final String PARAMETER_MESSAGE_VALIDATION = "message_validation";
    private static final String PARAMETER_LABEL_LINK = "label_link";
    private static final String PARAMETER_PERIOD_VALIDTY = "period_validity";

    //Fields
    private static final String FIELD_NOTIFY = "module.workflow.notifydirectory.task_notify_directory_config.label_notify_by";
    private static final String FIELD_SUBJECT = "module.workflow.notifydirectory.task_notify_directory_config.label_subject";
    private static final String FIELD_MESSAGE = "module.workflow.notifydirectory.task_notify_directory_config.label_message";
    private static final String FIELD_SENDER_NAME = "module.workflow.notifydirectory.task_notify_directory_config.label_sender_name";
    private static final String FIELD_TASK_DIRECTORY = "module.workflow.notifydirectory.task_notify_directory_config.label_task_directory";
    private static final String FIELD_TASK_ENTRY_DIRECTORY_SMS = "module.workflow.notifydirectory.task_notify_directory_config.label_task_entry_directory_sms";
    private static final String FIELD_TASK_ENTRY_DIRECTORY_EMAIL = "module.workflow.notifydirectory.task_notify_directory_config.label_task_entry_directory_email";
    private static final String FIELD_STATE = "module.workflow.notifydirectory.task_notify_directory_config.label_state";
    private static final String FIELD_MESSAGE_VALIDATION = "module.workflow.notifydirectory.task_notify_directory_config.label_message_validation";
    private static final String FIELD_LABEL_LINK = "module.workflow.notifydirectory.task_notify_directory_config.label_label_link";
    private static final String FIELD_LABEL_PERIOD_VALIDITY = "module.workflow.notifydirectory.task_notify_directory_config.label_period_validity";
    private static final String LABEL_REFERENCE_DIRECTORY = "module.workflow.notifydirectory.task_notify_directory_config.label_reference_directory";

    //Message
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.notifydirectory.message.mandatory.field";
    private static final String MESSAGE_EQUAL_FIELD = "module.workflow.notifydirectory.message.equal.field";

    //Parameters
    private static final String PARAMETER_APPLY = "apply";
    private static final String HTML_LINK_CLOSE = "</a>";
    private static final String HTML_LINK_OPEN_1 = "<a href=\"";
    private static final String HTML_LINK_OPEN_2 = "\" >";
    private static final String COMMA = ",";
    private static final String ID = "id";
    private static final String NAME = "name";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#init()
     */
    public void init(  )
    {
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.ITaskListener#doSaveConfig(fr.paris.lutece.plugins.workflow.business.Action, javax.servlet.http.HttpServletRequest)
     */
    public String doSaveConfig( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        int nNotify = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_NOTIFY ) );
        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strPositionEntryDirectorySms = request.getParameter( PARAMETER_POSITION_ENTRY_DIRECTORY_SMS );
        int nPositionEntryDirectorySms = WorkflowUtils.convertStringToInt( strPositionEntryDirectorySms );
        String strPositionEntryDirectoryEmail = request.getParameter( PARAMETER_POSITION_ENTRY_DIRECTORY_EMAIL );
        int nPositionEntryDirectoryEmail = WorkflowUtils.convertStringToInt( strPositionEntryDirectoryEmail );
        int nIdDirectory = WorkflowUtils.convertStringToInt( strIdDirectory );
        int nIdState = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_STATE ) );
        String emailValidation = request.getParameter( PARAMETER_EMAIL_VALIDATION );
        String strMessageValidation = request.getParameter( PARAMETER_MESSAGE_VALIDATION );
        String strLabelLink = request.getParameter( PARAMETER_LABEL_LINK );
        int nPeriodValidity = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_PERIOD_VALIDTY ) );

        String strError = WorkflowUtils.EMPTY_STRING;

        if ( ( request.getParameter( PARAMETER_APPLY ) == null ) && ( nNotify == DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            strError = FIELD_NOTIFY;
        }
        else if ( nIdDirectory == WorkflowUtils.CONSTANT_ID_NULL )
        {
            strError = FIELD_TASK_DIRECTORY;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectorySms == WorkflowUtils.CONSTANT_ID_NULL ) &&
                ( ( nNotify == 2 ) || ( nNotify == 3 ) ) )
        {
            strError = FIELD_TASK_ENTRY_DIRECTORY_SMS;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectoryEmail == WorkflowUtils.CONSTANT_ID_NULL ) &&
                ( ( nNotify == 1 ) || ( nNotify == 3 ) ) )
        {
            strError = FIELD_TASK_ENTRY_DIRECTORY_EMAIL;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( ( strSenderName == null ) || strSenderName.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = FIELD_SENDER_NAME;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( ( strSubject == null ) || strSubject.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = FIELD_SUBJECT;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( ( strMessage == null ) || strMessage.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = FIELD_MESSAGE;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) && ( nIdState == WorkflowUtils.CONSTANT_ID_NULL ) &&
                ( emailValidation != null ) )
        {
            strError = FIELD_STATE;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( ( strMessageValidation == null ) ||
                strMessageValidation.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) && ( emailValidation != null ) )
        {
            strError = FIELD_MESSAGE_VALIDATION;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( ( strLabelLink == null ) || strLabelLink.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) &&
                ( emailValidation != null ) )
        {
            strError = FIELD_LABEL_LINK;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( nPeriodValidity == WorkflowUtils.CONSTANT_ID_NULL ) && ( emailValidation != null ) )
        {
            strError = FIELD_LABEL_PERIOD_VALIDITY;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectorySms == nPositionEntryDirectoryEmail ) )
        {
            Object[] tabRequiredFields = 
                {
                    I18nService.getLocalizedString( FIELD_TASK_ENTRY_DIRECTORY_EMAIL, locale ),
                    I18nService.getLocalizedString( FIELD_TASK_ENTRY_DIRECTORY_SMS, locale )
                };

            return AdminMessageService.getMessageUrl( request, MESSAGE_EQUAL_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( this.getId(  ), plugin );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskNotifyDirectoryConfig(  );
            config.setIdTask( this.getId(  ) );
            bCreate = true;
        }

        config.setIdDirectory( DirectoryUtils.convertStringToInt( strIdDirectory ) );
        config.setPositionEntryDirectorySms( nPositionEntryDirectorySms );
        config.setPositionEntryDirectoryEmail( nPositionEntryDirectoryEmail );
        config.setMessage( strMessage );
        config.setSenderName( strSenderName );
        config.setSubject( strSubject );
        config.setEmailValidation( emailValidation != null );
        config.setIdStateAfterValidation( nIdState );
        config.setLabelLink( strLabelLink );
        config.setMessageValidation( strMessageValidation );
        config.setPeriodValidity( nPeriodValidity );

        if ( nNotify == 1 )
        {
            config.setNotifyByEmail( true );
            config.setNotifyBySms( false );
        }
        else if ( nNotify == 2 )
        {
            config.setNotifyByEmail( false );
            config.setNotifyBySms( true );
        }
        else if ( nNotify == 3 )
        {
            config.setNotifyByEmail( true );
            config.setNotifyBySms( true );
        }

        if ( bCreate )
        {
            TaskNotifyDirectoryConfigHome.create( config, plugin );
        }
        else
        {
            TaskNotifyDirectoryConfigHome.update( config, plugin );
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doValidateTask(int, java.lang.String, javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.ITaskListener#getDisplayConfigForm(fr.paris.lutece.plugins.workflow.business.Action, javax.servlet.http.HttpServletRequest)
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        String strAcceptEntryType = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE );
        String strRefuseEntryType = AppPropertiesService.getProperty( PROPERTY_REFUSE_DIRECTORY_TYPE );
        String[] strTabAcceptEntryType = strAcceptEntryType.split( COMMA );
        String[] strTabRefuseEntryType = strRefuseEntryType.split( COMMA );

        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        ReferenceList entryList = null;
        ReferenceList directoryList = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList taskReferenceListDirectory = new ReferenceList(  );
        taskReferenceListDirectory.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        if ( directoryList != null )
        {
            taskReferenceListDirectory.addAll( directoryList );
        }

        if ( ( config != null ) && ( config.getIdDirectory(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );
            entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
            entryFilter.setIsComment( EntryFilter.FILTER_FALSE );

            entryList = new ReferenceList(  );

            for ( IEntry entry : EntryHome.getEntryList( entryFilter, pluginDirectory ) )
            {
                for ( int i = 0; i < strTabAcceptEntryType.length; i++ )
                {
                    if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryType[i] ) )
                    {
                        entryList.addItem( entry.getPosition(  ),
                            String.valueOf( entry.getPosition(  ) ) + " (" +
                            I18nService.getLocalizedString( LABEL_REFERENCE_DIRECTORY, locale ) + entry.getTitle(  ) +
                            " - " +
                            I18nService.getLocalizedString( entry.getEntryType(  ).getTitleI18nKey(  ), locale ) + ")" );
                    }
                }
            }
        }

        if ( ( config != null ) && ( config.getIdDirectory(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );

            List<IEntry> entryListFreemarker = new ArrayList<IEntry>(  );

            for ( IEntry entry : EntryHome.getEntryList( entryFilter, pluginDirectory ) )
            {
                boolean tmp = true;

                for ( int i = 0; i < strTabRefuseEntryType.length; i++ )
                {
                    if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabRefuseEntryType[i] ) )
                    {
                        tmp = false;
                    }
                }

                if ( tmp )
                {
                    entryListFreemarker.add( entry );
                }
            }

            model.put( MARK_ENTRY_LIST_FREEMARKER, entryListFreemarker );
        }

        ReferenceList taskReferenceListEntry = new ReferenceList(  );
        taskReferenceListEntry.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        if ( entryList != null )
        {
            taskReferenceListEntry.addAll( entryList );
        }

        if ( config == null )
        {
            config = new TaskNotifyDirectoryConfig(  );
            config.setIdDirectory( DirectoryUtils.CONSTANT_ID_NULL );
            config.setPositionEntryDirectorySms( DirectoryUtils.CONSTANT_ID_NULL );
            config.setPositionEntryDirectoryEmail( DirectoryUtils.CONSTANT_ID_NULL );
            config.setPeriodValidity( DirectoryUtils.CONSTANT_ID_NULL );
        }

        Workflow workflow = ( ActionHome.findByPrimaryKey( this.getAction(  ).getId(  ), pluginWorkflow ) ).getWorkflow(  );
        StateFilter stateFilter = new StateFilter(  );
        stateFilter.setIdWorkflow( workflow.getId(  ) );

        List<State> listState = StateHome.getListStateByFilter( stateFilter, pluginWorkflow );
        ReferenceList referencelistState = new ReferenceList(  );
        referencelistState.addItem( -1, DirectoryUtils.EMPTY_STRING );

        ReferenceList referencelistStateTmp = ReferenceList.convert( listState, ID, NAME, true );
        referencelistState.addAll( referencelistStateTmp );

        String strDefaultSenderName = AppPropertiesService.getProperty( PROPERTY_NOTIFY_MAIL_DEFAULT_SENDER_NAME );
        model.put( MARK_CONFIG, config );
        model.put( MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        model.put( MARK_DIRECTORY_ENTRY_LIST, taskReferenceListEntry );
        model.put( MARK_DIRECTORY_LIST, taskReferenceListDirectory );
        model.put( MARK_STATE_LIST, referencelistState );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskForm(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.ITaskListener#getDisplayTaskInformation(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.plugins.workflow.business.Action)
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.ITaskListener#processTask(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.plugins.workflow.business.Action)
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        String strEmail = DirectoryUtils.EMPTY_STRING;
        String strSms = DirectoryUtils.EMPTY_STRING;
        String strServerSms = AppPropertiesService.getProperty( PROPERTY_SERVER_SMS );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory, plugin );
        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( ( config != null ) && ( resourceHistory != null ) &&
                resourceHistory.getResourceType(  ).equals( Record.WORKFLOW_RESOURCE_TYPE ) )
        {
            //Record
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
            Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                    pluginDirectory );
            record.setDirectory( directory );

            //RecordField Email
            EntryFilter entryFilterEmail = new EntryFilter(  );
            entryFilterEmail.setPosition( config.getPositionEntryDirectoryEmail(  ) );
            entryFilterEmail.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

            List<IEntry> entrylistEmail = EntryHome.getEntryList( entryFilterEmail, pluginDirectory );

            if ( ( entrylistEmail != null ) && !entrylistEmail.isEmpty(  ) )
            {
                IEntry entryEmail = entrylistEmail.get( 0 );
                RecordFieldFilter recordFieldFilterEmail = new RecordFieldFilter(  );
                recordFieldFilterEmail.setIdDirectory( directory.getIdDirectory(  ) );
                recordFieldFilterEmail.setIdEntry( entryEmail.getIdEntry(  ) );
                recordFieldFilterEmail.setIdRecord( record.getIdRecord(  ) );

                List<RecordField> listRecordFieldEmail = RecordFieldHome.getRecordFieldList( recordFieldFilterEmail,
                        pluginDirectory );

                if ( ( listRecordFieldEmail != null ) && !listRecordFieldEmail.isEmpty(  ) &&
                        ( listRecordFieldEmail.get( 0 ) != null ) )
                {
                    RecordField recordFieldEmail = listRecordFieldEmail.get( 0 );
                    strEmail = recordFieldEmail.getValue(  );

                    if ( recordFieldEmail.getField(  ) != null )
                    {
                        strEmail = recordFieldEmail.getField(  ).getTitle(  );
                    }
                }
            }

            //RecordField Sms
            EntryFilter entryFilterSms = new EntryFilter(  );
            entryFilterSms.setPosition( config.getPositionEntryDirectorySms(  ) );
            entryFilterSms.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

            List<IEntry> entrylistSms = EntryHome.getEntryList( entryFilterSms, pluginDirectory );

            if ( ( entrylistSms != null ) && !entrylistSms.isEmpty(  ) )
            {
                IEntry entrySms = entrylistSms.get( 0 );

                RecordFieldFilter recordFieldFilterSms = new RecordFieldFilter(  );
                recordFieldFilterSms.setIdDirectory( directory.getIdDirectory(  ) );
                recordFieldFilterSms.setIdEntry( entrySms.getIdEntry(  ) );
                recordFieldFilterSms.setIdRecord( record.getIdRecord(  ) );

                List<RecordField> listRecordFieldSms = RecordFieldHome.getRecordFieldList( recordFieldFilterSms,
                        pluginDirectory );

                if ( ( listRecordFieldSms != null ) && !listRecordFieldSms.isEmpty(  ) &&
                        ( listRecordFieldSms.get( 0 ) != null ) )
                {
                    RecordField recordFieldSms = listRecordFieldSms.get( 0 );
                    strSms = recordFieldSms.getValue(  );

                    if ( recordFieldSms.getField(  ) != null )
                    {
                        strSms = recordFieldSms.getField(  ).getTitle(  );
                    }
                }
            }
            
            String strSenderEmail = MailService.getNoReplyEmail(  );

            Map<String, String> model = new HashMap<String, String>(  );
            model.put( MARK_MESSAGE, config.getMessage(  ) );

            //Directory directory = DirectoryHome.findByPrimaryKey( config.getIdDirectory(  ), pluginDirectory );
            if ( directory != null )
            {
                model.put( MARK_DIRECTORY_TITLE, directory.getTitle(  ) );
                model.put( MARK_DIRECTORY_DESCRIPTION, directory.getDescription(  ) );
            }

            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdRecord( record.getIdRecord(  ) );

            List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter,
                    pluginDirectory );

            for ( RecordField recordField : listRecordField )
            {
            	String value = recordField.getEntry(  ).convertRecordFieldValueToString( recordField, request.getLocale(  ), false, false );
            	if ( isEntryTypeRefused( recordField.getEntry(  ) ) )
            	{
            		continue;
            	}
            	else if ( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation && 
                		!recordField.getField(  ).getTitle(  ).equals( EntryTypeGeolocation.CONSTANT_ADDRESS ) )
                {
                	continue;
                }
            	else if ( recordField.getField(  ) != null && 
                		!( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation ) )
                {
                    recordFieldFilter.setIdEntry( recordField.getEntry(  ).getIdEntry(  ) );
                    listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

                    if ( ( listRecordField.get( 0 ) != null ) && ( listRecordField.get( 0 ).getField(  ) != null ) &&
                            ( listRecordField.get( 0 ).getField(  ).getTitle(  ) != null ) )
                    {
                        value = listRecordField.get( 0 ).getField(  ).getTitle(  );
                    }
                }

                recordField.setEntry( EntryHome.findByPrimaryKey( recordField.getEntry(  ).getIdEntry(  ),
                        pluginDirectory ) );
                model.put( MARK_POSITION + String.valueOf( recordField.getEntry(  ).getPosition(  ) ), value );
            }

            if ( ( record.getDirectory(  ).getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                    WorkflowService.getInstance(  ).isAvailable(  ) )
            {
                State state = WorkflowService.getInstance(  )
                                             .getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        record.getDirectory(  ).getIdWorkflow(  ), null );
                model.put( MARK_STATUS, state.getName(  ) );
            }

            //Generate key
            String link = DirectoryUtils.EMPTY_STRING;
            String linkHtml = DirectoryUtils.EMPTY_STRING;

            if ( config.isEmailValidation(  ) )
            {
                ResourceKey resourceKey = new ResourceKey(  );
                UUID key = java.util.UUID.randomUUID(  );
                resourceKey.setKey( key.toString(  ) );
                resourceKey.setIdResource( record.getIdRecord(  ) );
                resourceKey.setResourceType( resourceHistory.getResourceType(  ) );
                resourceKey.setIdTask( this.getId(  ) );

                Calendar calendar = GregorianCalendar.getInstance(  );
                calendar.add( Calendar.DAY_OF_MONTH, config.getPeriodValidity(  ) );
                resourceKey.setDateExpiry( new Timestamp( calendar.getTimeInMillis(  ) ) );
                ResourceKeyHome.create( resourceKey, pluginDirectory );
                link = AppPropertiesService.getProperty( PROPERTY_URL ) + "/" + AppPathService.getPortalUrl(  ) +
                    "?page=workflow&key=" + key.toString(  );
                linkHtml = HTML_LINK_OPEN_1 + link + HTML_LINK_OPEN_2 + config.getLabelLink(  ) + HTML_LINK_CLOSE;

                Map<String, Object> modelTmp = new HashMap<String, Object>(  );
                modelTmp.put( MARK_LINK, link );
                linkHtml = AppTemplateService.getTemplateFromStringFtl( linkHtml, locale, modelTmp ).getHtml(  );
            }

            model.put( MARK_LINK, linkHtml );

            HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                        TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml(  ), locale, model );

            if ( config.isNotifyByEmail(  ) && ( strEmail != null ) &&
                    !DirectoryUtils.EMPTY_STRING.equals( strEmail ) )
            {
                // Build the mail message
                MailService.sendMailHtml( strEmail, config.getSenderName(  ), strSenderEmail,
                    AppTemplateService.getTemplateFromStringFtl( config.getSubject(  ), locale, model ).getHtml(  ),
                    t.getHtml(  ) );
            }

            if ( config.isNotifyBySms(  ) && ( strSms != null ) && !DirectoryUtils.EMPTY_STRING.equals( strSms ) )
            {
                // Build the sms message
                HtmlTemplate tSMS = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                            TEMPLATE_TASK_NOTIFY_SMS, locale, model ).getHtml(  ), locale, model );
                MailService.sendMailHtml( strSms + strServerSms, config.getSenderName(  ), strSenderEmail,
                    AppTemplateService.getTemplateFromStringFtl( config.getSubject(  ), locale, model ).getHtml(  ),
                    tSMS.getHtml(  ) );
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveConfig()
     */
    public void doRemoveConfig( Plugin plugin )
    {
        TaskNotifyDirectoryConfigHome.remove( this.getId(  ), plugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isConfigRequire()
     */
    public boolean isConfigRequire(  )
    {
        // TODO Auto-generated method stub
        return true;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isFormTaskRequire()
     */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveTaskInformation(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskInformationXml(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTitle(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTitle( Plugin plugin, Locale locale )
    {
        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( config != null )
        {
            return config.getSubject(  );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskFormEntries(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isTaskForActionAutomatic()
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
    }

    /**
     * Check if the entry is refused (values set in the workflow-notifydirectory.properties)
     * @param entry the entry
     * @return true if it is refused, false otherwise
     */
    private boolean isEntryTypeRefused( IEntry entry )
    {
    	boolean bIsRefused = false;
    	
        if ( entry != null )
        {
        	String strRefuseEntryType = AppPropertiesService.getProperty( PROPERTY_REFUSE_DIRECTORY_TYPE );
            String[] strTabRefuseEntryType = strRefuseEntryType.split( COMMA );
            
        	for ( int i = 0; i < strTabRefuseEntryType.length; i++ )
            {
            	if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabRefuseEntryType[i] ) )
                {
                	bIsRefused = true;
                	break;
                }
            }
        }
        
        return bIsRefused;
    }
}
