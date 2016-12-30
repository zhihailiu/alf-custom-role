package com.acme.group;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class GroupZoneWebScript extends DeclarativeWebScript {
	
    Log log = LogFactory.getLog(GroupZoneWebScript.class);

	private AuthorityService authorityService;
	
    public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	protected Map<String, Object> executeImpl(
            WebScriptRequest req, Status status, Cache cache) {

		JSONObject json;
		String shortName;
		String displayName;
		String parentShortName;
		Set<String> authorityZones;
		
		try {
			String s = req.getContent().getContent();
			log.info(s);
			
			json = new JSONObject(s);
			
			shortName = json.getString("shortName");
			displayName = json.getString("displayName");
			parentShortName = json.getString("parentShortName");
			
			JSONArray zones = json.getJSONArray("zones");
			authorityZones = new HashSet<String>();
			for (int i = 0; i < zones.length(); i++) {
				authorityZones.add(zones.getString(i));
			}
		} catch (JSONException | IOException e) {
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		// create group
		String fullName = this.authorityService.createAuthority(AuthorityType.GROUP, shortName, displayName, authorityZones);
		log.info("created:" + fullName);
		
		// add to parent group
		this.authorityService.addAuthority("GROUP_" + parentShortName, fullName);
		log.info("added to parent:" + "GROUP_" + parentShortName);

		Map<String, Object> model = this.buildResponse(fullName);
        return model;
    }
	
	private Map<String, Object> buildResponse(String fullName) {
		Map<String, Object> model = new HashMap<String, Object>();
		
        model.put("authorityType", AuthorityType.GROUP.toString());
		
		NodeRef node = this.authorityService.getAuthorityNodeRef(fullName);
        model.put("nodeRef", node.toString());

        model.put("shortName", this.authorityService.getShortName(fullName));
        
        model.put("fullName", fullName);

        model.put("displayName", this.authorityService.getAuthorityDisplayName(fullName));

        Set<String> parentGroups = this.authorityService.getContainingAuthorities(AuthorityType.GROUP, fullName, true);
        model.put("parentGroups", this.toJSONArrayString(parentGroups));

        Set<String> zones = this.authorityService.getAuthorityZones(fullName);
        model.put("zones", this.toJSONArrayString(zones));
        
        return model;
	}

	private String toJSONArrayString(Set<String> set) {
		JSONArray array = new JSONArray();
		for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
			String s = iter.next();
			array.put(s);
		}
		return array.toString();
	}
}