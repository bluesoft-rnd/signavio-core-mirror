package pl.net.bluesoft.rnd.processtool.editor;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.exceptions.RequestException;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.jbpm5.ImportClass;
import de.hpi.bpmn2_0.model.misc.ItemKind;
import de.hpi.bpmn2_0.model.misc.Property;
import de.hpi.bpmn2_0.transformation.Bpmn2XmlConverter;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
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
import pl.net.bluesoft.rnd.processtool.editor.jpdl.components.ConditionProperties;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.exception.UnsupportedJPDLObjectException;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.AperteComponent;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.AperteObject;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.AperteStepEditorNode;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.AperteTransition;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AperteWorkflowDefinitionGenerator {


    private final Logger logger = Logger.getLogger(AperteWorkflowDefinitionGenerator.class);
    private String AUTO_STEP_ACTION_CLASS = Platform.getInstance().getPlatformProperties().getAperteStepActionClass();
    private String AUTO_STEP_ACTION_CLASS_PATH = Platform.getInstance().getPlatformProperties().getAperteStepActionClassPackage() + "." + AUTO_STEP_ACTION_CLASS;
    private Map<String, AperteComponent> componentMap = new HashMap<String, AperteComponent>();
    private Map<String, AperteTransition> transitionMap = new HashMap<String, AperteTransition>();
    private String json;
    private ProcessConfig processConfig;
    private String processName;
    //private String processId;
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
            processName = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).getString("name");
            processFileName = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("aperte-process-filename");
            bundleDesc = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("mf-bundle-description");
            bundleName = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("mf-bundle-name");
            processToolDeployment = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("mf-processtool-deployment");
            PlatformProperties props = Platform.getInstance().getPlatformProperties();
            String aperteData = getAperteData(props.getServerName() + props.getJbpmGuiUrl() + props.getAperteConfigurationUrl());
            processDefinitionLanguage = new JSONObject(aperteData).optString("definitionLanguage");
            //processDefinitionLanguage = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("aperte-language");
            if (processDefinitionLanguage == null || "".equals(processDefinitionLanguage)) {
                processDefinitionLanguage = "bpmn20";//for old process definitions
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
            if (StringUtils.contains(processToolDeployment, ' ')) {
                throw new RequestException("Manifest: ProcessTool-Process-Deployment cannot have spaces in name.");
            }


            String processConfJson = jsonObj.getJSONObject(JsonConstants.PROPERTIES.getName()).optString("process-conf");
            if (processConfJson != null && !processConfJson.trim().isEmpty()) {
                processConfig = ProcessConfigJSONHandler.getInstance().toObject(processConfJson);
            }

            JSONArray childShapes = jsonObj.getJSONArray("childShapes");
            for (int i = 0; i < childShapes.length(); i++) {
                JSONObject obj = childShapes.getJSONObject(i);

                //BPMN preparation
                AperteObject aperteObject = AperteObject.getJPDLObject(obj, this);

                aperteObject.fillBasicProperties(obj);
                // checkIfTheNamesAreRepeated(componentMap, aperteObject);


                if (aperteObject instanceof AperteComponent) {
                    componentMap.put(aperteObject.getResourceId(), (AperteComponent) aperteObject);
                } else if (aperteObject instanceof AperteTransition) {
                    AperteTransition transition = (AperteTransition) aperteObject;
                    transitionMap.put(aperteObject.getResourceId(), transition);
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
            AperteComponent cmp = componentMap.get(key);
            for (String resourceId : cmp.getOutgoing().keySet()) {
                AperteTransition transition = transitionMap.get(resourceId);
                transition.setTargetName(componentMap.get(transition.getTarget()).getName());

                AperteComponent jpdlComponent = componentMap.get(transition.getTarget());

                cmp.putTransition(resourceId, transition);
                jpdlComponent.putIncomingTransition(resourceId, transition);
            }

        }
    }
       //TODO remove after tests
    /*private void checkIfTheNamesAreRepeated(Map componentMap, final AperteObject obj) {
        AperteTask jtask;
        if (obj instanceof AperteTask && !(obj instanceof AperteEndEvent)) {
            Collection values = componentMap.values();
            for (Object object : values) {
                if (object instanceof AperteTask && !(object instanceof AperteEndEvent)) {
                    jtask = (AperteTask) object;
                    if (jtask.getName().equals(obj.getName())) {
                        throw new RequestException("Name: '" + obj.getName()
                                + "' is duplicated. Change name it.");

                    }
                }
            }
        }
    }*/

    public String generateDefinition() {
        return generateBpmn20();
    }

    public String generateBpmn20() {

        try {
            JSONObject jsonObj = enrichModelerDataForBpmn20();
            String jsonForBpmn20 = jsonObj.toString();
            Definitions bpmnDefinitions = prepareDefinitions(jsonForBpmn20);
            List<Property> propertyList = preparePropertyList(jsonForBpmn20);
            enrichtDefinitions(propertyList, bpmnDefinitions);
            return convertDiagramToXml(bpmnDefinitions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Definitions prepareDefinitions(String jsonForBpmn20) throws JSONException, BpmnConverterException {
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonForBpmn20);
        Diagram2BpmnConverter converter;
        Platform.getInstance().getPlatformProperties();


        converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        return converter.getDefinitionsFromDiagram();
    }

    public Set<String> getSubProcessNamesFromJson(String json) throws JSONException {
        Set<String> subprocessNames = new HashSet<String>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray childShapes = jsonObj.getJSONArray("childShapes");
        for (int i = 0; i < childShapes.length(); i++) {
            JSONObject obj = childShapes.getJSONObject(i);
            String stencilId = obj.getJSONObject("stencil").getString("id");
            if (TaskType.COLAPSED_SUBPROCESS.getName().equals(stencilId)) {
                String taskName = getProcessName(obj);
                subprocessNames.add(taskName);
            }
        }
        return subprocessNames;
    }

    private String getProcessName(JSONObject obj) throws JSONException {
        JSONObject propertiesObj = obj.getJSONObject(JsonConstants.PROPERTIES.getName());
        return propertiesObj.optString("entry");
    }

    private JSONObject enrichModelerDataForBpmn20() throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        JSONArray childShapes = jsonObj.getJSONArray("childShapes");
        Map<String, JSONObject> outgoingMap = new HashMap<String, JSONObject>();
        Map<String, JSONObject> resourceIdMap = new HashMap<String, JSONObject>();
        for (int i = 0; i < childShapes.length(); i++) {
            JSONObject obj = childShapes.getJSONObject(i);
            fixBounds(obj);
            fixDockers(obj);
            resourceIdMap.put(obj.getString("resourceId"), obj);
            if (obj.has("outgoing")) {
                JSONArray outgoing = obj.getJSONArray("outgoing");
                for (int j = 0; j < outgoing.length(); j++) {
                    JSONObject outobj = outgoing.getJSONObject(j);
                    outgoingMap.put(outobj.getString("resourceId"), obj);
                }
            }
        }                //update user step with assignment data
        for (int i = 0; i < childShapes.length(); i++) {
            JSONObject obj = childShapes.getJSONObject(i);
            String stencilId = obj.getJSONObject("stencil").getString("id");
            if ("Task".equals(stencilId)) {
                String taskType = obj.getJSONObject(JsonConstants.PROPERTIES.getName()).getString("tasktype");
                if (TaskType.USER.getName().equals(taskType)) {
                    enrichBpmn20AssignmentConfig(obj, resourceIdMap);
                } else {
                    enrichBpmn20JavaTask(obj, resourceIdMap);
                }
            } else if (TaskType.COLAPSED_SUBPROCESS.getName().equals(stencilId)) {
                makeSubProcesCompatibleWithJbpm5(obj);
            }
        }
        return jsonObj;
    }

    private void fixDockers(JSONObject obj) throws JSONException {
        JSONArray dockers = obj.optJSONArray("dockers");
        if (dockers == null) return;
        for (int i = 0; i < dockers.length(); i++) {
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

    private void makeSubProcesCompatibleWithJbpm5(JSONObject obj) throws JSONException {
        JSONObject propertiesObj = obj.getJSONObject(JsonConstants.PROPERTIES.getName());
        boolean isCallActivity = Boolean.parseBoolean(propertiesObj.optString(JsonConstants.CALL_ACTIVITY.getName()));
        if (!isCallActivity) {
            propertiesObj.put(JsonConstants.CALL_ACTIVITY.getName(), "true");
        }
    }

    private void enrichBpmn20JavaTask(JSONObject obj, Map<String, JSONObject> resourceIdMap) throws JSONException {


        JSONObject propertiesObj = obj.getJSONObject(JsonConstants.PROPERTIES.getName());
        JSONObject aperteCfg = new JSONObject(propertiesObj.getString("aperte-conf"));

        String stepName = propertiesObj.getString("tasktype");
        if ("bpmn20".equals(processDefinitionLanguage))
            propertiesObj.put("tasktype", "Script");
        String attributeMap = generateAttributeMap(aperteCfg);
        String script = generateScript(stepName, attributeMap);
        propertiesObj.put("script", script);
        processOutgoingConditions(obj, resourceIdMap, propertiesObj, ConditionProperties.RESULT);
    }

    private String generateAttributeMap(JSONObject aperteCfg) throws JSONException {
        StringBuilder scriptAttributeMap = new StringBuilder();
        Iterator keys = aperteCfg.keys();
        if (keys.hasNext()) {
            String comma = "";
            scriptAttributeMap.append("[");
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = decodeValues(aperteCfg.getString(key));
                scriptAttributeMap.append(comma + "'" + key + "' : " + "'" + value + "'");
                comma = ",";
            }
            scriptAttributeMap.append("]");
        }
        String script = scriptAttributeMap.toString();
        if(script==null || script.isEmpty()){
            script="null";
        }
        return script;
    }

    private String generateScript(String taskName, String attributeMap) {
        StringBuilder script = new StringBuilder();
        script.append("jbpmStepAction = new " + AUTO_STEP_ACTION_CLASS + "();");
        script.append("\n");
        script.append(" processId =kcontext.getProcessInstance().getId();");
        script.append("\n");
        script.append(" processIdString =String.valueOf(processId);");
        script.append("\n");
        script.append("jbpmStepAction.invoke(processIdString,'" + taskName + "'," + attributeMap + ");");
        return script.toString();
    }

    private String decodeValues(String codedString) {
        if (codedString != null && !codedString.trim().isEmpty()) {
            try {
                byte[] bytes = Base64.decodeBase64(codedString.getBytes());
                codedString = new String(bytes);
            } catch (Exception e) {
                //TODO nothing, as some properties are base64, and some are not
            }
        }
        return codedString;

    }

    private List<Property> preparePropertyList(String jsonForBpmn20) {
        List<Property> propertyList = new ArrayList<Property>();
        Set<String> lisOfVariables = findAllVariables(jsonForBpmn20);
        lisOfVariables.add(ConditionProperties.ACTION);
        lisOfVariables.add(ConditionProperties.RESULT);
        lisOfVariables.add(ConditionProperties.CONDITION);
        for (String variable : lisOfVariables) {
            Property property = new Property();
            property.setId(variable);
            property.setItemSubjectRef(ItemKind._STRING);
            propertyList.add(property);
        }


        return propertyList;
    }

    private Set<String> findAllVariables(String jsonForBpmn20) {
        Set<String> variables = new HashSet<String>();
        String regex = "#\\{[a-zA-Z0-9]*\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonForBpmn20);
        while (matcher.find()) {
            String group = matcher.group();
            group = removeSufixAndPrefix(group);
            variables.add(group);
        }
        return variables;
    }

    private String removeSufixAndPrefix(String group) {
        return group.substring(2, group.length() - 1);
    }

    private void enrichtDefinitions(List<Property> propertyList, Definitions bpmnDefinitions) {
        Process process = getProcess(bpmnDefinitions);
        addAutoStepClassImport(process);
        process.setExecutable(true);
        process.setId(processName);
        process.getProperty().addAll(propertyList);
    }

    private String convertDiagramToXml(Definitions bpmnDefinitions) throws JAXBException, SAXException, ParserConfigurationException, TransformerException {
        Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions,
                Platform.getInstance().getFile("/WEB-INF/xsd/BPMN20.xsd").getAbsolutePath());

        return xmlConverter.getXml().toString();
    }

    private Process getProcess(Definitions bpmnDefinitions) {
        List<BaseElement> rootElements = bpmnDefinitions.getRootElement();
        for (BaseElement rootElement : rootElements) {
            boolean b = rootElement instanceof Process;
            if (b) {
                return (Process) rootElement;
            }
        }
        return null;

    }

    private void addAutoStepClassImport(Process process) {
      //  if (containsScriptTask(process)) {
            ExtensionElements extensionElements = new ExtensionElements();
            extensionElements.add(new ImportClass(AUTO_STEP_ACTION_CLASS_PATH));
            process.setExtensionElements(extensionElements);
      //  }
    }

    private boolean containsScriptTask(Process process) {
        List<BaseElement> childs = process.getChilds();
        for (BaseElement child : childs) {
            if (child instanceof ScriptTask) {
                return true;
            }
        }
        return false;
    }

    private void processOutgoingConditions(JSONObject obj, Map<String, JSONObject> resourceIdMap, JSONObject propertiesObj, String varName) throws JSONException {
        //find outgoing transition to XOR gateway. There should be only one outgoing transition and without a condition
        // If it indeed is a XOR gateway and transition conditions are not specified - fill them with defaults
        JSONArray outgoing = obj.getJSONArray("outgoing");
        if (outgoing.length() > 1) {
            throw new RuntimeException("Java task: " + propertiesObj.optString("name") + " has more than one outgoing transition.");
        }
        for (int i = 0; i < outgoing.length(); i++) {
            JSONObject transition = resourceIdMap.get(outgoing.getJSONObject(i).getString("resourceId"));
            JSONObject properties = transition.getJSONObject(JsonConstants.PROPERTIES.getName());
            if (properties.has("conditionexpression")) {
                properties.remove("conditionexpression");
            }
            properties.put("conditiontype", "Default");

            String targetId = transition.getJSONObject("target").getString("resourceId");
            JSONObject nextNode = resourceIdMap.get(targetId);
            String stencilId = nextNode.getJSONObject("stencil").getString("id");
            if ("Exclusive_Databased_Gateway".equals(stencilId)) {
                JSONArray xorOutgoing = nextNode.getJSONArray("outgoing");
                for (int j = 0; j < xorOutgoing.length(); j++) {
                    JSONObject xorTransition = resourceIdMap.get(xorOutgoing.getJSONObject(j).getString("resourceId"));
                    JSONObject subProperties = xorTransition.getJSONObject(JsonConstants.PROPERTIES.getName());
                    if (xorOutgoing.length() == 1) { //only one outgoing, remove condition and set condition type to default
                        if (subProperties.has("conditionexpression")) {
                            subProperties.remove("conditionexpression");
                        }
                        subProperties.put("conditiontype", "Default");
                    } else {
                        String condition = subProperties.optString("conditionexpression");
                        String conditionType = subProperties.optString("conditiontype");
                        if (!"Default".equals(conditionType) && (condition == null || condition.trim().isEmpty())) {
                            subProperties.put("conditionexpression", String.format("%s=='%s'", varName, subProperties.optString("name")));
                            subProperties.put("conditiontype", "Expression");
                        }
                    }
                }
            }

        }
    }

    private void enrichBpmn20AssignmentConfig(JSONObject obj, Map<String, JSONObject> resourceIdMap) throws JSONException {
        JSONObject propertiesObj = obj.getJSONObject(JsonConstants.PROPERTIES.getName());
        JSONObject aperteCfg = new JSONObject(propertiesObj.getString("aperte-conf"));
        String assignee = aperteCfg.optString("assignee");
        String taskName = propertiesObj.optString("name");
        ArrayList<String> singleAddress = new ArrayList<String>();
        String priority = aperteCfg.optString("priority");
        String candidateGroups = aperteCfg.optString("candidate_groups");
        JSONObject resources = new JSONObject();
        JSONArray items = new JSONArray();
        if (assignee != null && !assignee.trim().isEmpty()) {
            JSONObject o = new JSONObject();
            o.put("resource_type", "potentialOwner");
            o.put("resourceassignmentexpr", assignee);
            items.put(o);
        }
        if (candidateGroups != null && !candidateGroups.trim().isEmpty()) {
            JSONObject o = new JSONObject();
            o.put("resource_type", "potentialOwner");
            o.put("resourceassignmentexpr", candidateGroups);
            items.put(o);
        }
        // if (taskName != null && !taskName.trim().isEmpty()) {
        JSONObject ioSpecification = new JSONObject();
        ioSpecification.put("resource_type", "ioSpecification");
        ioSpecification.put("resource_type", "dataInput");
        items.put(ioSpecification);
        //  }
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
        ptc.append(String.format("<config.ProcessDefinitionConfig bpmDefinitionKey=\"%s\" description=\"%s\" processName=\"%s\"", processName, processName, processName));

        if (processConfig != null) {

            ptc.append(String.format(" taskItemClass=\"%s\">\n", processConfig.getTaskItemClass()));

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

        } else {
            ptc.append(String.format(">\n"));
        }

        ptc.append("<states>\n");

        //processtool-config.xml generation
        for (String key : componentMap.keySet()) {
            AperteComponent cmp = componentMap.get(key);
            if (cmp instanceof AperteStepEditorNode) {
                AperteStepEditorNode task = (AperteStepEditorNode) cmp;
                if (task.getWidget() != null) {
                    ptc.append(task.generateWidgetXML());
                }
            }
        }

        ptc.append("</states>\n");
        ptc.append("</config.ProcessDefinitionConfig>\n");
        return ptc.toString();
    }

    public AperteComponent findComponent(String key) {
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

    private String insertProcessId(String dict) {
        if (dict != null) {
            Pattern pattern = Pattern.compile("<process-dictionaries([^>]*)>");
            Matcher m = pattern.matcher(dict);

            if (m.find()) {

                String group = m.group(1);
                String toInsert = " processBpmDefinitionKey=\"" + processName
                        + "\"";
                if (group.contains("processBpmDefinitionKey=")) {

                    toInsert = replaceProcessIdIfExists(group, toInsert);
                } else {
                    toInsert += group;

                }

                return dict.substring(0, m.start(1)) + toInsert
                        + dict.substring(m.end(1));
            }
            return dict;
        }
        return null;
    }

    private String replaceProcessIdIfExists(String group, String newProcessId) {
        Pattern pattern = Pattern.compile("processBpmDefinitionKey=\"[A-Za-z]*\"");
        Matcher mSmall = pattern.matcher(group);
        if (mSmall.find()) {
            return mSmall.replaceAll(newProcessId);
        }
        return newProcessId;
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

    private String getAperteData(String aperteUrl) {
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

    public List<String> getSubprocessName() throws JSONException, BpmnConverterException {
        JSONObject jsonObj = enrichModelerDataForBpmn20();
        String jsonForBpmn20 = jsonObj.toString();
        Definitions bpmnDefinitions = prepareDefinitions(jsonForBpmn20);
        getProcess(bpmnDefinitions);

        return null;
    }

}

