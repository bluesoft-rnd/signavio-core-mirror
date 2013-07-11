package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

import java.util.HashMap;
import java.util.Map;

public  class AperteComponent extends AperteObject {

	protected Map<String, AperteTransition> outgoing = new HashMap<String, AperteTransition>();
	protected Map<String, AperteTransition> incoming = new HashMap<String, AperteTransition>();

    protected int boundsX, boundsY, width, height;
    protected int offsetX, offsetY;

    protected AperteComponent(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    public void applyOffset(int offsetX, int offsetY) {
        boundsX = boundsX + offsetX;
        boundsY = boundsY + offsetY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    public Map<String, AperteTransition> getOutgoing() {
		return outgoing;
	}
    public Map<String, AperteTransition> getIncoming() {
		return incoming;
	}

	public void setOutgoing(Map<String, AperteTransition> outgoing) {
		this.outgoing = outgoing;
	}
	public void setIncoming(Map<String, AperteTransition> incoming) {
		this.incoming = incoming;
	}
	
	public AperteTransition getTransition(String resourceId) {
		return outgoing.get(resourceId);
	}
	
	public void putTransition(String resourceId, AperteTransition transition) {
		outgoing.put(resourceId, transition);
	}
	
	public AperteTransition getIncomingTransition(String resourceId) {
		return incoming.get(resourceId);
	}
	
	public void putIncomingTransition(String resourceId, AperteTransition transition) {
		incoming.put(resourceId, transition);
	}
	
	//public abstract String toXML();


    public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		
		JSONArray array = json.getJSONArray("outgoing");
		
		for (int i = 0; i < array.length(); i++) {
			JSONObject arrObj = array.getJSONObject(i);
			outgoing.put(arrObj.getString("resourceId"), null);
		}

        JSONObject bounds = json.getJSONObject("bounds");
        if (bounds != null) {
            JSONObject upperLeft = bounds.getJSONObject("upperLeft");
            JSONObject lowerRight = bounds.getJSONObject("lowerRight");
            boundsX = upperLeft.getInt("x");
            boundsY = upperLeft.getInt("y");
            this.width = lowerRight.getInt("x") - boundsX;
            this.height = lowerRight.getInt("y") - boundsY;
        }
	}
	
	protected String getTransitionsXML() {
		StringBuilder sb = new StringBuilder();
		
		for (String targetResourceId : outgoing.keySet()) {
			AperteTransition transition = outgoing.get(targetResourceId);
			sb.append(String.format("<transition %s name=\"%s\" to=\"%s\">\n", transition.getDockers(offsetX, offsetY), transition.getName(), transition.getTargetName()));
			if (transition.getCondition() != null && transition.getCondition().trim().length() > 0) {
				sb.append(String.format("<condition expr=\"%s\"/>\n", transition.getCondition()));
			}
			//sb.append(String.format("<description>Original ID: '%s'</description>\n", transition.getResourceId()));
			sb.append("</transition>\n");
		}
		
		return sb.toString();
	}

}
