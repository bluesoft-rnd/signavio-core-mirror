package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import com.signavio.platform.exceptions.RequestException;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AperteCollapsedSubprocess extends AperteComponent {

	private String subprocess;
	private Map<String, String> output;
	private Map<String, String> input;

	public AperteCollapsedSubprocess(AperteWorkflowDefinitionGenerator generator) {
		super(generator);
	}


	@Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		
		subprocess = json.getJSONObject("properties").getString("entry");
		
		if (subprocess == null || subprocess.equals("")) {
			throw new RequestException("Subprocess references cannot be empty.");

		}
		
		input = jsonStringToMap(json.getJSONObject("properties")
				.getString("inputmaps"));
		
		String inputString = json.getJSONObject("properties").getString("inputmaps");

		if (inputString == null || inputString.isEmpty()) {
			throw new RequestException("Input maps cannot be empty.");

		} 
		try {
			input = jsonStringToMap(inputString);
		} catch (JSONException e) {
			throw new RequestException(
					"Error while parsing input maps: "
							+ e.getLocalizedMessage()
							+ "\n Parsing map shoud look like this, example: \"initiator:initiator\" ");
		}
		
		String outputString = json.getJSONObject("properties").getString("outputmaps");
		

		if (outputString == null || outputString.isEmpty()) {
			throw new RequestException("Output maps cannot be empty.");

		} else {
			try {
				output = jsonStringToMap(outputString);
			} catch (JSONException e) {
				throw new RequestException(
						"Error while parsing output maps: "
								+ e.getLocalizedMessage()
								+ "\n Parsing map shoud look like this, example: \"initiator:initiator\" ");
			}

		}
	}

	protected Map<String, String> jsonStringToMap(String inputMapsString)
			throws JSONException {
		JSONObject inputMaps = new JSONObject(inputMapsString);
		Iterator<String> nameItr = inputMaps.keys();
		Map<String, String> inputs = new HashMap<String, String>();
		while (nameItr.hasNext()) {
			String n = nameItr.next();
			inputs.put(n, inputMaps.getString(n));
		}
		return inputs;
	}
}
