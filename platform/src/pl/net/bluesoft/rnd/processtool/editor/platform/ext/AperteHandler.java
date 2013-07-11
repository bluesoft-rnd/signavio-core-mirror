/**
 *
 */
package pl.net.bluesoft.rnd.processtool.editor.platform.ext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@HandlerConfiguration(uri = "/aperte_definitions", rel="aperte")
public class AperteHandler extends BasisHandler {
	
	private final static Logger logger = Logger.getLogger(AperteHandler.class);
	private final static PlatformProperties props = Platform.getInstance().getPlatformProperties();

	public AperteHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Returns a plugins configuration xml file that fits to the current user's license.
	 * @throws Exception
	 */
	@Override
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  	
  		// Set status code and write the representation
  		res.setStatus(200);
  		res.setContentType("application/json");

        try {
            JSONObject ret = getStencilExtensionString();
            ret.write( res.getWriter() );

        } catch (IOException e) {
            logger.error("Error while reading data", e);
        } catch (JSONException e) {
            logger.error("Error while parsing data", e);
        }
    }

    private JSONObject getStencilExtensionString() throws IOException, JSONException {
        JSONObject root = new JSONObject();

        setGlobalParams(root);
        root.put( "stencils", new JSONArray());

        addProperties(root);

        addRules(root);
        removeStencils(root);

        return root;
    }

    private void addProperties(JSONObject root) throws IOException,JSONException {
        JSONArray properties = new JSONArray();
        root.put( "properties", properties);

        modifyTaskProperties(properties);
        modifyEndProperties(properties);
        modifySequenceFlowProperties(properties);
        modifyBpmnProperties(properties);
    }

    private void modifyBpmnProperties(JSONArray properties) throws JSONException, IOException {
        JSONObject obj1 = new JSONObject();
        properties.put(obj1);

        JSONArray o_roles= new JSONArray();
        o_roles.put("BPMNDiagram");
        obj1.put("roles",o_roles);

        JSONArray o_prop= new JSONArray();
        obj1.put("properties",o_prop);

        o_prop.put(getAperteLanguage());
        o_prop.put(getProcessConf());
        o_prop.put(getProcessFileName());
        o_prop.put(getProcessId());
        o_prop.put(getBundleName());
        o_prop.put(getBundleDescription());
        o_prop.put(getProcessToolDeployment());
    }

    private JSONObject getAperteLanguage() throws JSONException, IOException {

        String definitionLanguage = getBpmDefinitionLanguage();

        JSONObject o = new JSONObject();
        o.put("id", "aperte-language");
        o.put("type", "String");
        o.put("title", "BPM language");
        o.put("value", definitionLanguage);
        o.put("description", "BPM language");
        o.put("readonly", true);
        o.put("optional", false);

//        JSONArray items = new JSONArray();
//        o.put("items" ,items);
//
//        JSONObject cc = new JSONObject();
//        items.put(cc);
//        cc.put("id","z1");
//        cc.put("title",definitionLanguage);
//        cc.put("value",definitionLanguage);
        return o;

    }

    private String getBpmDefinitionLanguage() throws IOException, JSONException {
        String url = props.getServerName() + props.getJbpmGuiUrl() + props.getAperteConfigurationUrl();
        String config = getAperteData(url);

        JSONObject jsonConfig = ((config == null) ? null : new JSONObject(config));
        return jsonConfig != null ? (String) jsonConfig.get("definitionLanguage") : "?";
    }

    private JSONObject getProcessFileName() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id","aperte-process-filename");
        o.put("type","String");
        o.put("title","Aperte process filename");
        o.put("description","Aperte process filename");
        o.put("readonly",false);
        o.put("optional",true);
        return o;
    }

    private JSONObject getProcessId() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id","process-id");
        o.put("type","String");
        o.put("title","Aperte process Id");
        o.put("description","Aperte process Id");
        o.put("readonly",false);
        o.put("optional",true);
        return o;
    }
    
    private JSONObject getBundleName() throws JSONException {
 	    JSONObject o = new JSONObject();
        o.put("id","mf-bundle-name");
        o.put("type","String");
        o.put("title","Manifest: Bundle-Name");
        o.put("description","Manifest: Bundle-Name");
        o.put("readonly",false);
        o.put("optional",true);
        return o;
    }
    
    private JSONObject getBundleDescription() throws JSONException {
  	    JSONObject o = new JSONObject();
        o.put("id","mf-bundle-description");
 		o.put("type","String");
 		o.put("title","Manifest: Bundle-Description");
 		o.put("description","Manifest: Bundle-Description");
 		o.put("readonly",false);
 		o.put("optional",true);
        return o;
     }
    
    private JSONObject getProcessToolDeployment() throws JSONException {
   	    JSONObject o = new JSONObject();
        o.put("id","mf-processtool-deployment");
  	    o.put("type","String");
  	    o.put("title","Manifest: ProcessTool-Process-Deployment");
  		o.put("description","Manifest: ProcessTool-Process-Deployment");
  		o.put("readonly",false);
  		o.put("optional",true);
        return o;
    }

    private void modifyTaskProperties(JSONArray properties) throws IOException,JSONException {
        JSONObject obj1 = new JSONObject();
        properties.put(obj1);

        JSONArray o_roles= new JSONArray();
        o_roles.put("Task");
        obj1.put("roles",o_roles);

        JSONArray o_prop= new JSONArray();
        obj1.put("properties",o_prop);

        o_prop.put(getAperteConf());
        
        JSONObject o = new JSONObject();
        o.put("id","description");
	    o.put("type","String");
	    o.put("title","Description");
	    o.put("description","Task description");
	    o.put("readonly",false);
	    o.put("optional",true);
	    o_prop.put(o);

        o = new JSONObject();
        JSONArray complexItems = new JSONArray();
   	    o.put("id","step-permissions");    o.put("type","Complex");
   	    o.put("title","Step permissions"); o.put("description","Step permissions");
   	    o.put("readonly",true);  o.put("optional",true);
   	    o.put("complexItems", complexItems);
	    o_prop.put(o);

        o_prop.put(getAperteTaskTypes());
    }
    
    private void modifyEndProperties(JSONArray properties) throws IOException,JSONException {
        JSONObject obj1 = new JSONObject();
        properties.put(obj1);

        JSONArray o_roles= new JSONArray();
        o_roles.put("EndNoneEvent");
        obj1.put("roles",o_roles);

        JSONArray o_prop= new JSONArray();
        obj1.put("properties",o_prop);

        o_prop.put(getAperteConf());
        
        JSONObject o = new JSONObject();
        o.put("id","EndNoneEvent");
	    o.put("type","String");
	    o.put("title","EndNoneEvent");
	    o.put("value","EndNoneEvent");
	    o.put("description","EndNoneEvent description");
	    o.put("readonly",false);
	    o.put("optional",true);
	    o.put("refToView","");
	    o_prop.put(o);
	    
	     o = new JSONObject();
        o.put("id","tasktype");
        o.put("type","String");
        o.put("title","Tasktype");
        o.put("title_de","Tasktyp");
        o.put("value","User");
        o.put("description","Defines the tasks type which is shown in the left upper corner of the task.");
        o.put("description_de","Definiert den Aufgabentyp, der in der linken oberen Ecke der Task angezeigt wird.");
        o.put("readonly",false);
        o.put("optional",false);
        o.put("refToView","");

        o_prop.put(o);
    }
    
    private void modifySequenceFlowProperties(JSONArray properties) throws IOException,JSONException {
    	JSONObject obj1 = new JSONObject();
        properties.put(obj1);

        JSONArray o_roles= new JSONArray();
        o_roles.put("SequenceFlow");
        obj1.put("roles",o_roles);

        JSONArray o_prop= new JSONArray();
        obj1.put("properties",o_prop);

        	    
	    o_prop.put(getAperteButtons());
	    
	    JSONObject o = new JSONObject();
	    JSONArray complexItems = new JSONArray();
	    o.put("id","action-attributes");    o.put("type","Complex");
	    o.put("title","Action attributes"); o.put("description","Action attributes");
	    o.put("readonly",false);  o.put("optional",true);
	    o.put("complexItems", complexItems);
	    o_prop.put(o);
	    
	    JSONObject complexItem = new JSONObject();
	    //note: signavio does not allow for the '-' sign to appear in 'id' 
	    complexItem.put("id","attributename");	    complexItem.put("name","Attribute name");
	    complexItem.put("type","String");   complexItem.put("value","");
	    complexItem.put("width",100);       complexItem.put("optional",true);
	    complexItems.put(complexItem);
	    
	    complexItem = new JSONObject();
	    complexItem.put("id","attributevalue");      complexItem.put("name","Attribute value");
	    complexItem.put("type","String");   complexItem.put("value","");
	    complexItem.put("width",100);	    complexItem.put("optional",true);
	    complexItems.put(complexItem);
	    
	    o = new JSONObject();
	    complexItems = new JSONArray();
	    o.put("id","action-permissions");    o.put("type","Complex");
	    o.put("title","Action permissions"); o.put("description","Action permissions");
	    o.put("readonly",false);  o.put("optional",true);
	    o.put("complexItems", complexItems);
	    o_prop.put(o);
	    
	    complexItem = new JSONObject();
	    complexItem.put("id","rolename");	    complexItem.put("name","Role name");
	    complexItem.put("type","String");   complexItem.put("value",".*");
	    complexItem.put("width",100);       complexItem.put("optional",true);
	    complexItems.put(complexItem);
	    
	    o = new JSONObject();
        o.put("id","action-properties");
	    o.put("type","String");
	    o.put("title","Action autowired properties");
	    o.put("description","Action autowired properties");
	    o.put("readonly",true);
	    o.put("optional",true);
	    o_prop.put(o);
    }

    private void setGlobalParams(JSONObject root) throws JSONException {
        root.put("title", "Aperte Core Elements");
        root.put("title_de","Aperte Core Elements");
        root.put("namespace","http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#");
        root.put("description","A basic subset of BPMN 2.0 containing only task, sequence flow, start event, end event, parallel gateway and data-based XOR.");
        root.put("extends","http://b3mn.org/stencilset/bpmn2.0#");
    }

    private void addRules(JSONObject root) throws JSONException {
        JSONObject rules = new JSONObject();
        root.put("rules",rules);
        rules.put("connectionRules", new JSONArray());
        rules.put("cardinalityRules", new JSONArray());
        rules.put("containmentRules", new JSONArray());
    }

    private void removeStencils(JSONObject root) throws JSONException {
        JSONArray removestencils = new JSONArray();
        root.put("removestencils",removestencils);
        removestencils.put("ITSystem");
        removestencils.put("EventSubprocess");
        removestencils.put("CollapsedEventSubprocess");
        removestencils.put("Subprocess");
        removestencils.put("DataStore");
        removestencils.put("Message");
        removestencils.put("StartErrorEvent");
        removestencils.put("StartCompensationEvent");
        removestencils.put("StartParallelMultipleEvent");
        removestencils.put("StartEscalationEvent");
        removestencils.put("IntermediateParallelMultipleEventCatching");
        removestencils.put("IntermediateEscalationEvent");
        removestencils.put("EndEscalationEvent");
        removestencils.put("IntermediateEscalationEventThrowing");
        removestencils.put("EventbasedGateway");
        removestencils.put("InclusiveGateway");
        removestencils.put("ComplexGateway");
        removestencils.put("CollapsedPool");
        removestencils.put("Pool");
        removestencils.put("Lanes");
        removestencils.put("MessageFlow");
        removestencils.put("processparticipant");
        removestencils.put("Group");
        removestencils.put("TextAnnotation");
        removestencils.put("DataObject");
        removestencils.put("StartConditionalEvent");
        removestencils.put("StartSignalEvent");
        removestencils.put("StartMultipleEvent");
        removestencils.put("IntermediateEvent");
        removestencils.put("IntermediateMessageEventCatching");
        removestencils.put("IntermediateMessageEventThrowing");
        //removestencils.put("IntermediateTimerEvent");
        //removestencils.put("IntermediateErrorEvent");
        removestencils.put("IntermediateCancelEvent");
        removestencils.put("IntermediateCompensationEventCatching");
        removestencils.put("IntermediateCompensationEventThrowing");
        removestencils.put("IntermediateConditionalEvent");
       // removestencils.put("IntermediateSignalEventCatching");
        removestencils.put("IntermediateSignalEventThrowing");
        removestencils.put("IntermediateMultipleEventCatching");
        removestencils.put("IntermediateMultipleEventThrowing");
        removestencils.put("IntermediateLinkEventCatching");
        removestencils.put("IntermediateLinkEventThrowing");
        removestencils.put("EndMessageEvent");
        //removestencils.put("EndErrorEvent");
        removestencils.put("EndCancelEvent");
        removestencils.put("EndCompensationEvent");
        removestencils.put("EndSignalEvent");
        removestencils.put("EndMultipleEvent");
        removestencils.put("EndTerminateEvent");
        removestencils.put("Association_Undirected");
        removestencils.put("Association_Unidirectional");
        removestencils.put("Association_Bidirectional");
        //removestencils.put("CollapsedSubprocess");
        //removestencils.put("StartTimerEvent");
      //  removestencils.put("MessageFlow");
        removestencils.put("StartMessageEvent");
        root.put("removeproperties",new JSONArray() );
    }

    private String getAperteData(String aperteUrl) throws IOException {
        try {
        	URL url = new URL(aperteUrl);
	        URLConnection conn = url.openConnection();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        StringBuffer sb = new StringBuffer();
	        String line;
	        while ((line = rd.readLine()) != null) {
	            sb.append(line);
	        }
	        rd.close();
	        return sb.toString();
        } catch (IOException e) {
        	logger.error("Error reading data from " + aperteUrl, e);
        	return null;
        }
    }
    
    private JSONObject getAperteConf() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id","aperte-conf");
        o.put("type","String");
        o.put("title","Aperte Configuration");
        o.put("description","Extended configuration for aperte workflow");
        o.put("readonly",true);
        o.put("optional",true);
        return o;
    }
	
    
    private JSONObject getProcessConf() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", "process-conf");
        o.put("type", "String");
        o.put("title", "Aperte process configuration");
        o.put("description", "Aperte process configuration");
        o.put("readonly", true);
        o.put("optional", true);
        return o;
    }
    
    private JSONObject getAperteTaskTypes() throws IOException, JSONException {
        JSONObject o = new JSONObject();
        o.put("id","tasktype");
        o.put("type","Choice");
        o.put("title","Tasktype");
        o.put("title_de","Tasktyp");
        o.put("value","User");
        o.put("description","Defines the tasks type which is shown in the left upper corner of the task.");
        o.put("description_de","Definiert den Aufgabentyp, der in der linken oberen Ecke der Task angezeigt wird.");
        o.put("readonly",false);
        o.put("optional",false);
        o.put("refToView","");
        JSONArray items = new JSONArray();
        o.put("items" ,items);

        JSONObject c4 = new JSONObject();
        items.put(c4);
        c4.put("id","c4");
        c4.put("title","User");
        c4.put("title_de","Benutzer");
        c4.put("value","User");
        c4.put("icon" , "activity/list/type.user.png");
        c4.put("refToView","userTask");


        String stepListUrl = props.getServerName() + props.getJbpmGuiUrl() + props.getAperteStepListUrl();
        String stepList = getAperteData(stepListUrl);

        JSONArray pjo = ((stepList == null) ? null : new JSONArray(stepList));

        if (pjo != null) {
            for(int i=0;i<pjo.length();i++){
                JSONObject oo = (JSONObject) pjo.get(i);
                String name = oo.get("name").toString();

                JSONObject cc = new JSONObject();
                items.put(cc);
                cc.put("id","c"+(10+i));
                cc.put("title",name);
                cc.put("title_de",name);
                cc.put("value",name);
                cc.put("icon" , "activity/list/type.service.png");
                cc.put("refToView","serviceTask");
            }
        }
        return o;
    }
     
     private JSONObject getAperteButtons() throws IOException, JSONException {
         JSONObject o = new JSONObject();
         o.put("id","button-type");
		 o.put("type","Choice");
		 o.put("title","Button type");
		 o.put("title_de","Button type");
		 o.put("value","Default");
		 o.put("description","Defines button types.");
		 o.put("description_de","Defines button types.");
		 o.put("readonly",false);
		 o.put("optional",false);
		 o.put("refToView","");
         JSONArray items = new JSONArray();
         o.put("items" ,items);
       	 
         String buttonListUrl = props.getServerName() + props.getJbpmGuiUrl() + props.getAperteButtonListUrl();
	     String buttonList = getAperteData(buttonListUrl);
	    	
	     JSONArray pjo = ((buttonList == null) ? null : new JSONArray(buttonList));
			
         if (pjo != null) { 
			 for(int i=0;i<pjo.length();i++) {
                String name = (String)pjo.get(i);

                JSONObject cc = new JSONObject();
                items.put(cc);
                cc.put("id","x"+(10+i));
			    cc.put("title",name);
			    cc.put("title_de",name);
			    cc.put("value",name);
		        cc.put("icon" , "activity/list/type.service.png");
			    cc.put("refToView","serviceTask");
             }
         }
        
         return o;
    }
    
}

