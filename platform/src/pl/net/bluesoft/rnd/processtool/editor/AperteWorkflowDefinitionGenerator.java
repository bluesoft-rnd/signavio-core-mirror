package pl.net.bluesoft.rnd.processtool.editor;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.exceptions.RequestException;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Bpmn2XmlConverter;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2_0.transformation.Diagram2XmlConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aperteworkflow.editor.domain.Permission;
import org.aperteworkflow.editor.domain.ProcessConfig;
import org.aperteworkflow.editor.domain.Queue;
import org.aperteworkflow.editor.domain.QueueRolePermission;
import org.aperteworkflow.editor.json.ProcessConfigJSONHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.xml.sax.SAXException;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.exception.UnsupportedJPDLObjectException;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.*;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class AperteWorkflowDefinitionGenerator {

    //key = resourceId
    private Map<String, JPDLComponent> componentMap = new HashMap<String, JPDLComponent>();
    private Map<String, JPDLTransition> transitionMap = new HashMap<String, JPDLTransition>();

    private String json;

    private ProcessConfig processConfig;

    private final Logger logger = Logger.getLogger(AperteWorkflowDefinitionGenerator.class);

    private String processName;
    private String processFileName;
    private String bundleDesc;
    private String bundleName;
    private String processToolDeployment;
    private int offsetY;
    private int offsetX;
    private String processDefinitionLanguage;

    public AperteWorkflowDefinitionGenerator(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


    public void init(String json) {
        try {
            this.json = json;
            JSONObject jsonObj = enrichModelerDataForBpmn20();
//            JSONObject jsonObj = new JSONObject(json);
            processName = jsonObj.getJSONObject("properties").getString("name");
            processFileName = jsonObj.getJSONObject("properties").optString("aperte-process-filename");
            bundleDesc = jsonObj.getJSONObject("properties").optString("mf-bundle-description");
            bundleName = jsonObj.getJSONObject("properties").optString("mf-bundle-name");
            processToolDeployment = jsonObj.getJSONObject("properties").optString("mf-processtool-deployment");
            PlatformProperties props = Platform.getInstance().getPlatformProperties();
            String aperteData = getAperteData(props.getServerName() + props.getJbpmGuiUrl() + props.getAperteConfigurationUrl());
            processDefinitionLanguage = new JSONObject(aperteData).optString("definitionLanguage");
            //processDefinitionLanguage = jsonObj.getJSONObject("properties").optString("aperte-language");
            if (processDefinitionLanguage == null || "".equals(processDefinitionLanguage)) {
                processDefinitionLanguage = "jpdl";//for old process definitions
            }
            if (StringUtils.isEmpty(processName)) {
                throw new RequestException("Process name is empty.");
            }
            if (StringUtils.isEmpty(processFileName)) {
                throw new RequestException("Aperte process filename is empty.");
            }
            if (StringUtils.isEmpty(bundleName)) {
                throw new RequestException("Manifest Bundle-Name is empty.");
            }
            if (StringUtils.isEmpty(bundleDesc)) {
                throw new RequestException("Manifest Bundle-Description is empty.");
            }
            if (StringUtils.isEmpty(processToolDeployment)) {
                throw new RequestException("Manifest: ProcessTool-Process-Deployment is empty.");
            }


            String processConfJson = jsonObj.getJSONObject("properties").optString("process-conf");
            if (processConfJson != null && !processConfJson.trim().isEmpty()) {
                processConfig = ProcessConfigJSONHandler.getInstance().toObject(processConfJson);
            }

            JSONArray childShapes = jsonObj.getJSONArray("childShapes");
            for (int i = 0; i < childShapes.length(); i++) {
                JSONObject obj = childShapes.getJSONObject(i);
                JPDLObject jpdlObject = JPDLObject.getJPDLObject(obj, this);
                jpdlObject.fillBasicProperties(obj);
                if (jpdlObject instanceof JPDLComponent) {
                        //not needed anymore
//                    ((JPDLComponent) jpdlObject).applyOffset(offsetX, offsetY);
                    componentMap.put(jpdlObject.getResourceId(), (JPDLComponent) jpdlObject);
                } else if (jpdlObject instanceof JPDLTransition) {
                    transitionMap.put(jpdlObject.getResourceId(), (JPDLTransition) jpdlObject);
                }
            }
        } catch (JSONException e) {
            logger.error("Error while generating JPDL file.", e);
            throw new RequestException("Error while generating JPDL file.", e);
        } catch (UnsupportedJPDLObjectException e) {
            logger.error("Error while generating JPDL file.", e);
            throw new RequestException(e.getMessage());
        }

        //second pass, complete the transition map
        for (String key : componentMap.keySet()) {
            JPDLComponent cmp = componentMap.get(key);
            for (String resourceId : cmp.getOutgoing().keySet()) {
                JPDLTransition transition = transitionMap.get(resourceId);
                transition.setTargetName(componentMap.get(transition.getTarget()).getName());
                cmp.putTransition(resourceId, transition);
            }
        }

    }

    public String generateDefinition() {
        if ("jpdl".equals(processDefinitionLanguage)) {
            return generateJpdl();
        } else {
            return generateBpmn20();
        }

    }


    public String generateJpdl() {
        StringBuffer jpdl = new StringBuffer();
        jpdl.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        jpdl.append(String.format("<process name=\"%s\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n", processName));

        Set<String> swimlanes = new HashSet<String>();

        for (String key : componentMap.keySet()) {
            JPDLComponent comp = componentMap.get(key);
            if (comp instanceof JPDLUserTask) {
                JPDLUserTask userTask = (JPDLUserTask) comp;
                if (userTask.getSwimlane() != null && userTask.getSwimlane().trim().length() > 0) {
                    swimlanes.add(userTask.getSwimlane());
                }
            }
        }

        for (String sl : swimlanes) {
            jpdl.append(String.format("<swimlane candidate-groups=\"%s\" name=\"%s\"/>\n", sl, sl));
        }


        for (String key : componentMap.keySet()) {
            jpdl.append(componentMap.get(key).toXML());
        }

        jpdl.append("</process>");

        return jpdl.toString();
    }

    public String generateBpmn20() {

        try {
            JSONObject jsonObj = enrichModelerDataForBpmn20();
            String jsonForBpmn20 = jsonObj.toString();
            BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonForBpmn20);

            Diagram2BpmnConverter converter;

            converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
            Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
            ((de.hpi.bpmn2_0.model.Process)bpmnDefinitions.getRootElement().get(0)).setExecutable(true);
            bpmnDefinitions.getRootElement().get(0).setId(processName);
            Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions,
                    Platform.getInstance().getFile("/WEB-INF/xsd/BPMN20.xsd").getAbsolutePath());
            return xmlConverter.getXml().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject enrichModelerDataForBpmn20() throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        JSONArray childShapes = jsonObj.getJSONArray("childShapes");
        Map<String,JSONObject> outgoingMap = new HashMap<String, JSONObject>();
        Map<String,JSONObject> resourceIdMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < childShapes.length(); i++) {
            JSONObject obj = childShapes.getJSONObject(i);
            fixBounds(obj);
            fixDockers(obj);
            resourceIdMap.put(obj.getString("resourceId"), obj);
            if (obj.has("outgoing")) {
                JSONArray outgoing = obj.getJSONArray("outgoing");
                for (int j=0; j <  outgoing.length(); j++) {
                    JSONObject outobj = outgoing.getJSONObject(j);
                    outgoingMap.put(outobj.getString("resourceId"), obj);
                }
            }
        }                //update user step with assignment data
        for (int i = 0; i < childShapes.length(); i++) {
            JSONObject obj = childShapes.getJSONObject(i);
            String stencilId = obj.getJSONObject("stencil").getString("id");

            if ("Task".equals(stencilId)) {
                String taskType = obj.getJSONObject("properties").getString("tasktype");
                if ("User".equals(taskType)) {
                    enrichBpmn20AssignmentConfig(obj, resourceIdMap);
                } else {
                    enrichBpmn20JavaTask(obj, resourceIdMap);
                }
            } else if ("SequenceFlow".equals(stencilId)) {
                enrichBpmn20SequenceFlow(obj, outgoingMap, resourceIdMap);
            } else if ("Exclusive_Databased_Gateway".equals(stencilId)) {

            }
        }
        return jsonObj;
    }

    private void fixDockers(JSONObject obj) throws JSONException {
        JSONArray dockers = obj.optJSONArray("dockers");
        if (dockers == null) return;
        for (int i=0; i < dockers.length(); i++) {
            fixOffset(dockers.getJSONObject(i));
        }
    }

    private void fixBounds(JSONObject obj) throws JSONException {
        JSONObject bounds = obj.optJSONObject("bounds");
        if (bounds == null) return;
        JSONObject lowerRight = bounds.getJSONObject("lowerRight");
        JSONObject upperLeft = bounds.getJSONObject("upperLeft");
        fixOffset(lowerRight);
        fixOffset(upperLeft);
    }

    private void fixOffset(JSONObject point) throws JSONException {
        int x = point.getInt("x");
        int y = point.getInt("y");
        point.put("x", x + offsetX);
        point.put("y", y + offsetY);
    }

    private void enrichBpmn20SequenceFlow(JSONObject obj,
                                          Map<String,JSONObject> outgoingMap,
                                          Map<String,JSONObject> resourceIdMap) throws JSONException {
//        String targetId = obj.getJSONObject("target").getString("resourceId");
//        JSONObject source = outgoingMap.get(obj.getString("resourceId"));


//        JSONArray incoming = obj.getJSONArray("incoming");
//        JSONArray outgoing = obj.getJSONArray("outgoing");
//        JSONObject incomingNode = incoming.getJSONObject(0);
//        JSONObject outgoingNode = outgoing.getJSONObject(0);

    }

    private void enrichBpmn20JavaTask(JSONObject obj, Map<String,JSONObject> resourceIdMap) throws JSONException {
        JSONObject propertiesObj = obj.getJSONObject("properties");
        JSONObject aperteCfg = new JSONObject(propertiesObj.getString("aperte-conf"));

        String stepName = propertiesObj.getString("tasktype");
        if ("bpmn20".equals(processDefinitionLanguage))
            propertiesObj.put("tasktype", "Service");
        propertiesObj.put("activiti_class", "org.aperteworkflow.ext.activiti.ActivitiStepAction");

        JSONArray fields = new JSONArray();
        JSONObject nameField = new JSONObject();
        nameField.put("name", "stepName");
        nameField.put("string_value", stepName);
        fields.put(nameField);
        JSONObject paramsField = new JSONObject();
        paramsField.put("name", "params");

        
        StringBuilder sb = new StringBuilder();
        sb.append("<map>\n");
        Iterator iterator = aperteCfg.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = aperteCfg.getString(key);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    byte[] bytes = Base64.decodeBase64(value.getBytes());
                    value = new String(bytes);
                } catch (Exception e) {
                    //TODO nothing, as some properties are base64, and some are not
                }
                sb.append(String.format("<%s>%s</%s>",
                        key,
                        XmlUtil.encodeXmlEcscapeCharacters(value), //todo use XStream
                        key));
            }
        }
        sb.append("</map>\n");

        paramsField.put("expression", sb.toString());
        fields.put(paramsField);

        propertiesObj.put("activiti_fields", fields);

        processOutgoingConditions(obj, resourceIdMap, propertiesObj, "RESULT");
    }

    private void processOutgoingConditions(JSONObject obj, Map<String, JSONObject> resourceIdMap, JSONObject propertiesObj, String varName) throws JSONException {
        //find outgoing transition to XOR gateway. There should be only one outgoing transition and without a condition
        // If it indeed is a XOR gateway and transition conditions are not specified - fill them with defaults
        JSONArray outgoing = obj.getJSONArray("outgoing");
        if (outgoing.length() > 1) {
            throw new RuntimeException("Java task: " + propertiesObj.optString("name") + " has more than one outgoing transition.");
        }
        for (int i=0; i < outgoing.length(); i++) {
            JSONObject transition = resourceIdMap.get(outgoing.getJSONObject(i).getString("resourceId"));
            JSONObject properties = transition.getJSONObject("properties");
            if (properties.has("conditionexpression")) {
                properties.remove("conditionexpression");
            }
            properties.put("conditiontype", "Default");
            
            String targetId = transition.getJSONObject("target").getString("resourceId");
            JSONObject nextNode = resourceIdMap.get(targetId);
            String stencilId = nextNode.getJSONObject("stencil").getString("id");
            if ("Exclusive_Databased_Gateway".equals(stencilId)) {
                JSONArray xorOutgoing = nextNode.getJSONArray("outgoing");
                for (int j=0; j < xorOutgoing.length(); j++) {
                    JSONObject xorTransition = resourceIdMap.get(xorOutgoing.getJSONObject(j).getString("resourceId"));
                    JSONObject subProperties = xorTransition.getJSONObject("properties");
                    if (xorOutgoing.length() == 1) { //only one outgoing, remove condition and set condition type to default
                        if (subProperties.has("conditionexpression")) {
                            subProperties.remove("conditionexpression");
                        }
                        subProperties.put("conditiontype", "Default");
                    } else {
                        String condition = subProperties.optString("conditionexpression");
                        String conditionType = subProperties.optString("conditiontype");
                        if (!"Default".equals(conditionType) && (condition == null || condition.trim().isEmpty())) {
                            subProperties.put("conditionexpression", String.format("${%s=='%s'}", varName, subProperties.optString("name")));
                            subProperties.put("conditiontype", "Expression");
                        }
                    }
                }
            }
            
        }
    }


    private void enrichBpmn20AssignmentConfig(JSONObject obj, Map<String,JSONObject> resourceIdMap) throws JSONException {
        JSONObject propertiesObj = obj.getJSONObject("properties");
        JSONObject aperteCfg = new JSONObject(propertiesObj.getString("aperte-conf"));
        String assignee = aperteCfg.optString("assignee");
        //It looks like swimlanes are unsupported in Activiti :(
//        String swimlane = obj.optString("swimlane");
        String candidateGroups = aperteCfg.optString("candidate_groups");
        //resources/items[0]/resource_type
        JSONObject resources = new JSONObject();
        JSONArray items = new JSONArray();
        if (assignee != null && !assignee.trim().isEmpty()) {
            JSONObject o = new JSONObject();
            o.put("resource_type", "humanperformer");
            o.put("resourceassignmentexpr", assignee);
            items.put(o);
        }
        if (candidateGroups != null && !candidateGroups.trim().isEmpty()) {
            JSONObject o = new JSONObject();
            o.put("resource_type", "potentialowner");
            o.put("resourceassignmentexpr", candidateGroups);
            items.put(o);
        }
        resources.put("items", items);
        resources.put("totalCount", items.length());
        if (items.length() != 0) {
            if (propertiesObj.opt("resources") != null)
                propertiesObj.remove("resources");
            propertiesObj.put("resources", resources);//overwrite
        }
        propertiesObj.put("implementation", "unspecified");
        processOutgoingConditions(obj, resourceIdMap, propertiesObj, "ACTION");
        
    }

    public String generateProcessToolConfig() {
        StringBuffer ptc = new StringBuffer();
        ptc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        ptc.append(String.format("<config.ProcessDefinitionConfig bpmDefinitionKey=\"%s\" description=\"%s\" processName=\"%s\"  taskItemClass=\"%s\">\n", processName, processName, processName, processConfig.getTaskItemClass()));

        if (processConfig != null) {

            if (processConfig.getComment() != null && !processConfig.getComment().isEmpty()) {
                ptc.append(String.format("<comment>%s</comment>", XmlUtil.wrapCDATA(processConfig.getComment())));
            }

            if (processConfig.getProcessPermissions() != null && !processConfig.getProcessPermissions().isEmpty()) {
                ptc.append("<permissions>\n");

                for (Permission permission : processConfig.getProcessPermissions()) {
                    ptc.append(String.format("<config.ProcessDefinitionPermission privilegeName=\"%s\" roleName=\"%s\"/>", permission.getPrivilegeName(), permission.getRoleName()));
                }

                ptc.append("</permissions>\n");
            }

        }

        ptc.append("<states>\n");

        //processtool-config.xml generation
        for (String key : componentMap.keySet()) {
            JPDLComponent cmp = componentMap.get(key);
            if (cmp instanceof JPDLUserTask) {
                JPDLUserTask task = (JPDLUserTask) cmp;
                if (task.getWidget() != null) {
                    ptc.append(task.generateWidgetXML());
                }
            }
            if (cmp instanceof JPDLEndEvent) {
            	JPDLEndEvent task = (JPDLEndEvent) cmp;
                if (task.getWidget() != null) {
                    ptc.append(task.generateWidgetXML());
                }
            }
            
            
        }

        ptc.append("</states>\n");
        ptc.append("</config.ProcessDefinitionConfig>\n");
        return ptc.toString();
    }

    public JPDLComponent findComponent(String key) {
        return componentMap.get(key);
    }

    public String generateQueuesConfig() {

        StringBuffer q = new StringBuffer();
        q.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        q.append("<list>\n");

        if (processConfig != null) {
            if (processConfig.getQueues() != null && !processConfig.getQueues().isEmpty()) {
                for (Queue queue : processConfig.getQueues()) {
                    String description = queue.getDescription();
                    if (description == null) {
                        description = queue.getName();
                    }

                    q.append(String.format(
                            "<config.ProcessQueueConfig name=\"%s\" description=\"%s\">\n",
                            queue.getName(),
                            description
                    ));
                    q.append("<rights>\n");

                    if (queue.getRolePermissions() != null && !queue.getRolePermissions().isEmpty()) {
                        for (QueueRolePermission rolePermission : queue.getRolePermissions()) {
                            q.append(String.format(
                                    "<config.ProcessQueueRight roleName=\"%s\" browseAllowed=\"%b\"/>\n",
                                    rolePermission.getRoleName(),
                                    rolePermission.isBrowsingAllowed()
                            ));
                        }
                    }

                    q.append("</rights>\n");
                    q.append("</config.ProcessQueueConfig>\n");
                }
            }
        }

        q.append("</list>\n");
        return q.toString();
    }

    public String getProcessDefinitionLanguage() {
        return processDefinitionLanguage;
    }

    public String getProcessName() {
        return processName;
    }

    public String getProcessFileName() {
        return processFileName;
    }

    public String getBundleDesc() {
        return bundleDesc;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getProcessToolDeployment() {
        return processToolDeployment;
    }

    public Map<String, String> getMessages() {
        if (processConfig == null) {
            return null;
        }
        return processConfig.getMessages();
    }
    
    public String getDictionary() {
        if (processConfig == null) {
            return null;
        }
        return processConfig.getDictionary();
    }

	public String getDefaultLanguage() {
		return processConfig.getDefaultLanguage();
	}

    public byte[] getProcessIcon() {
        if (processConfig == null) {
            return null;
        }
        return processConfig.getProcessIcon();
    }


    private String getAperteData(String aperteUrl)  {
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
            throw new RuntimeException(e);
        }
    }

}

