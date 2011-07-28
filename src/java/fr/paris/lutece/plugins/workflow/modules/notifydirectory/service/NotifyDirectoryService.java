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
package fr.paris.lutece.plugins.workflow.modules.notifydirectory.service;

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

import org.apache.commons.lang.StringUtils;

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
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ActionHome;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.StateFilter;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKey;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.ResourceKeyHome;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.business.TaskNotifyDirectoryConfigHome;
import fr.paris.lutece.plugins.workflow.modules.notifydirectory.utils.constants.NotifyDirectoryConstants;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.service.WorkflowWebService;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 * NotifyDirectoryService
 *
 */
public final class NotifyDirectoryService
{
    private static final String BEAN_NOTIFY_DIRECTORY_SERVICE = "workflow-notifydirectory.notifyDirectoryService";
    private List<Integer> _listAcceptedEntryTypesEmailSMS;
    private List<Integer> _listAcceptedEntryTypesUserGuid;
    private List<Integer> _listRefusedEntryTypes;

    /**
     * Private constructor
     */
    private NotifyDirectoryService(  )
    {
    	// Init list accepted entry types for email
        _listAcceptedEntryTypesEmailSMS = fillListEntryTypes( NotifyDirectoryConstants.PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_EMAIL_SMS );
        
        // Init list accepted entry types for user guid
        _listAcceptedEntryTypesUserGuid = fillListEntryTypes( NotifyDirectoryConstants.PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_USER_GUID );
        
        // Init list refused entry types
        _listRefusedEntryTypes = fillListEntryTypes( NotifyDirectoryConstants.PROPERTY_REFUSED_DIRECTORY_ENTRY_TYPE_USER_GUID );
    }

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static NotifyDirectoryService getService(  )
    {
        return (NotifyDirectoryService) SpringContextService.getPluginBean( NotifyDirectoryPlugin.PLUGIN_NAME,
            BEAN_NOTIFY_DIRECTORY_SERVICE );
    }

    /**
    * Fill the list of entry types
    * @param strPropertyEntryTypes the property containing the entry types
    * @return a list of integer
    */
    public static List<Integer> fillListEntryTypes( String strPropertyEntryTypes )
    {
        List<Integer> listEntryTypes = new ArrayList<Integer>(  );
        String strEntryTypes = AppPropertiesService.getProperty( strPropertyEntryTypes );

        if ( StringUtils.isNotBlank( strEntryTypes ) )
        {
            String[] listAcceptEntryTypesForIdDemand = strEntryTypes.split( NotifyDirectoryConstants.COMMA );

            for ( String strAcceptEntryType : listAcceptEntryTypesForIdDemand )
            {
                if ( StringUtils.isNotBlank( strAcceptEntryType ) && StringUtils.isNumeric( strAcceptEntryType ) )
                {
                    int nAcceptedEntryType = Integer.parseInt( strAcceptEntryType );
                    listEntryTypes.add( nAcceptedEntryType );
                }
            }
        }

        return listEntryTypes;
    }

    // CHECKS
    
    /**
     * Check if the given entry type id is accepted for the email or the sms
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    public boolean isEntryTypeEmailSMSAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesEmailSMS != null ) && !_listAcceptedEntryTypesEmailSMS.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesEmailSMS.contains( nIdEntryType );
        }

        return bIsAccepted;
    }
    
    /**
     * Check if the given entry type id is accepted for the user guid
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    public boolean isEntryTypeUserGuidAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesUserGuid != null ) && !_listAcceptedEntryTypesUserGuid.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesUserGuid.contains( nIdEntryType );
        }

        return bIsAccepted;
    }

    /**
     * Check if the entry is refused (values set in the workflow-notifydirectory.properties)
     * @param nIdEntryType the id entry type
     * @return true if it is refused, false otherwise
     */
    private boolean isEntryTypeRefused( int nIdEntryType )
    {
    	boolean bIsRefused = true;

        if ( ( _listRefusedEntryTypes != null ) && !_listRefusedEntryTypes.isEmpty(  ) )
        {
            bIsRefused = _listRefusedEntryTypes.contains( nIdEntryType );
        }

        return bIsRefused;
    }
    
    // GETS
    
    /**
     * Get the list of states
     * @param nIdAction the id action
     * @return a ReferenceList
     */
    public ReferenceList getListStates( int nIdAction )
    {
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ReferenceList referenceListStates = new ReferenceList(  );
        Action action = ActionHome.findByPrimaryKey( nIdAction, pluginWorkflow );

        if ( ( action != null ) && ( action.getWorkflow(  ) != null ) )
        {
            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );

            List<State> listStates = StateHome.getListStateByFilter( stateFilter, pluginWorkflow );

            referenceListStates.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, NotifyDirectoryConstants.ID,
                    NotifyDirectoryConstants.NAME, true ) );
        }

        return referenceListStates;
    }
    
    /**
     * Get the list of directorise
     * @return a ReferenceList
     */
    public ReferenceList getListDirectories(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList listDirectories = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList refenreceListDirectories = new ReferenceList(  );
        refenreceListDirectories.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        if ( listDirectories != null )
        {
            refenreceListDirectories.addAll( listDirectories );
        }

        return refenreceListDirectories;
    }
    
    /**
     * Get the list of entries from a given id task
     * @param nIdTask the id task
     * @return a list of IEntry
     */
    public List<IEntry> getListEntries( int nIdTask )
    {
        Plugin pluginNotifyDirectory = PluginService.getPlugin( NotifyDirectoryPlugin.PLUGIN_NAME );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        TaskNotifyDirectoryConfig config = TaskNotifyDirectoryConfigHome.findByPrimaryKey( nIdTask,
                pluginNotifyDirectory );

        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        if ( config != null )
        {
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );

            listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );
        }

        return listEntries;
    }

    /**
     * Get the list of entries that have the accepted type (which are defined in <b>workflow-notifycrm.properties</b>)
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a ReferenceList
     */
    public ReferenceList getListEntriesUserGuid( int nIdTask, Locale locale )
    {
        ReferenceList refenreceListEntries = new ReferenceList(  );
        refenreceListEntries.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        for ( IEntry entry : getListEntries( nIdTask ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeUserGuidAccepted( nIdEntryType ) )
            {
                refenreceListEntries.addItem( entry.getPosition(  ), buildReferenceEntryToString( entry, locale ) );
            }
        }

        return refenreceListEntries;
    }
    
    /**
     * Get the list of entries that have the accepted type (which are defined in <b>workflow-notifycrm.properties</b>)
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a ReferenceList
     */
    public ReferenceList getListEntriesEmailSMS( int nIdTask, Locale locale )
    {
        ReferenceList refenreceListEntries = new ReferenceList(  );
        refenreceListEntries.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        for ( IEntry entry : getListEntries( nIdTask ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeEmailSMSAccepted( nIdEntryType ) )
            {
                refenreceListEntries.addItem( entry.getPosition(  ), buildReferenceEntryToString( entry, locale ) );
            }
        }

        return refenreceListEntries;
    }

    /**
     * Get the list of entries that have not the refused type (which are defined in the <b>workflow-notifycrm.properties</b>).
     * <br />
     * This list will be displayed as a freemarker label that the webmaster can use to write the notifications.
     * @param nIdTask the id task
     * @return a list of {@link IEntry}
     */
    public List<IEntry> getListEntriesFreemarker( int nIdTask )
    {
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        for ( IEntry entry : getListEntries( nIdTask ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( !isEntryTypeRefused( nIdEntryType ) )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }
    
    /**
     * Get the email from either an entry containing the email, or an entry containing the user guid
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the email
     */
    public String getEmail( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory )
    {
        String strEmail = StringUtils.EMPTY;

        if ( config.isNotifyByEmail(  ) )
        {
            if ( config.isNotifyByUserGuid(  ) )
            {
                String strUserGuid = getRecordFieldValue( config.getPositionEntryDirectoryUserGuid(  ), nIdRecord,
                        nIdDirectory );
                strEmail = WorkflowWebService.getService(  )
                                             .getUserAttribute( strUserGuid, LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
            }
            else
            {
                strEmail = getRecordFieldValue( config.getPositionEntryDirectoryEmail(  ), nIdRecord, nIdDirectory );
            }
        }

        return strEmail;
    }
    
    /**
     * Get the sms phone number
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the sms phone number
     */
    public String getSMSPhoneNumber( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory )
    {
        String strSMSPhoneNumber = StringUtils.EMPTY;

        if ( config.isNotifyBySms(  ) )
        {
        	strSMSPhoneNumber = getRecordFieldValue( config.getPositionEntryDirectorySms(  ), nIdRecord, nIdDirectory );
        }

        return strSMSPhoneNumber;
    }

    /**
     * Get the user guid
     * @param config the config
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the user guid, an empty string if the position is not set
     */
    public String getUserGuid( TaskNotifyDirectoryConfig config, int nIdRecord, int nIdDirectory )
    {
        String strUserGuid = StringUtils.EMPTY;

        if ( config.getPositionEntryDirectoryUserGuid(  ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            strUserGuid = getRecordFieldValue( config.getPositionEntryDirectoryUserGuid(  ), nIdRecord, nIdDirectory );
        }

        return strUserGuid;
    }
    
    /**
     * Fill the model
     * @param config the config
     * @param record the record
     * @param directory the directory
     * @param locale the Locale
     * @return the model
     */
    public Map<String, String> fillModel( TaskNotifyDirectoryConfig config, ResourceHistory resourceHistory, 
    		Record record, Directory directory, HttpServletRequest request )
    {
    	Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    	Map<String, String> model = new HashMap<String, String>(  );
    	model.put( NotifyDirectoryConstants.MARK_MESSAGE, config.getMessage(  ) );

        //Directory directory = DirectoryHome.findByPrimaryKey( config.getIdDirectory(  ), pluginDirectory );
        if ( directory != null )
        {
            model.put( NotifyDirectoryConstants.MARK_DIRECTORY_TITLE, directory.getTitle(  ) );
            model.put( NotifyDirectoryConstants.MARK_DIRECTORY_DESCRIPTION, directory.getDescription(  ) );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdRecord( record.getIdRecord(  ) );

        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

        for ( RecordField recordField : listRecordField )
        {
            String value = recordField.getEntry(  )
                                      .convertRecordFieldValueToString( recordField, request.getLocale(  ), false,
                    false );

            if ( isEntryTypeRefused( recordField.getEntry(  ).getEntryType(  ).getIdType(  ) ) )
            {
                continue;
            }
            else if ( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation &&
                    !recordField.getField(  ).getTitle(  ).equals( EntryTypeGeolocation.CONSTANT_ADDRESS ) )
            {
                continue;
            }
            else if ( ( recordField.getField(  ) != null ) &&
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
            model.put( NotifyDirectoryConstants.MARK_POSITION + String.valueOf( recordField.getEntry(  ).getPosition(  ) ), value );
        }

        if ( ( record.getDirectory(  ).getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            State state = WorkflowService.getInstance(  )
                                         .getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    record.getDirectory(  ).getIdWorkflow(  ), null );
            model.put( NotifyDirectoryConstants.MARK_STATUS, state.getName(  ) );
        }

        //Generate key
        String linkHtml = DirectoryUtils.EMPTY_STRING;

        if ( config.isEmailValidation(  ) )
        {
            ResourceKey resourceKey = new ResourceKey(  );
            UUID key = java.util.UUID.randomUUID(  );
            resourceKey.setKey( key.toString(  ) );
            resourceKey.setIdResource( record.getIdRecord(  ) );
            resourceKey.setResourceType( resourceHistory.getResourceType(  ) );
            resourceKey.setIdTask( config.getIdTask(  ) );

            Calendar calendar = GregorianCalendar.getInstance(  );
            calendar.add( Calendar.DAY_OF_MONTH, config.getPeriodValidity(  ) );
            resourceKey.setDateExpiry( new Timestamp( calendar.getTimeInMillis(  ) ) );
            ResourceKeyHome.create( resourceKey, pluginDirectory );
            
            StringBuilder sbBaseUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );
            if ( sbBaseUrl.length( ) > 0 && !sbBaseUrl.toString(  ).endsWith( NotifyDirectoryConstants.SLASH ) )
            {
            	sbBaseUrl.append( NotifyDirectoryConstants.SLASH );
            }
            sbBaseUrl.append( AppPathService.getPortalUrl(  ) );
            UrlItem url = new UrlItem( sbBaseUrl.toString(  ) );
            url.addParameter( NotifyDirectoryConstants.PARAMETER_PAGE, WorkflowPlugin.PLUGIN_NAME );
            url.addParameter( NotifyDirectoryConstants.PARAMETER_KEY, key.toString(  ) );

            StringBuffer sbLinkHtml = new StringBuffer(  );
            Map<String, String> mapParams = new HashMap<String, String>(  );
            mapParams.put( NotifyDirectoryConstants.ATTRIBUTE_HREF, url.getUrl(  ) );
            XmlUtil.beginElement( sbLinkHtml, NotifyDirectoryConstants.TAG_A, mapParams );
            sbLinkHtml.append( config.getLabelLink(  ) );
            XmlUtil.endElement( sbLinkHtml, NotifyDirectoryConstants.TAG_A );
            
            linkHtml = sbLinkHtml.toString(  );

            Map<String, Object> modelTmp = new HashMap<String, Object>(  );
            modelTmp.put( NotifyDirectoryConstants.MARK_LINK, url.getUrl(  ) );
            linkHtml = AppTemplateService.getTemplateFromStringFtl( linkHtml, request.getLocale(  ), modelTmp ).getHtml(  );
        }

        model.put( NotifyDirectoryConstants.MARK_LINK, linkHtml );
    	
    	// Fill user attributes
    	if ( WorkflowWebService.isUserAttributeWSActive(  ) )
        {
            String strUserGuid = getUserGuid( config, record.getIdRecord(  ), directory.getIdDirectory(  ) );
            WorkflowWebService.getService(  ).fillUserAttributesToModel( model, strUserGuid );
        }
    	
    	return model;
    }
    
    /**
     * Build the reference entry into String
     * @param entry the entry
     * @param locale the Locale
     * @return the reference entry
     */
    private String buildReferenceEntryToString( IEntry entry, Locale locale )
    {
        StringBuilder sbReferenceEntry = new StringBuilder(  );
        sbReferenceEntry.append( entry.getPosition(  ) );
        sbReferenceEntry.append( NotifyDirectoryConstants.SPACE + NotifyDirectoryConstants.OPEN_BRACKET );
        sbReferenceEntry.append( entry.getTitle(  ) );
        sbReferenceEntry.append( NotifyDirectoryConstants.SPACE + NotifyDirectoryConstants.HYPHEN + NotifyDirectoryConstants.SPACE );
        sbReferenceEntry.append( I18nService.getLocalizedString( entry.getEntryType(  ).getTitleI18nKey(  ), locale ) );
        sbReferenceEntry.append( NotifyDirectoryConstants.CLOSED_BRACKET );

        return sbReferenceEntry.toString(  );
    }

    /**
     * Get the record field value
     * @param nPosition the position of the entry
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the record field value
     */
    private String getRecordFieldValue( int nPosition, int nIdRecord, int nIdDirectory )
    {
        String strRecordFieldValue = StringUtils.EMPTY;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        // RecordField
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setPosition( nPosition );
        entryFilter.setIdDirectory( nIdDirectory );

        List<IEntry> listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        if ( ( listEntries != null ) && !listEntries.isEmpty(  ) )
        {
            IEntry entry = listEntries.get( 0 );
            RecordFieldFilter recordFieldFilterEmail = new RecordFieldFilter(  );
            recordFieldFilterEmail.setIdDirectory( nIdDirectory );
            recordFieldFilterEmail.setIdEntry( entry.getIdEntry(  ) );
            recordFieldFilterEmail.setIdRecord( nIdRecord );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilterEmail,
                    pluginDirectory );

            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty(  ) && ( listRecordFields.get( 0 ) != null ) )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue(  );

                if ( recordFieldIdDemand.getField(  ) != null )
                {
                    strRecordFieldValue = recordFieldIdDemand.getField(  ).getTitle(  );
                }
            }
        }

        return strRecordFieldValue;
    }
}
