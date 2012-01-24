package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPDLTransition extends JPDLObject {
  
	protected JPDLTransition() {
		
	}
	
	private static final String DEFAULT_SKIP_SAVING = "true";
	private static final String DEFAULT_AUTO_HIDE = "true";
	private static final String DEFAULT_PRIORITY = "10";
	private static final String DEFAULT_BUTTON_NAME = "Default";
	
	private String target;
    private String targetName;
    
    //action properties
    private String skipSaving;
    private String autoHide;
    private String priority;
    private String label;
    private String description;
    private String buttonName;
    private List<String> actionPermissions = new ArrayList<String>();
    private Map<String,String> actionAttributes = new HashMap<String,String>();
    
    //for 'decision'
    private String condition;
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getSkipSaving() {
		return skipSaving;
	}

	public void setSkipSaving(String skipSaving) {
		this.skipSaving = skipSaving;
	}

	public String getAutoHide() {
		return autoHide;
	}

	public void setAutoHide(String autoHide) {
		this.autoHide = autoHide;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String generateActionPermissionsXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<permissions>\n");
		for (String perm : actionPermissions) {
			sb.append(String.format("<config.ProcessStateActionPermission roleName=\"%s\" />\n", perm));
		}
		sb.append("</permissions>\n");
		return sb.toString();
	}
	
	public String generateActionAttributesXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<attributes>\n");
		for (String name : actionAttributes.keySet()) {
			sb.append(String.format("<config.ProcessStateActionAttribute name=\"%s\" value=\"%s\" />\n", name, actionAttributes.get(name)));
		}
		sb.append("</attributes>\n");
		return sb.toString();
	}
 
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		target = json.getJSONObject("target").getString("resourceId");
		skipSaving = json.getJSONObject("properties").getString("skip-saving");
		autoHide = json.getJSONObject("properties").getString("auto-hide");
		priority = json.getJSONObject("properties").getString("priority");
		label = json.getJSONObject("properties").getString("button-label");
		description = json.getJSONObject("properties").getString("button-desc");
		buttonName = json.getJSONObject("properties").getString("button-type");
		condition = json.getJSONObject("properties").getString("conditionexpression");
		
		if (StringUtils.isEmpty(skipSaving)) skipSaving = DEFAULT_SKIP_SAVING;
		if (StringUtils.isEmpty(autoHide)) autoHide = DEFAULT_AUTO_HIDE;
		if (StringUtils.isEmpty(priority)) priority = DEFAULT_PRIORITY;
		if (StringUtils.isEmpty(buttonName)) buttonName = DEFAULT_BUTTON_NAME;
		
		JSONObject permissions = json.getJSONObject("properties").optJSONObject("action-permissions");
		if (permissions != null) {
			 JSONArray permissionsItems = permissions.optJSONArray("items");
			 for (int i = 0; i < permissionsItems.length(); i++) {
				 JSONObject obj = permissionsItems.getJSONObject(i);
				 actionPermissions.add(obj.optString("rolename"));
			 }
		}
		JSONObject attributes = json.getJSONObject("properties").optJSONObject("action-attributes");
		if (attributes != null) {
			 JSONArray attributesItems = attributes.optJSONArray("items");
			 for (int i = 0; i < attributesItems.length(); i++) {
				 JSONObject obj = attributesItems.getJSONObject(i);
				 actionAttributes.put(obj.optString("attributename"), obj.optString("attributevalue"));
			 }
		}
	}
	
}
