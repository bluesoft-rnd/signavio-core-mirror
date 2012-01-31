package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

public class JPDLStartEvent extends JPDLComponent {
  
	
	protected JPDLStartEvent() {
	  
    }
	
	
	public String toXML() { 
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("<start name=\"%s\" g=\"%d,%d,%d,%d\">\n", name,boundsX, boundsY, width, height));
		//sb.append(String.format("<description>Original ID: '%s'</description>\n", resourceId));
		sb.append(getTransitionsXML());
		sb.append("</start>\n");
		return sb.toString();
    }
	
	@Override
	public String getObjectName() {
		return "Start Event";
	}
}
