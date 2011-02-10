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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.web;

import fr.paris.lutece.plugins.workflow.business.ActionHome;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflow;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowHome;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.business.task.ITask;
import fr.paris.lutece.plugins.workflow.business.task.TaskHome;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKey;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKeyHome;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfigHome;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.service.WorkflowService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.sql.Timestamp;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class manages Form page.
 *
 */
public class NotifyDirectoryApp implements XPageApplication
{
    // properties for page titles and path label
    private static final String PROPERTY_XPAGE_PAGETITLE = "module.workflow.notifydirectory.xpage.pagetitle";
    private static final String PROPERTY_XPAGE_PATHLABEL = "module.workflow.notifydirectory.xpage.pathlabel";

    // markers
    private static final String MARK_MESSAGE_VALIDATION = "message_validation_success";

    //Message
    private static final String MESSAGE_ERROR_VALIDATION = "module.workflow.notifydirectory.message.error_validation";
    private static final String MESSAGE_ERROR_URL = "module.workflow.notifydirectory.message.error_url";

    // templates
    private static final String TEMPLATE_XPAGE_VALIDATION = "skin/plugins/workflow/modules/notifydirectory/task_notify_directory_validation.html";

    // request parameters
    private static final String PARAMETER_KEY = "key";
    private static final String USER_AUTO = "auto";

    /**
     * Returns the Form XPage result content depending on the request parameters and the current mode.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @return The page content.
     * @throws SiteMessageException the SiteMessageException
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        XPage page = new XPage(  );
        HttpSession session = request.getSession(  );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );
        page.setContent( getValid( request, session, nMode, plugin ) );

        return page;
    }

    /**
     * Perform formSubmit in database and return the result page
     * @param request The HTTP request
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @return the form recap
     * @throws SiteMessageException SiteMessageException
     */
    private String getValid( HttpServletRequest request, HttpSession session, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Locale locale = request.getLocale(  );
        HashMap model = new HashMap(  );

        if ( request.getParameter( PARAMETER_KEY ) != null )
        {
            ResourceKey resourceKey = ResourceKeyHome.findByPrimaryKey( request.getParameter( PARAMETER_KEY ), plugin );
            Timestamp currentDate = new Timestamp( GregorianCalendar.getInstance(  ).getTimeInMillis(  ) );

            if ( ( resourceKey != null ) && currentDate.before( resourceKey.getDateExpiry(  ) ) )
            {
                ITask task = TaskHome.findByPrimaryKey( resourceKey.getIdTask(  ), plugin, locale );
                TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( task.getId(  ),
                        plugin );

                State state = StateHome.findByPrimaryKey( config.getIdStateAfterValidation(  ), pluginWorkflow );
                Action action = ActionHome.findByPrimaryKey( task.getAction(  ).getId(  ), pluginWorkflow );

                // Create Resource History
                ResourceHistory resourceHistory = new ResourceHistory(  );
                resourceHistory.setIdResource( resourceKey.getIdResource(  ) );
                resourceHistory.setResourceType( resourceKey.getResourceType(  ) );
                resourceHistory.setAction( action );
                resourceHistory.setWorkFlow( action.getWorkflow(  ) );
                resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp(  ) );
                resourceHistory.setUserAccessCode( USER_AUTO );
                ResourceHistoryHome.create( resourceHistory, plugin );

                // Update Resource
                ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( resourceKey.getIdResource(  ),
                        resourceKey.getResourceType(  ), action.getWorkflow(  ).getId(  ), pluginWorkflow );
                resourceWorkflow.setState( state );
                ResourceWorkflowHome.update( resourceWorkflow, pluginWorkflow );

                //Delete ResourceKey
                ResourceKeyHome.remove( resourceKey.getKey(  ), pluginWorkflow );

                //if new state have action automatic
                WorkflowService.getInstance(  )
                               .executeActionAutomatic( resourceKey.getIdResource(  ), resourceKey.getResourceType(  ),
                    action.getWorkflow(  ).getId(  ), resourceWorkflow.getExternalParentId(  ) );

                model.put( MARK_MESSAGE_VALIDATION, config.getMessageValidation(  ) );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_VALIDATION, locale, model );

                return template.getHtml(  );
            }
            else
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_VALIDATION, AdminMessage.TYPE_ERROR );
            }
        }

        SiteMessageService.setMessage( request, MESSAGE_ERROR_VALIDATION, AdminMessage.TYPE_ERROR );

        return null;
    }
}
