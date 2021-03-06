package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.exceptions.RequestException;

import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

public class JPDLCollapsedSubprocess extends JPDLComponent {


    private String subprocess;
	private Map<String, String> output;
	private Map<String, String> input;

	public JPDLCollapsedSubprocess(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

	@Override
	public String toXML() {
		StringBuffer sb = new StringBuffer();

		sb.append(String.format("<sub-process sub-process-key=\"%s\" name=\"%s\" g=\"%d,%d,%d,%d\">\n", subprocess, name,
                boundsX, boundsY, width, height
        ));
		for(Entry<String, String> entry : input.entrySet()){
			sb.append(String.format("<parameter-in subvar=\"%s\" var=\"%s\"/>\n", entry.getKey(), entry.getValue()));
		}
		for(Entry<String, String> entry : output.entrySet()){
			sb.append(String.format("<parameter-out var=\"%s\" subvar=\"%s\"/>\n", entry.getKey(), entry.getValue()));
		}

		sb.append(getTransitionsXML());
		sb.append("</sub-process>\n");

		return sb.toString();
    }

	@Override
	public String getObjectName() {
		return "Subprocess";
	}

	@Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		subprocess = json.getJSONObject("properties").getString("entry");

		try{
			input = jsonStringToMap(json.getJSONObject("properties").getString("inputmaps"));
		} catch(JSONException e){
			throw new RequestException("Error while parsing input maps: " + e.getLocalizedMessage());
		}

		try{
			output = jsonStringToMap(json.getJSONObject("properties").getString("outputmaps"));
		} catch(JSONException e){
			throw new RequestException("Error while parsing output maps: " + e.getLocalizedMessage());
		}
	}

	protected Map<String, String> jsonStringToMap(String inputMapsString) throws JSONException {
		JSONObject inputMaps = new JSONObject(inputMapsString);
		Iterator<String> nameItr = inputMaps.keys();
		Map<String, String> inputs = new HashMap<String, String>();
		while(nameItr.hasNext()) {
			String n = nameItr.next();
			inputs.put(n, inputMaps.getString(n));
		}
		return inputs;
	}
}
