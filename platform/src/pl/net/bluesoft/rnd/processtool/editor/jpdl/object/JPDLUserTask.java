package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import com.signavio.platform.exceptions.RequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aperteworkflow.editor.domain.Permission;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.Widget;
import pl.net.bluesoft.rnd.processtool.editor.XmlUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JPDLUserTask extends JDPLStepEditorNode {



    public JPDLUserTask(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }


	@Override
	public String toXML() { 
		StringBuffer sb = new StringBuffer();
		String taskConf = "";
		if (assignee != null && assignee.trim().length() > 0)
			taskConf = String.format("assignee=\"%s\"", assignee);
		else if (candidateGroups != null && candidateGroups.trim().length() > 0)
			taskConf = String.format("candidate-groups=\"%s\"", candidateGroups);
		else if (swimlane != null && swimlane.trim().length() > 0)
			taskConf = String.format("swimlane=\"%s\"", swimlane);
		
		sb.append(String.format("<task %s name=\"%s\" g=\"%d,%d,%d,%d\">\n", taskConf,name,
                boundsX, boundsY, width, height
        ));
		sb.append(getTransitionsXML());
		sb.append("</task>\n");
		return sb.toString();
    }
	


	 
	@Override
	public String getObjectName() {
		return "User task";
	}	
}
