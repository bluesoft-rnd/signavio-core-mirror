package pl.net.bluesoft.rnd.processtool.editor;

import com.signavio.platform.exceptions.RequestException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aperteworkflow.editor.domain.Permission;
import org.aperteworkflow.editor.domain.ProcessConfig;
import org.aperteworkflow.editor.domain.Queue;
import org.aperteworkflow.editor.domain.QueueRolePermission;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.exception.UnsupportedJPDLObjectException;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.JPDLComponent;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.JPDLObject;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.JPDLTransition;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.object.JPDLUserTask;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.queue.QueueDef;

import java.io.IOException;
import java.util.*;

public class JPDLGenerator {
  
	//key = resourceId
	private Map<String, JPDLComponent> componentMap = new HashMap<String, JPDLComponent>(); 
	private Map<String, JPDLTransition> transitionMap = new HashMap<String, JPDLTransition>();
	private SortedMap<Integer, QueueDef> queueConf;
    
    private ProcessConfig processConfig;
    
	private final Logger logger = Logger.getLogger(JPDLGenerator.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private String processName;
	private String processFileName;
    private String bundleDesc;
    private String bundleName;
    private String processToolDeployment;
    private int offsetY;
    private int offsetX;

    public JPDLGenerator(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


    public void init(String json) {
		try {
		  JSONObject jsonObj = new JSONObject(json);
		  
		  processName = jsonObj.getJSONObject("properties").getString("name");
		  processFileName = jsonObj.getJSONObject("properties").optString("aperte-process-filename");
          bundleDesc = jsonObj.getJSONObject("properties").optString("mf-bundle-description");
          bundleName = jsonObj.getJSONObject("properties").optString("mf-bundle-name");
          processToolDeployment = jsonObj.getJSONObject("properties").optString("mf-processtool-deployment");

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
          
		  String queueConfJson = jsonObj.getJSONObject("properties").optString("queue-conf");
		  if (queueConfJson != null && queueConfJson.trim().length() > 0) {
		    queueConf = mapper.readValue(queueConfJson, new TypeReference<SortedMap<Integer,QueueDef>>(){});
		  } else {
			queueConf = new TreeMap<Integer, QueueDef>();
		  }

            String processConfJson = jsonObj.getJSONObject("properties").optString("process-conf");
            if (processConfJson != null && !processConfJson.trim().isEmpty()) {
                processConfig = mapper.readValue(processConfJson, ProcessConfig.class);
            }

            JSONArray childShapes = jsonObj.getJSONArray("childShapes");
            for (int i = 0; i < childShapes.length(); i++) {
                JSONObject obj = childShapes.getJSONObject(i);
                JPDLObject jpdlObject = JPDLObject.getJPDLObject(obj);
                jpdlObject.fillBasicProperties(obj);
                if (jpdlObject instanceof JPDLComponent) {
                    ((JPDLComponent) jpdlObject).applyOffset(offsetX, offsetY);
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
		} catch (JsonMappingException e) {
			logger.error("Error while parsing queues.", e);
			throw new RequestException(e.getMessage());
		} catch (JsonParseException e) {
			logger.error("Error while parsing queues.", e);
			throw new RequestException(e.getMessage());
		} catch (IOException e) {
			logger.error("Error while parsing queues.", e);
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
	
	public String generateJpdl() {
		StringBuffer jpdl = new StringBuffer();
		jpdl.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		jpdl.append(String.format("<process name=\"%s\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n", processName));

		Set<String> swimlanes = new HashSet<String>();
		
		for (String key : componentMap.keySet()) {
			JPDLComponent comp = componentMap.get(key);
			if (comp instanceof JPDLUserTask) {
				JPDLUserTask userTask = (JPDLUserTask)comp;
				if (userTask.getSwimlane() != null && userTask.getSwimlane().trim().length() > 0) {
					swimlanes.add(userTask.getSwimlane());
				}
			}
		}
		
		Iterator<String> i = swimlanes.iterator();
		while (i.hasNext()) {
			String sl = i.next();
			jpdl.append(String.format("<swimlane candidate-groups=\"%s\" name=\"%s\"/>\n", sl, sl));
		}
		  
		
		for (String key : componentMap.keySet()) {
			jpdl.append(componentMap.get(key).toXML());
		}
		
		jpdl.append("</process>");
		
		return jpdl.toString();
	}
	
	public String generateProcessToolConfig() {
        StringBuffer ptc = new StringBuffer();
        ptc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        ptc.append(String.format("<config.ProcessDefinitionConfig bpmDefinitionKey=\"%s\" description=\"%s\" processName=\"%s\" comment=\"%s\">\n", processName, processName, processName, processName));

        if (processConfig != null) {
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
				JPDLUserTask task = (JPDLUserTask)cmp;
				if (task.getWidget() != null) {
					ptc.append(task.generateWidgetXML());
				}
			}
		}
		
		ptc.append("</states>\n");
		ptc.append("</config.ProcessDefinitionConfig>\n");
		return ptc.toString();
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

//		for (Integer i : queueConf.keySet()) {
//			QueueDef qd = queueConf.get(i);
//			q.append(String.format("<config.ProcessQueueConfig name=\"%s\" description=\"%s\">\n", qd.getName(), qd.getDescription()));
//			SortedMap<Integer,QueueRight> rights = qd.getRights();
//			q.append("<rights>\n");
//			for (Integer j : rights.keySet()) {
//			  QueueRight qr = rights.get(j);
//			  q.append(String.format("<config.ProcessQueueRight roleName=\"%s\" browseAllowed=\"%b\"/>\n", qr.getRoleName(), qr.isBrowseAllowed()));
//			}
//			q.append("</rights>\n");
//			q.append("</config.ProcessQueueConfig>\n");
//		}
		
		q.append("</list>\n");
		return q.toString();
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
	
}
