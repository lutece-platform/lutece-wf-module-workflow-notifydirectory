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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.NotifyDirectoryService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.TaskNotifyDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.utils.constants.NotifyDirectoryConstants;
import fr.paris.lutece.plugins.workflow.service.WorkflowWebService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskNotifyDirectory
 *
 */
public class TaskNotifyDirectory extends Task
{
    // Templates
    private static final String TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_config.html";
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_mail.html";
    private static final String TEMPLATE_TASK_NOTIFY_SMS = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_sms.html";

    /**
     * {@inheritDoc}
     */
    public void init(  )
    {
    }

    /**
     * {@inheritDoc}
     */
    public String doSaveConfig( HttpServletRequest request, Locale locale, Plugin plugin )
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
        String emailValidation = request.getParameter( NotifyDirectoryConstants.PARAMETER_EMAIL_VALIDATION );
        String strMessageValidation = request.getParameter( NotifyDirectoryConstants.PARAMETER_MESSAGE_VALIDATION );
        String strLabelLink = request.getParameter( NotifyDirectoryConstants.PARAMETER_LABEL_LINK );
        int nPeriodValidity = DirectoryUtils.convertStringToInt( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_PERIOD_VALIDTY ) );
        boolean bIsNotifyByUserGuid = StringUtils.isNotBlank( request.getParameter( 
                    NotifyDirectoryConstants.PARAMETER_IS_NOTIFY_BY_USER_GUID ) );

        String strError = StringUtils.EMPTY;

        if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nNotify == DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            strError = NotifyDirectoryConstants.FIELD_NOTIFY;
        }
        else if ( nIdDirectory == WorkflowUtils.CONSTANT_ID_NULL )
        {
            strError = NotifyDirectoryConstants.FIELD_TASK_DIRECTORY;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectorySms == WorkflowUtils.CONSTANT_ID_NULL ) &&
                ( ( nNotify == NotificationTypeEnum.SMS.getId(  ) ) ||
                ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) ) ) )
        {
            strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_SMS;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectoryEmail == WorkflowUtils.CONSTANT_ID_NULL ) && !bIsNotifyByUserGuid &&
                ( ( nNotify == NotificationTypeEnum.EMAIL.getId(  ) ) ||
                ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) ) ) )
        {
            strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_EMAIL;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( ( strSenderName == null ) || strSenderName.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = NotifyDirectoryConstants.FIELD_SENDER_NAME;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( ( strSubject == null ) || strSubject.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = NotifyDirectoryConstants.FIELD_SUBJECT;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( ( strMessage == null ) || strMessage.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = NotifyDirectoryConstants.FIELD_MESSAGE;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nIdState == WorkflowUtils.CONSTANT_ID_NULL ) && ( emailValidation != null ) )
        {
            strError = NotifyDirectoryConstants.FIELD_STATE;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( ( strMessageValidation == null ) ||
                strMessageValidation.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) && ( emailValidation != null ) )
        {
            strError = NotifyDirectoryConstants.FIELD_MESSAGE_VALIDATION;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( ( strLabelLink == null ) || strLabelLink.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) &&
                ( emailValidation != null ) )
        {
            strError = NotifyDirectoryConstants.FIELD_LABEL_LINK;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nPeriodValidity == WorkflowUtils.CONSTANT_ID_NULL ) && ( emailValidation != null ) )
        {
            strError = NotifyDirectoryConstants.FIELD_LABEL_PERIOD_VALIDITY;
        }
        else if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectoryUserGuid == WorkflowUtils.CONSTANT_ID_NULL ) && bIsNotifyByUserGuid )
        {
            strError = NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_USER_GUID;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, NotifyDirectoryConstants.MESSAGE_MANDATORY_FIELD,
                tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( ( request.getParameter( NotifyDirectoryConstants.PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectorySms == nPositionEntryDirectoryEmail ) && !bIsNotifyByUserGuid )
        {
            Object[] tabRequiredFields = 
                {
                    I18nService.getLocalizedString( NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_EMAIL, locale ),
                    I18nService.getLocalizedString( NotifyDirectoryConstants.FIELD_TASK_ENTRY_DIRECTORY_SMS, locale ),
                };

            return AdminMessageService.getMessageUrl( request, NotifyDirectoryConstants.MESSAGE_EQUAL_FIELD,
                tabRequiredFields, AdminMessage.TYPE_STOP );
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
        config.setPositionEntryDirectoryUserGuid( nPositionEntryDirectoryUserGuid );
        config.setMessage( strMessage );
        config.setSenderName( strSenderName );
        config.setSubject( strSubject );
        config.setEmailValidation( emailValidation != null );
        config.setIdStateAfterValidation( nIdState );
        config.setLabelLink( strLabelLink );
        config.setMessageValidation( strMessageValidation );
        config.setPeriodValidity( nPeriodValidity );
        config.setNotifyByUserGuid( bIsNotifyByUserGuid );

        if ( nNotify == NotificationTypeEnum.EMAIL.getId(  ) )
        {
            config.setNotifyByEmail( true );
            config.setNotifyBySms( false );
        }
        else if ( nNotify == NotificationTypeEnum.SMS.getId(  ) )
        {
            config.setNotifyByEmail( false );
            config.setNotifyBySms( true );
        }
        else if ( nNotify == NotificationTypeEnum.EMAIL_SMS.getId(  ) )
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

    /**
     * {@inheritDoc}
     */
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale )
    {
        NotifyDirectoryService notifyDirectoryService = NotifyDirectoryService.getService(  );
        TaskNotifyDirectoryConfigService configService = TaskNotifyDirectoryConfigService.getService(  );

        String strDefaultSenderName = AppPropertiesService.getProperty( NotifyDirectoryConstants.PROPERTY_NOTIFY_MAIL_DEFAULT_SENDER_NAME );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( NotifyDirectoryConstants.MARK_CONFIG, configService.findByPrimaryKey( getId(  ), plugin ) );
        model.put( NotifyDirectoryConstants.MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_EMAIL_SMS,
            notifyDirectoryService.getListEntriesEmailSMS( getId(  ), locale ) );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_FREEMARKER,
            notifyDirectoryService.getListEntriesFreemarker( getId(  ) ) );
        model.put( NotifyDirectoryConstants.MARK_DIRECTORY_LIST, notifyDirectoryService.getListDirectories(  ) );
        model.put( NotifyDirectoryConstants.MARK_STATE_LIST,
            notifyDirectoryService.getListStates( getAction(  ).getId(  ) ) );
        model.put( NotifyDirectoryConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( NotifyDirectoryConstants.MARK_LOCALE, request.getLocale(  ) );
        model.put( NotifyDirectoryConstants.MARK_IS_USER_ATTRIBUTE_WS_ACTIVE,
            WorkflowWebService.isUserAttributeWSActive(  ) );
        model.put( NotifyDirectoryConstants.MARK_LIST_ENTRIES_USER_GUID,
            notifyDirectoryService.getListEntriesUserGuid( getId(  ), locale ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_DIRECTORY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory, plugin );
        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( this.getId(  ), plugin );

        if ( ( config != null ) && ( resourceHistory != null ) &&
                Record.WORKFLOW_RESOURCE_TYPE.equals( resourceHistory.getResourceType(  ) ) )
        {
            NotifyDirectoryService notifyDirectoryService = NotifyDirectoryService.getService(  );
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

            // Record
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
            Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                    pluginDirectory );
            record.setDirectory( directory );

            // Get email
            String strEmail = notifyDirectoryService.getEmail( config, record.getIdRecord(  ),
                    directory.getIdDirectory(  ) );

            // Get Sms
            String strSms = notifyDirectoryService.getSMSPhoneNumber( config, record.getIdRecord(  ),
                    directory.getIdDirectory(  ) );
            String strServerSms = AppPropertiesService.getProperty( NotifyDirectoryConstants.PROPERTY_SERVER_SMS );

            // Get sender email
            String strSenderEmail = MailService.getNoReplyEmail(  );

            Map<String, String> model = notifyDirectoryService.fillModel( config, resourceHistory, record, directory,
                    request );

            HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                        TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml(  ), locale, model );

            if ( config.isNotifyByEmail(  ) && StringUtils.isNotBlank( strEmail ) )
            {
                // Build the mail message
                MailService.sendMailHtml( strEmail, config.getSenderName(  ), strSenderEmail,
                    AppTemplateService.getTemplateFromStringFtl( config.getSubject(  ), locale, model ).getHtml(  ),
                    t.getHtml(  ) );
            }

            if ( config.isNotifyBySms(  ) && StringUtils.isNotBlank( strSms ) )
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

    /**
     * {@inheritDoc}
     */
    public void doRemoveConfig( Plugin plugin )
    {
        TaskNotifyDirectoryConfigHome.remove( this.getId(  ), plugin );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConfigRequire(  )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
    }

    /**
     * {@inheritDoc}
     */
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
    }
}
