/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.taskinfo;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.INotifyDirectoryService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.NotifyDirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.TaskNotifyDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.service.taskinfo.AbstractTaskInfoProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * NotifyDirectoryTaskInfoProvider
 *
 */
public class NotifyDirectoryTaskInfoProvider extends AbstractTaskInfoProvider
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_MAIL = "admin/plugins/workflow/modules/notifydirectory/task_notify_directory_mail.html";
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private INotifyDirectoryService _notifyDirectoryService;
    @Inject
    private ITaskService _taskService;
    @Inject
    @Named( TaskNotifyDirectoryConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyDirectoryConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName(  )
    {
        return NotifyDirectoryPlugin.PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request )
    {
        String strTaskResourceInfo = StringUtils.EMPTY;

        Locale locale = _notifyDirectoryService.getLocale( request );

        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( nIdTask );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
        ITask task = _taskService.findByPrimaryKey( nIdTask, locale );

        if ( record != null )
        {
            Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                    pluginDirectory );

            Map<String, Object> model = _notifyDirectoryService.fillModel( config, resourceHistory, record, directory,
                    request, task.getAction(  ).getId(  ), nIdHistory );

            HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                        TEMPLATE_TASK_NOTIFY_MAIL, locale, model ).getHtml(  ), locale, model );

            strTaskResourceInfo = t.getHtml(  );
        }

        return strTaskResourceInfo;
    }
}
