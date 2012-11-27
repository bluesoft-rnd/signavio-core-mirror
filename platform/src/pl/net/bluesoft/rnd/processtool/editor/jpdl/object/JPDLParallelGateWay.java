package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
/**
 * 
 * @author kkolodziej@bluesoft.net.pl
 *
 */
public class JPDLParallelGateWay extends JPDLComponent {


    public JPDLParallelGateWay(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    @Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);
	}
    
    
    @Override
	public String toXML() { 
    	StringBuilder sb = new StringBuilder();
    	
    	// If its actual a fork?!
    	if(getOutgoing().size()>1 && getIncoming().size()==1){
    	
    	
    	sb.append(String.format("<fork name=\"%s\" g=\"%d,%d,%d,%d\" >\n", name,
                boundsX, boundsY, width, height
                ));
		sb.append(getTransitionsXML());
		sb.append("</fork>\n");
    	}
    	
    	
    	// If its actual a join?! 
    	else if(getOutgoing().size()==1 && getIncoming().size()>1){
        	sb.append(String.format("<join name=\"%s\" g=\"%d,%d,%d,%d\" >\n", name,
                    boundsX, boundsY, width, height
                    ));
    		sb.append(getTransitionsXML());
    		sb.append("</join>\n");
        	}
    	
    	else{
    		
    		sb.append(String.format("<join name=\"%s\" g=\"%d,%d,%d,%d\" >\n", name,
                    boundsX, boundsY, width, height
                    ));
    		sb.append(String.format("<transition to=\"%s_fork\" />\n",name));
    		sb.append("</join>\n");
    		
    		
    		sb.append(String.format("<fork name=\"%s_fork\" g=\"%d,%d,%d,%d\" >\n", name,
                    boundsX, boundsY, width, height
                    ));
    		sb.append(getTransitionsXML());
    		sb.append("</fork>\n");	
    	}
    	
    	
		return sb.toString();
    }


	@Override
	public String getObjectName() {
		return "ParallelGateway";
	}
	
}
