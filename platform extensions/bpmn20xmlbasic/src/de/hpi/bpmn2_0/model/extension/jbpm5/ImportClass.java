package de.hpi.bpmn2_0.model.extension.jbpm5;

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: Kamil
 * Date: 27.06.13
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement(name = "import")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportClass extends AbstractExtensionElement {

    @XmlAttribute
    private  String name ="test";

    public ImportClass() {
        super();
    }

    public ImportClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





}
