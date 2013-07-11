package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.XmlUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AperteJavaTask extends AperteTask {
	
	private Map<String,String> stepDataMap = new HashMap<String,String>();

    public AperteJavaTask(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }


	@Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
		String stepDataJson = json.getJSONObject("properties").getString("aperte-conf");
		if (stepDataJson != null && stepDataJson.trim().length() != 0) {
		  stepDataJson = XmlUtil.decodeXmlEscapeCharacters(stepDataJson);
		  JSONObject stepDataJsonObj = new JSONObject(stepDataJson);
		  Iterator i = stepDataJsonObj.keys();
		  while(i.hasNext()) {
			String key = (String)i.next();  
            Object value = stepDataJsonObj.get(key);  
            if (value instanceof String) {
                byte[] bytes = Base64.decodeBase64(((String) value).getBytes());
                value = new String(bytes);
            }
			stepDataMap.put(key, value.toString());
		  }
		}

	}

}
