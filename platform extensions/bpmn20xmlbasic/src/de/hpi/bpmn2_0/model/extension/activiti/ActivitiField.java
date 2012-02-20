package de.hpi.bpmn2_0.model.extension.activiti;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

/**
 * Reflects activiti:field element for Activiti BPMN2.0 extensions
 *
 * @author tlipski@bluesoft.net.pl
 *
 */
@XmlRootElement(name = "field")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivitiField extends AbstractExtensionElement {

    @XmlAttribute
	private String name;

    @XmlElement(name = "string")
    private String stringValue;

    @XmlElement
    private String expression;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
