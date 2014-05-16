package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import com.signavio.platform.exceptions.RequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.IndentedStringBuilder;
import pl.net.bluesoft.rnd.processtool.editor.XmlUtil;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AperteTransition extends AperteObject {
	public static final String ACTION_PSEUDO_STATE_WIDGETS = "actionPseudoStateWidgets";

	public AperteTransition(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    private static final String DEFAULT_BUTTON_NAME = "Default";
	
	private String target;
    private String targetName;
    private List<Docker> dockers;
    
    //action properties
    private String buttonName;
    private List<String> actionPermissions = new ArrayList<String>();
    private Map<String,Object> actionAttributes = new TreeMap<String,Object>();
    private Map<String,Object> actionAutowiredProperties = new TreeMap<String,Object>();
	private String widgetsJson;
    
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

	private void generateActionPermissionsXML(IndentedStringBuilder sb) {
		if (!actionPermissions.isEmpty()) {
			sb.append("<permissions>\n");
			sb.begin();
			for (String perm : actionPermissions) {
				sb.append(String.format("<config.ProcessStateActionPermission roleName=\"%s\" />\n", perm));
			}
			sb.end();
			sb.append("</permissions>\n");
		}
	}
	
	private void generateActionAttributesXML(IndentedStringBuilder sb) {
		if (!actionAttributes.isEmpty()) {
			sb.append("<attributes>\n");
			sb.begin();
			for (String name : actionAttributes.keySet()) {
				sb.append(String.format("<config.ProcessStateActionAttribute name=\"%s\" value=\"%s\" />\n", name, actionAttributes.get(name)));
			}
			sb.end();
			sb.append("</attributes>\n");
		}
	}
	
	public void generateStateActionXML(IndentedStringBuilder sb) {
		sb.append(String.format("<config.ProcessStateAction bpmName=\"%s\" buttonName=\"%s\" ", name, buttonName));
		for (String name : actionAutowiredProperties.keySet()) {
			Object value = actionAutowiredProperties.get(name);
			if (value != null) {
				sb.append(String.format("%s=\"%s\" ", name, value));
			}
		}
	    sb.append(">\n");
		sb.begin();
	    generateActionPermissionsXML(sb);
	    generateActionAttributesXML(sb);
		sb.end();
	    sb.append("</config.ProcessStateAction>\n");
	}
  
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		 
		JSONObject jsonObject = json.getJSONObject("properties");
		String ap = (String) jsonObject.get("action-properties");
		String name = (String) jsonObject.get("name");
		
		if(!isPriorityisFilledCorrect(ap)){
			throw new JSONException("Priority field in:  '" + name + "' transition must be filled with number!");
		}

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
		  target = json.getJSONObject("target").optString("resourceId");
		} else {
		  throw new RequestException("Transition '" + name + "' has no target.");
		}
        JSONObject properties = json.getJSONObject("properties");
        buttonName = properties.optString("button-type");
		condition = properties.optString("conditionexpression");
//		if (!StringUtils.isEmpty(condition) && !condition.startsWith("#{")) {
//			condition = "#{" + condition + "}";
//		}
		
		if (StringUtils.isEmpty(buttonName)) buttonName = DEFAULT_BUTTON_NAME;
		
		JSONObject permissions = properties.optJSONObject("action-permissions");
		if (permissions != null) {
			 JSONArray permissionsItems = permissions.optJSONArray("items");
			 for (int i = 0; i < permissionsItems.length(); i++) {
				 JSONObject obj = permissionsItems.getJSONObject(i);
				 actionPermissions.add(obj.optString("rolename"));
			 }
		}

		loadAttributeMap(properties, "action-properties", actionAutowiredProperties);

		loadAttributeMap(properties, "action-attributes", actionAttributes);

		if (actionAttributes.containsKey(ACTION_PSEUDO_STATE_WIDGETS)) {
			widgetsJson = (String)actionAttributes.get(ACTION_PSEUDO_STATE_WIDGETS);
			actionAttributes.remove(ACTION_PSEUDO_STATE_WIDGETS);

			if (widgetsJson != null && !widgetsJson.isEmpty()) {
				widgetsJson = new String(Base64.decodeBase64(widgetsJson), Charset.forName("UTF-8"));
			}
		}
	}

	private void loadAttributeMap(JSONObject properties, String propertyName, Map<String, Object> targetMap) throws JSONException {
		String autowiredProps = properties.optString(propertyName);
		loadAttributeMap(autowiredProps, targetMap);
	}

	private void loadAttributeMap(String autowiredProps, Map<String, Object> targetMap) throws JSONException {
		if (!StringUtils.isEmpty(autowiredProps)) {
			JSONObject jsonObj = new JSONObject(XmlUtil.decodeXmlEscapeCharacters(autowiredProps));
			Iterator i = jsonObj.keys();
		    while (i.hasNext()) {
		    	String key = (String)i.next();
				targetMap.put(key, jsonObj.get(key));
		    }
		}
	}

	private boolean isPriorityisFilledCorrect(String actionProperties){
		if (actionProperties!=null && !actionProperties.isEmpty()) {
			Pattern patern = Pattern.compile("\"priority\":\"\\d*\"");
			Matcher matcher = patern.matcher(actionProperties);
			return matcher.find();
		}
		return true;
	}

	public String getWidgetsJson() {
		return widgetsJson;
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
