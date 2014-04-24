/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.web;

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.NotificationTypeEnum;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.INotifyDirectoryService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.TaskNotifyDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.utils.constants.NotifyDirectoryConstants;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.service.security.IWorkflowUserAttributesManager;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * INotifyDirectoryTaskComponent
 *
 */
public class NotifyDirectoryTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_config.html";

    // SERVICES
    @Inject
    @Named( TaskNotifyDirectoryConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyDirectoryConfigService;
    @Inject
    private INotifyDirectoryService _notifyDirectoryService;
    @Inject
    private IWorkflowUserAttributesManager _userAttributesManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        int nNotify = DirectoryUtils.convertStringToInt( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_NOTIFY ) );
        String strSenderName = request.getParameter( NotifyDirectoryConstants.PARAMETER_SENDER_NAME );
        String strSubject = request.getParameter( NotifyDirectoryConstants.PARAMETER_SUBJECT );
        String strMessage = request.getParameter( NotifyDirectoryConstants.PARAMETER_MESSAGE );
        String strIdDirectory = request.getParameter( NotifyDirectoryConstants.PARAMETER_ID_DIRECTORY );
        String strPositionEntryDirectorySms = request.getParameter( NotifyDirectoryConstants.PARAMETER_POSITION_ENTRY_DIRECTORY_SMS );
        int nPositionEntryDirectorySms = WorkflowUtils.convertStringToInt( strPositionEntryDirectorySms );
        String strPositionEntryDirectoryEmail = request.getParameter( NotifyDirectoryConstants.PARAMETER_POSITION_ENTRY_DIRECTORY_EMAIL );
        int nPositionEntryDirectoryEmail = WorkflowUtils.convertStringToInt( strPositionEntryDirectoryEmail );
        String strPositionEntryDirectoryUserGuid = request.getParameter( NotifyDirectoryConstants.PARAMETER_POSITION_ENTRY_DIRECTORY_USER_GUID );
        int nPositionEntryDirectoryUserGuid = WorkflowUtils.convertStringToInt( strPositionEntryDirectoryUserGuid );
        int nIdDirectory = WorkflowUtils.convertStringToInt( strIdDirectory );
        int nIdState = DirectoryUtils.convertStringToInt( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_ID_STATE ) );
        String strEmailValidation = request.getParameter( NotifyDirectoryConstants.PARAMETER_EMAIL_VALIDATION );
        String strMessageValidation = request.getParameter( NotifyDirectoryConstants.PARAMETER_MESSAGE_VALIDATION );
        String strLabelLink = request.getParameter( NotifyDirectoryConstants.PARAMETER_LABEL_LINK );
        int nPeriodValidity = DirectoryUtils.convertStringToInt( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_PERIOD_VALIDTY ) );
        boolean bIsNotifyByUserGuid = StringUtils.isNotBlank( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_IS_NOTIFY_BY_USER_GUID ) );
        String strRecipientsCc = request.getParameter( NotifyDirectoryConstants.PARAMETER_RECIPIENTS_CC );
        String strRecipientsBcc = request.getParameter( NotifyDirectoryConstants.PARAMETER_RECIPIENTS_BCC );
        int nIdMailingList = DirectoryUtils.convertStringToInt( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_ID_MAILING_LIST ) );
        String strViewRecord = request.getParameter( NotifyDirectoryConstants.PARAMETER_VIEW_RECORD );
        String strLabelLinkViewRecord = request.getParameter( NotifyDirectoryConstants.PARAMETER_LABEL_LINK_VIEW_RECORD );
        String strApply = request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY );
        String strError = StringUtils.EMPTY;
        String[] tabSelectedPositionEntryFile = request.getParameterValues( NotifyDirectoryConstants.PARAMETER_LIST_POSITION_ENTRY_FILE_CHECKED );

        if ( StringUtils.isBlank( strApply ) )
        {
            if ( nIdDirectory == WorkflowUtils.CONSTANT_ID_NULL )
            {
                strError = NotifyDirectoryConstants.FIELD_TASK_DIRECTORY;
            }
            else if ( nNotify == DirectoryUtils.CONSTANT_ID_NULL )
            {
                strError = NotifyDirectoryConstants.FIELD_NOTIFY;
            }
            else if ( ( nPositionEntryDirectorySms == WorkflowUtils.CONSTANT_ID_NULL ) &&
                    ( ( nNotify == NotificationTypeEnum.SMS.getId(  ) ) ||
                    ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) ) ) )
            {
                strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_SMS;
            }
            else if ( ( nPositionEntryDirectoryEmail == WorkflowUtils.CONSTANT_ID_NULL ) && !bIsNotifyByUserGuid &&
                    ( ( nNotify == NotificationTypeEnum.EMAIL.getId(  ) ) ||
                    ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) ) ) )
            {
                strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_EMAIL;
            }
            else if ( StringUtils.isBlank( strSenderName ) )
            {
                strError = NotifyDirectoryConstants.FIELD_SENDER_NAME;
            }
            else if ( StringUtils.isBlank( strSubject ) )
            {
                strError = NotifyDirectoryConstants.FIELD_SUBJECT;
            }
            else if ( StringUtils.isBlank( strMessage ) )
            {
                strError = NotifyDirectoryConstants.FIELD_MESSAGE;
            }
            else if ( ( nPositionEntryDirectoryUserGuid == WorkflowUtils.CONSTANT_ID_NULL ) && bIsNotifyByUserGuid )
            {
                strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_USER_GUID;
            }
            else if ( ( nNotify == NotificationTypeEnum.MAILING_LIST.getId(  ) ) &&
                    ( nIdMailingList == WorkflowUtils.CONSTANT_ID_NULL ) )
            {
                strError = NotifyDirectoryConstants.FIELD_MAILING_LIST;
            }
            else if ( StringUtils.isNotBlank( strEmailValidation ) )
            {
                if ( nIdState == WorkflowUtils.CONSTANT_ID_NULL )
                {
                    strError = NotifyDirectoryConstants.FIELD_STATE;
                }
                else if ( StringUtils.isBlank( strMessageValidation ) )
                {
                    strError = NotifyDirectoryConstants.FIELD_MESSAGE_VALIDATION;
                }
                else if ( StringUtils.isBlank( strLabelLink ) )
                {
                    strError = NotifyDirectoryConstants.FIELD_LABEL_LINK;
                }
                else if ( nPeriodValidity == WorkflowUtils.CONSTANT_ID_NULL )
                {
                    strError = NotifyDirectoryConstants.FIELD_LABEL_PERIOD_VALIDITY;
                }
            }
            else if ( StringUtils.isNotBlank( strViewRecord ) && StringUtils.isBlank( strLabelLinkViewRecord ) )
            {
                strError = NotifyDirectoryConstants.FIELD_LABEL_LINK_VIEW_RECORD;
            }
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, NotifyDirectoryConstants.MESSAGE_MANDATORY_FIELD,
                tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( StringUtils.isBlank( strApply ) && ( nPositionEntryDirectorySms == nPositionEntryDirectoryEmail ) &&
                !bIsNotifyByUserGuid && ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) ) )
        {
            Object[] tabRequiredFields = 
                {
                    I18nService.getLocalizedString( NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_EMAIL, locale ),
                    I18nService.getLocalizedString( NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_SMS, locale ),
                };

            return AdminMessageService.getMessageUrl( request, NotifyDirectoryConstants.MESSAGE_EQUAL_FIELD,
                tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( task.getId(  ) );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskNotifyDirectoryConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
        }

        config.setIdDirectory( DirectoryUtils.convertStringToInt( strIdDirectory ) );
        config.setPositionEntryDirectorySms( nPositionEntryDirectorySms );
        config.setPositionEntryDirectoryEmail( nPositionEntryDirectoryEmail );
        config.setPositionEntryDirectoryUserGuid( nPositionEntryDirectoryUserGuid );
        config.setMessage( strMessage );
        config.setSenderName( strSenderName );
        config.setSubject( strSubject );
        config.setEmailValidation( strEmailValidation != null );
        config.setIdStateAfterValidation( nIdState );
        config.setLabelLink( strLabelLink );
        config.setMessageValidation( strMessageValidation );
        config.setPeriodValidity( nPeriodValidity );
        config.setRecipientsCc( StringUtils.isNotEmpty( strRecipientsCc ) ? strRecipientsCc : StringUtils.EMPTY );
        config.setRecipientsBcc( StringUtils.isNotEmpty( strRecipientsBcc ) ? strRecipientsBcc : StringUtils.EMPTY );
        config.setIdMailingList( nIdMailingList );
        config.setViewRecord( strViewRecord != null );
        config.setLabelLinkViewRecord( strLabelLinkViewRecord );

        config.setNotifyByUserGuid( bIsNotifyByUserGuid );

        if ( nNotify == NotificationTypeEnum.EMAIL.getId(  ) )
        {
            config.setNotifyByEmail( true );
            config.setNotifyBySms( false );
            config.setNotifyByMailingList( false );
        }
        else if ( nNotify == NotificationTypeEnum.SMS.getId(  ) )
        {
            config.setNotifyByEmail( false );
            config.setNotifyBySms( true );
            config.setNotifyByMailingList( false );
        }
        else if ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) )
        {
            config.setNotifyByEmail( true );
            config.setNotifyBySms( true );
            config.setNotifyByMailingList( false );
        }
        else if ( nNotify == NotificationTypeEnum.MAILING_LIST.getId(  ) )
        {
            config.setNotifyByEmail( false );
            config.setNotifyBySms( false );
            config.setNotifyByMailingList( true );
        }

        if ( ( tabSelectedPositionEntryFile != null ) && ( tabSelectedPositionEntryFile.length > 0 ) )
        {
            List<Integer> listSelectedPositionEntryFile = new ArrayList<Integer>(  );

            for ( int i = 0; i < tabSelectedPositionEntryFile.length; i++ )
            {
                listSelectedPositionEntryFile.add( WorkflowUtils.convertStringToInt( tabSelectedPositionEntryFile[i] ) );
            }

            config.setListPositionEntryFile( listSelectedPositionEntryFile );
        }
        else
        {
            config.setListPositionEntryFile( null );
        }

        if ( bCreate )
        {
            _taskNotifyDirectoryConfigService.create( config );
        }
        else
        {
            _taskNotifyDirectoryConfigService.update( config );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( task.getId(  ) );

        String strDefaultSenderName = AppPropertiesService.getProperty( NotifyDirectoryConstants.PROPERTY_NOTIFY_MAIL_DEFAULT_SENDER_NAME );
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( NotifyDirectoryConstants.MARK_CONFIG, config );
        model.put( NotifyDirectoryConstants.MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_EMAIL_SMS,
            _notifyDirectoryService.getListEntriesEmailSMS( task.getId(  ), locale ) );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_FREEMARKER,
            _notifyDirectoryService.getListEntriesFreemarker( task.getId(  ) ) );
        model.put( NotifyDirectoryConstants.MARK_DIRECTORY_LIST, _notifyDirectoryService.getListDirectories(  ) );
        model.put( NotifyDirectoryConstants.MARK_STATE_LIST,
            _notifyDirectoryService.getListStates( task.getAction(  ).getId(  ) ) );
        model.put( NotifyDirectoryConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( NotifyDirectoryConstants.MARK_LOCALE, request.getLocale(  ) );
        model.put( NotifyDirectoryConstants.MARK_IS_USER_ATTRIBUTE_WS_ACTIVE, _userAttributesManager.isEnabled(  ) );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_USER_GUID,
            _notifyDirectoryService.getListEntriesUserGuid( task.getId(  ), locale ) );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_FILE,
            _notifyDirectoryService.getListEntriesFile( task.getId(  ), locale ) );

        if ( config != null )
        {
            model.put( NotifyDirectoryConstants.MARK_LIST_POSITION_ENTRY_FILE_CHECKED,
                config.getListPositionEntryFile(  ) );
        }

        model.put( NotifyDirectoryConstants.MARK_MAILING_LIST, _notifyDirectoryService.getMailingList( request ) );
        model.put( NotifyDirectoryConstants.MARK_PLUGIN_WORKFLOW, pluginWorkflow );
        model.put( NotifyDirectoryConstants.MARK_TASKS_LIST, _notifyDirectoryService.getListBelowTasks( task, locale ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
