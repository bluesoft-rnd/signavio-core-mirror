package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import com.signavio.platform.exceptions.RequestException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.components.StencilNames;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.exception.UnsupportedJPDLObjectException;

public abstract class AperteObject {

	protected String resourceId;
	protected String name;
    protected String stencilId;

    protected AperteWorkflowDefinitionGenerator generator;

    protected AperteObject(AperteWorkflowDefinitionGenerator generator) {
        this.generator = generator;
    }

    public String getStencilId() {
        return stencilId;
    }

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
        stencilId = json.getJSONObject("stencil").getString("id");
		if (StringUtils.isEmpty(name)) {
			throw new RequestException("Object '" + stencilId + "' has no name.");
		}
	}

    public static AperteObject getJPDLObject(JSONObject obj, AperteWorkflowDefinitionGenerator generator)
            throws JSONException, UnsupportedJPDLObjectException {

		AperteObject ret = null;

		String stencilId = obj.getJSONObject("stencil").getString("id");
		if (StencilNames.TASK.equalsStencilName(stencilId)) {
			String taskType = obj.getJSONObject("properties").getString("tasktype");
			if (StencilNames.USER.equalsStencilName(taskType))
			  ret = new AperteStepEditorNode(generator);
			else
			  ret = new AperteJavaTask(generator);
		} else if (StencilNames.SEQUENCE_FLOW.equalsStencilName(stencilId)) {
			ret = new AperteTransition(generator);
		} else if (StencilNames.COLLAPSED_SUBPROCESS.equalsStencilName(stencilId)) {
			ret = new AperteCollapsedSubprocess(generator);
		} else if (StencilNames.END_EVENT.equalsStencilName(stencilId)) {
			ret = new AperteStepEditorNode(generator);
		} else {
			ret = new AperteComponent(generator);
		}
		return ret;
	}

    protected static int round(String s) {
    	if (s == null)
    		return 0;
    	Float f = Float.parseFloat(s);
    	return Math.round(f);
    }

    public AperteWorkflowDefinitionGenerator getGenerator() {
        return generator;
    }
}
