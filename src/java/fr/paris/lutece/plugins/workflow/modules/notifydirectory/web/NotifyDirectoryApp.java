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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.web;

import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKey;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.IResourceKeyService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.ITaskNotifyDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.ResourceKeyService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.service.TaskNotifyDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.utils.constants.NotifyDirectoryConstants;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.sql.Timestamp;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * This class manages Form page.
 *
 */
public class NotifyDirectoryApp implements XPageApplication
{
    // Templates
    private static final String TEMPLATE_XPAGE_VALIDATION = "skin/plugins/workflow/modules/notifydirectory/task_notify_directory_validation.html";

    // SERVICES
    private IResourceKeyService _resourceKeyService = SpringContextService.getBean( ResourceKeyService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private ITaskNotifyDirectoryConfigService _taskNotifyDirectoryConfigService = SpringContextService.getBean( TaskNotifyDirectoryConfigService.BEAN_SERVICE );
    private IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
    private IResourceWorkflowService _resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );

    /**
     * {@inheritDoc}
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        XPage page = new XPage(  );
        HttpSession session = request.getSession(  );

        page.setTitle( I18nService.getLocalizedString( NotifyDirectoryConstants.PROPERTY_XPAGE_PAGETITLE,
                request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( NotifyDirectoryConstants.PROPERTY_XPAGE_PATHLABEL,
                request.getLocale(  ) ) );
        page.setContent( getValid( request, session, plugin ) );

        return page;
    }

    /**
     * Perform formSubmit in database and return the result page
     * @param request The HTTP request
     * @param session the session
     * @param plugin The Plugin
     * @return the form recap
     * @throws SiteMessageException SiteMessageException
     */
    private String getValid( HttpServletRequest request, HttpSession session, Plugin plugin )
        throws SiteMessageException
    {
        Locale locale = request.getLocale(  );
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( request.getParameter( NotifyDirectoryConstants.PARAMETER_KEY ) != null )
        {
            ResourceKey resourceKey = _resourceKeyService.findByPrimaryKey( request.getParameter( 
                        NotifyDirectoryConstants.PARAMETER_KEY ), plugin );
            Timestamp currentDate = new Timestamp( GregorianCalendar.getInstance(  ).getTimeInMillis(  ) );

            if ( ( resourceKey != null ) && currentDate.before( resourceKey.getDateExpiry(  ) ) )
            {
                ITask task = _taskService.findByPrimaryKey( resourceKey.getIdTask(  ), locale );
                TaskNotifyDirectoryConfig config = _taskNotifyDirectoryConfigService.findByPrimaryKey( task.getId(  ),
                        plugin );

                State state = _stateService.findByPrimaryKey( config.getIdStateAfterValidation(  ) );
                Action action = _actionService.findByPrimaryKey( task.getAction(  ).getId(  ) );

                // Create Resource History
                ResourceHistory resourceHistory = new ResourceHistory(  );
                resourceHistory.setIdResource( resourceKey.getIdResource(  ) );
                resourceHistory.setResourceType( resourceKey.getResourceType(  ) );
                resourceHistory.setAction( action );
                resourceHistory.setWorkFlow( action.getWorkflow(  ) );
                resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp(  ) );
                resourceHistory.setUserAccessCode( NotifyDirectoryConstants.USER_AUTO );
                _resourceHistoryService.create( resourceHistory );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceKey.getIdResource(  ),
                        resourceKey.getResourceType(  ), action.getWorkflow(  ).getId(  ) );
                resourceWorkflow.setState( state );
                _resourceWorkflowService.update( resourceWorkflow );

                // Delete ResourceKey
                _resourceKeyService.remove( resourceKey.getKey(  ), plugin );

                //if new state have action automatic
                WorkflowService.getInstance(  )
                               .executeActionAutomatic( resourceKey.getIdResource(  ), resourceKey.getResourceType(  ),
                    action.getWorkflow(  ).getId(  ), resourceWorkflow.getExternalParentId(  ) );

                model.put( NotifyDirectoryConstants.MARK_MESSAGE_VALIDATION, config.getMessageValidation(  ) );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_VALIDATION, locale, model );

                return template.getHtml(  );
            }
            else
            {
                SiteMessageService.setMessage( request, NotifyDirectoryConstants.MESSAGE_ERROR_VALIDATION,
                    AdminMessage.TYPE_ERROR );
            }
        }

        SiteMessageService.setMessage( request, NotifyDirectoryConstants.MESSAGE_ERROR_VALIDATION,
            AdminMessage.TYPE_ERROR );

        return null;
    }
}
