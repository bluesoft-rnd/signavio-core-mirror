package pl.net.bluesoft.rnd.processtool.editor;

import org.aperteworkflow.editor.domain.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Widget {
  
	private List<Widget> childrenList = new ArrayList<Widget>();
	private String widgetId;
    private Integer priority;
	private Map<String,Object> attributesMap = new HashMap<String,Object>();
	private List<Permission> permissions = new ArrayList<Permission>();
	
	public void addChildWidget(Widget w) {
		childrenList.add(w);
	}

	public List<Widget> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<Widget> childrenList) {
		this.childrenList = childrenList;
	}

	public String getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public Map<String, Object> getAttributesMap() {
		return attributesMap;
	}

	public void setAttributesMap(Map<String, Object> attributesMap) {
		this.attributesMap = attributesMap;
	}
	
	public void putAttribute(String key, Object value) {
		attributesMap.put(key, value);
	}
	
	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public void addPermission(Permission p) {
		permissions.add(p);
	}

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
