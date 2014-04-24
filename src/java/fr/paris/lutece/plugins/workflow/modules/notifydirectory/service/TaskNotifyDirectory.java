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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.service;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.mail.FileAttachment;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskNotifyDirectory
 *
 */
public class TaskNotifyDirectory extends SimpleTask
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_mail.html";
    private static final String TEMPLATE_TASK_NOTIFY_SMS = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_sms.html";

    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( TaskNotifyDirectoryConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyDirectoryConfigService;
    @Inject
    private INotifyDirectoryService _notifyDirectoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( this.getId(  ) );

        if ( ( config != null ) && ( resourceHistory != null ) &&
                Record.WORKFLOW_RESOURCE_TYPE.equals( resourceHistory.getResourceType(  ) ) )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

            // Record
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

            if ( record != null )
            {
                Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                        pluginDirectory );

                if ( directory != null )
                {
                    record.setDirectory( directory );

                    // Get email
                    String strEmail = _notifyDirectoryService.getEmail( config, record.getIdRecord(  ),
                            directory.getIdDirectory(  ) );
                    String strEmailContent = StringUtils.EMPTY;

                    // Get Sms
                    String strSms = _notifyDirectoryService.getSMSPhoneNumber( config, record.getIdRecord(  ),
                            directory.getIdDirectory(  ) );
                    String strSmsContent = StringUtils.EMPTY;

                    //Get Files Attachment
                    List<FileAttachment> listFileAttachment = _notifyDirectoryService.getFilesAttachment( config,
                            record.getIdRecord(  ), directory.getIdDirectory(  ) );

                    // Get sender email
                    String strSenderEmail = MailService.getNoReplyEmail(  );

                    Map<String, Object> model = _notifyDirectoryService.fillModel( config, resourceHistory, record,
                            directory, request, getAction(  ).getId(  ), nIdResourceHistory );

                    String strSubject = AppTemplateService.getTemplateFromStringFtl( config.getSubject(  ), locale,
                            model ).getHtml(  );

                    boolean bIsNotifyByEmail = config.isNotifyByEmail(  ) && StringUtils.isNotBlank( strEmail );
                    boolean bIsNotifyBySms = config.isNotifyBySms(  ) && StringUtils.isNotBlank( strSms );
                    boolean bIsNotifyByMailingList = config.isNotifyByMailingList(  );
                    boolean bHasRecipients = ( StringUtils.isNotBlank( config.getRecipientsBcc(  ) ) ||
                        StringUtils.isNotBlank( config.getRecipientsCc(  ) ) );

                    if ( bIsNotifyByEmail || bIsNotifyByMailingList || bHasRecipients )
                    {
                        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                                    TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml(  ), locale, model );
                        strEmailContent = t.getHtml(  );
                    }

                    if ( bIsNotifyBySms )
                    {
                        HtmlTemplate tSMS = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                                    TEMPLATE_TASK_NOTIFY_SMS, locale, model ).getHtml(  ), locale, model );
                        strSmsContent = tSMS.toString(  );
                    }

                    _notifyDirectoryService.sendMessage( config, strEmail, strSms, strSenderEmail, strSubject,
                        strEmailContent, strSmsContent, listFileAttachment );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        _taskNotifyDirectoryConfigService.remove( this.getId(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( this.getId(  ) );

        if ( config != null )
        {
            return config.getSubject(  );
        }

        return StringUtils.EMPTY;
    }
}
