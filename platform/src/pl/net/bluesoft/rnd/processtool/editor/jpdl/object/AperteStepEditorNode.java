package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import com.signavio.platform.exceptions.RequestException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.aperteworkflow.editor.domain.Permission;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.IndentedStringBuilder;
import pl.net.bluesoft.rnd.processtool.editor.Widget;
import pl.net.bluesoft.rnd.processtool.editor.XmlUtil;
import pl.net.bluesoft.rnd.processtool.editor.jpdl.components.StencilNames;

import java.util.*;

import static pl.net.bluesoft.rnd.processtool.editor.XmlUtil.hasText;

public  class AperteStepEditorNode extends AperteTask {

	protected Widget widget;
	protected String commentary;
	protected String description;
	private String stepInfoPattern;

	protected String assignee;
	protected String swimlane;
	protected String candidateGroups;
	protected List<Permission> permissions;

	protected AperteStepEditorNode(AperteWorkflowDefinitionGenerator generator) {
		super(generator);
	}

	@Override
	public void fillBasicProperties(JSONObject json) throws JSONException {
		super.fillBasicProperties(json);

		// Properties from Step Editor attributes
		String widgetJson = json.getJSONObject("properties").getString(
				"aperte-conf");
		if (widgetJson != null && !widgetJson.trim().isEmpty()) {
			widget = new Widget();
			widgetJson = XmlUtil.decodeXmlEscapeCharacters(widgetJson);
			JSONObject widgetJsonObj = new JSONObject(widgetJson);
			assignee = widgetJsonObj.optString("assignee");
			swimlane = widgetJsonObj.optString("swimlane");
			candidateGroups = widgetJsonObj.optString("candidate_groups");

			commentary = widgetJsonObj.optString("commentary");
			if (commentary != null) {
				byte[] bytes = Base64.decodeBase64(commentary.getBytes());
				commentary = new String(bytes);
			}

			description = widgetJsonObj.optString("description");

			stepInfoPattern = widgetJsonObj.optString("stepInfo");

			permissions = generatePermissionsFromJSON(widgetJsonObj
					.optJSONArray("step-permissions"));

			checkAssignee();
			JSONArray children = widgetJsonObj.optJSONArray("children");
			JSONObject properties = widgetJsonObj.optJSONObject("properties");
			JSONArray permissions = widgetJsonObj.optJSONArray("permissions");
			widget.setWidgetId(widgetJsonObj.getString("widgetId"));
			widget.setPriority(widgetJsonObj.getInt("priority"));
			createWidgetTree(widget, children, properties, permissions);
		}
	}

	private void checkAssignee() {
		boolean b1 = StringUtils.isEmpty(assignee);
		boolean b2 = StringUtils.isEmpty(swimlane);
		boolean b3 = StringUtils.isEmpty(candidateGroups);
		if (b1 && b2 && b3) {
			throw new RequestException(
					"Fill in assignee, swimlane or candidateGroups for UserTask '" + name + "'");
		}
		if ((b1 && b2 && !b3) || (b1 && b3) || (!b1 && b2 && b3)) {
		} else {
			throw new RequestException(
					"Only one of fields: assignee, swimlane, candidateGroups can be filled for UserTask '"
							+ name + "'");
		}
	}

	private void createWidgetTree(Widget w, JSONArray children,
			JSONObject properties, JSONArray permissions) throws JSONException {
		if (properties != null) {
			Iterator it = properties.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = properties.get(key);
				w.putAttribute(key, value);
			}
		}
		List<Permission> permissionsList = generatePermissionsFromJSON(permissions);
		w.setPermissions(permissionsList);
		if (children != null) {
			for (int i = 0; i < children.length(); i++) {
				JSONObject obj = children.getJSONObject(i);
				Widget n = new Widget();
				n.setWidgetId(obj.getString("widgetId"));
				n.setPriority(obj.getInt("priority"));
				w.addChildWidget(n);
				createWidgetTree(n, obj.optJSONArray("children"),
						obj.optJSONObject("properties"),
						obj.optJSONArray("permissions"));
			}
		}
	}

	private List<Permission> generatePermissionsFromJSON(JSONArray permissions)
			throws JSONException {
		List<Permission> permissionsList = new ArrayList<Permission>();
		if (permissions != null) {
			for (int i = 0; i < permissions.length(); i++) {
				JSONObject obj = permissions.getJSONObject(i);
				Permission p = new Permission();
				p.setRoleName(obj.getString("roleName"));
				p.setPrivilegeName(obj.getString("privilegeName"));
				permissionsList.add(p);
			}
		}
		return permissionsList;
	}

	public Widget getWidget() {
		return widget;
	}

	public void generateWidgetXML(IndentedStringBuilder sb) {
		sb.append("<config.ProcessStateConfiguration name=\"").append(name).append('"');

		if (hasText(description)) {
			sb.append(" description=\"").append(description).append('"');
		}

		if (hasText(stepInfoPattern)) {
			sb.append(" stepInfoPattern=\"");
			sb.append(XmlUtil.encodeXmlEcscapeCharacters(stepInfoPattern));
			sb.append('"');
		}

		sb.append(">\n");

		sb.begin();

		if (hasText(commentary)) {
			sb.append(String.format("<commentary>%s</commentary>\n", XmlUtil.wrapCDATA(commentary)));
		}

		sb.append("<widgets>\n");
		sb.begin();
		generateWidgetPermissionsXML(sb, widget.getPermissions());
		generateAttributesXML(sb, widget.getAttributesMap());
		generateChildrenXML(sb, widget.getChildrenList(), false);
		sb.end();
		sb.append("</widgets>\n");

		if (!outgoing.isEmpty()) {
			sb.append("<actions>\n");
			sb.begin();
			if (outgoing.values().size() != 1) {
				throw new RuntimeException("User task: " + name + " has more than one outgoing transition.");
			}
			AperteTransition next = outgoing.values().iterator().next();
			AperteComponent component = generator.findComponent(next.getTarget());
			if (StencilNames.EXCLUSIVE_DATABASED_GATEWAY.equalsStencilName(component.getStencilId())) {
				generateActions(sb, component.getOutgoing().values());
			} else {// normal button,
				generateActions(sb, outgoing.values());
			}
			sb.end();
			sb.append("</actions>\n");
		}
		generateStatePermissionsXML(sb, permissions);
		sb.end();
		sb.append("</config.ProcessStateConfiguration>\n");
	}

	private void generateActions(IndentedStringBuilder sb, Collection<AperteTransition> transitions) {
		for (AperteTransition trans : getTransitionsOrderedByName(transitions)) {
			trans.generateStateActionXML(sb);
		}
	}

	private List<AperteTransition> getTransitionsOrderedByName(Collection<AperteTransition> transitions) {
		List<AperteTransition> result = new ArrayList<AperteTransition>(transitions);
		Collections.sort(result, new Comparator<AperteTransition>() {
			@Override
			public int compare(AperteTransition t1, AperteTransition t2) {
				return t1.getName().compareTo(t2.getName());
			}
		});
		return result;
	}

	private void generateWidgetPermissionsXML(IndentedStringBuilder sb, List<Permission> permissions) {
		String permissionClass = "ProcessStateWidgetPermission";
		generatePermissionsXml(sb, permissions, permissionClass);
	}

	private void generateStatePermissionsXML(IndentedStringBuilder sb, List<Permission> permissions) {
		String permissionClass = "ProcessStatePermission";
		generatePermissionsXml(sb, permissions, permissionClass);
	}

	private void generatePermissionsXml(IndentedStringBuilder sb, List<Permission> permissions, String permissionClass) {
		if (permissions.isEmpty()) {
			return;
		}

		sb.append("<permissions>\n");
		sb.begin();
		for (Permission p : permissions) {
			sb.append(String.format("<config." + permissionClass + " privilegeName=\"%s\" roleName=\"%s\"/>\n",
					p.getPrivilegeName(), p.getRoleName()));
		}
		sb.end();
		sb.append("</permissions>\n");
	}

	private void generateAttributesXML(IndentedStringBuilder sb, Map<String, Object> attributesMap) {
		if (attributesMap.isEmpty()) {
			return;
		}

		sb.append("<attributes>\n");
		sb.begin();
		for (String key : attributesMap.keySet()) {
			Object value = attributesMap.get(key);
			String strValue = new String(Base64.decodeBase64(((String) value).getBytes()));

			if (XmlUtil.containsXmlEscapeCharacters(strValue)) {
				sb.append(String.format("<config.ProcessStateWidgetAttribute name=\"%s\">\n", key));
				sb.begin();
				sb.append(String.format("<value>%s</value>\n", XmlUtil.wrapCDATA(strValue)));
				sb.end();
				sb.append("</config.ProcessStateWidgetAttribute>\n");
			}
			else {
				sb.append(String.format("<config.ProcessStateWidgetAttribute name=\"%s\" value=\"%s\"/>\n",
						key, strValue));
			}
		}
		sb.end();
		sb.append("</attributes>\n");
	}

	private void generateChildrenXML(IndentedStringBuilder sb, List<Widget> list, boolean withChildrenTag) {
        if (list.isEmpty()) {
            return;
        }

        if (withChildrenTag) {
            sb.append("<children>\n");
			sb.begin();
        }
        for (Widget w : list) {
            sb.append(String.format("<config.ProcessStateWidget className=\"%s\" priority=\"%d\">\n",
					w.getWidgetId(), w.getPriority()));
			sb.begin();
            generateWidgetPermissionsXML(sb, w.getPermissions());
            generateAttributesXML(sb, w.getAttributesMap());
            generateChildrenXML(sb, w.getChildrenList(), true);
			sb.end();
            sb.append("</config.ProcessStateWidget>\n");
        }
        if (withChildrenTag) {
			sb.end();
            sb.append("</children>\n");
        }
    }

	public String getSwimlane() {
		return swimlane;
	}
}
