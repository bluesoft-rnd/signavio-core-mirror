package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import pl.net.bluesoft.rnd.processtool.editor.jpdl.exception.UnsupportedJPDLObjectException;

import com.signavio.platform.exceptions.RequestException;

public abstract class JPDLObject {
	
	protected String resourceId;
	protected String name;
	
	public abstract String getObjectName();
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void fillBasicProperties(JSONObject json) throws JSONException {
		resourceId = json.getString("resourceId");
		name = json.getJSONObject("properties").getString("name");
		if (StringUtils.isEmpty(name)) {
			throw new RequestException("Object '" + getObjectName() + "' has no name.");
		}
	}
	
    public static JPDLObject getJPDLObject(JSONObject obj) throws JSONException, UnsupportedJPDLObjectException {
		
		JPDLObject ret = null;

		String stencilId = obj.getJSONObject("stencil").getString("id");
		
		if ("StartNoneEvent".equals(stencilId)) {
			ret = new JPDLStartEvent();
		} else if ("Task".equals(stencilId)) {
			String taskType = obj.getJSONObject("properties").getString("tasktype");
			if ("User".equals(taskType))
			  ret = new JPDLUserTask();
			else
			  ret = new JPDLJavaTask();
		} else if ("SequenceFlow".equals(stencilId)) {
			ret = new JPDLTransition();
		} else if ("EndNoneEvent".equals(stencilId)) {
			ret = new JPDLEndEvent();
		} else if ("Exclusive_Databased_Gateway".equals(stencilId)) {
			ret = new JPDLDecision();
		} else {
		  throw new UnsupportedJPDLObjectException("Object named '" + stencilId + "' is not supported.");
		}
		return ret;
	}
    
    protected static int round(String s) {
    	if (s == null)
    		return 0;
    	Float f = Float.parseFloat(s);
    	return Math.round(f);
    }
    
}
