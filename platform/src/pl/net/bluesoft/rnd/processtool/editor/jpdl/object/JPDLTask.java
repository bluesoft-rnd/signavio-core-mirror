package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

public abstract class JPDLTask extends JPDLComponent {
  
	protected String taskType;

    protected JPDLTask(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	@Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		taskType = json.getJSONObject("properties").getString("tasktype");
	}
}
