package de.hpi.bpmn2_0.model.extension.activiti;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

/**
 * Reflects activiti:out element for Activiti BPMN2.0 extensions
 *
 *
 */
@XmlRootElement(name = "out")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivitiOut extends AbstractExtensionElement {

    @XmlAttribute
	private String source;

    @XmlAttribute
	private String target;

    public ActivitiOut(){

    }

	public ActivitiOut(String source, String target) {
		super();
		this.source = source;
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
