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
    private List<Docker> dockers;
    
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
		
		JSONArray dockerArray = json.getJSONArray("dockers");
		
		dockers = new ArrayList<Docker>();
        //TODO support when somebody is not using default dock point on a node
//		for (int i = 0; i < dockerArray.length(); i++) { //go through all the dockers, since you can dock transition line to many places on a node
		for (int i = 1; i < dockerArray.length()-1; i++) {
			JSONObject docker = dockerArray.getJSONObject(i);
			int x = round(docker.getString("x"));
			int y = round(docker.getString("y"));
			dockers.add(new Docker(x, y));
		}
		
		
		
		if (json.optJSONObject("target") != null) {
		  target = json.getJSONObject("target").getString("resourceId");
		} else {
		  throw new RequestException("Transition '" + name + "' has no target.");
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
	
	@Override
	public String getObjectName() {
		return "Transition";
	}
	
	public String getDockers(int offsetX, int offsetY) {
		StringBuffer dockerString = new StringBuffer();
		for(Docker d : dockers) {
			dockerString.append(d.getX()+offsetX).append(",").append(d.getY()+offsetY);
			if(dockers.indexOf(d) == dockers.size() - 1)
				dockerString.append(":0,0");
			else
				dockerString.append(";");
		}
		return "g=\"" + dockerString.toString() + "\"";
	}
	
	private class Docker {
		private int x;
		private int y;
		
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		
		public Docker(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}
}
