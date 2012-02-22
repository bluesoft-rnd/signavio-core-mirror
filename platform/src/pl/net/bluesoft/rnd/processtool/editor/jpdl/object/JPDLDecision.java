package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

public class JPDLDecision extends JPDLComponent {


    public JPDLDecision(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    @Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
	}
    
    
    @Override
	public String toXML() { 
    	StringBuilder sb = new StringBuilder();
    	sb.append(String.format("<decision name=\"%s\" g=\"%d,%d,%d,%d\" >\n", name,
                boundsX, boundsY, width, height
                ));
		sb.append(getTransitionsXML());
		sb.append("</decision>");
		return sb.toString();
    }


	@Override
	public String getObjectName() {
		return "Decision";
	}
	
}
