package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.net.bluesoft.rnd.processtool.editor.XmlUtil;

import com.signavio.platform.exceptions.RequestException;

public class JPDLTransition extends JPDLObject {
  
	protected JPDLTransition() {
		
	}
	
	private static final String DEFAULT_BUTTON_NAME = "Default";
	
	private String target;
    private String targetName;
    
    //action properties
    private String buttonName;
    private List<String> actionPermissions = new ArrayList<String>();
    private Map<String,String> actionAttributes = new HashMap<String,String>();
    private Map<String,Object> actionAutowiredProperties = new HashMap<String,Object>();
    
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	private String generateActionPermissionsXML() {
		StringBuffer sb = new StringBuffer();
		if (!actionPermissions.isEmpty()) {
		  sb.append("<permissions>\n");
		}
		for (String perm : actionPermissions) {
			sb.append(String.format("<config.ProcessStateActionPermission roleName=\"%s\" />\n", perm));
		}
		if (!actionPermissions.isEmpty()) {
		  sb.append("</permissions>\n");
		}
		return sb.toString();
	}
	
	private String generateActionAttributesXML() {
		StringBuffer sb = new StringBuffer();
		if (!actionAttributes.isEmpty()) {
		  sb.append("<attributes>\n");
		}
		for (String name : actionAttributes.keySet()) {
			sb.append(String.format("<config.ProcessStateActionAttribute name=\"%s\" value=\"%s\" />\n", name, actionAttributes.get(name)));
		}
		if (!actionAttributes.isEmpty()) {
		  sb.append("</attributes>\n");
		}
		return sb.toString();
	}
	
	public String generateStateActionXML() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("<config.ProcessStateAction bpmName=\"%s\" buttonName=\"%s\" ", name, buttonName));
		for (String name : actionAutowiredProperties.keySet()) {
			sb.append(String.format("%s=\"%s\" ", name, actionAutowiredProperties.get(name)));
		}
	    sb.append(" >\n");
	    sb.append(generateActionPermissionsXML());
	    sb.append(generateActionAttributesXML());
	    sb.append("</config.ProcessStateAction>\n");
	    return sb.toString();
	}
 
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		if (json.optJSONObject("target") != null) {
		  target = json.getJSONObject("target").getString("resourceId");
		} else {
		  throw new RequestException("Transition has no target.");
		}
		buttonName = json.getJSONObject("properties").getString("button-type");
		condition = json.getJSONObject("properties").getString("conditionexpression");
		if (!StringUtils.isEmpty(condition) && !condition.startsWith("#{")) {
			condition = "#{" + condition + "}";
		}
		
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
		String autowiredProps = json.getJSONObject("properties").optString("action-properties");
		if (!StringUtils.isEmpty(autowiredProps)) {
			JSONObject jsonObj = new JSONObject(XmlUtil.replaceXmlEscapeCharacters(autowiredProps));
			Iterator i = jsonObj.keys();
		    while (i.hasNext()) {
		    	String key = (String)i.next();
		    	actionAutowiredProperties.put(key, jsonObj.get(key));
		    }
		}
		
	}
	
}
