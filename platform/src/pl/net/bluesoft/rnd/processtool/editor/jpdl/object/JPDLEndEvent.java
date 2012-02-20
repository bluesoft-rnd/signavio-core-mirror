package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

public class JPDLEndEvent extends JPDLComponent {


    public JPDLEndEvent(AperteWorkflowDefinitionGenerator generator) {
        super(generator);
    }

    public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("<end name=\"%s\" g=\"%d,%d,%d,%d\">\n", name,boundsX, boundsY, width, height));
		//sb.append(String.format("<description>Original ID: '%s'</description>\n", resourceId));
		sb.append(getTransitionsXML());
		sb.append("</end>\n");
		return sb.toString();
    }
	
	@Override
	public String getObjectName() {
		return "End Event";
	}
}
