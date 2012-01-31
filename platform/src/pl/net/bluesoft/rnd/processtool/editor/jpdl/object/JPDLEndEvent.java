package pl.net.bluesoft.rnd.processtool.editor.jpdl.object;

public class JPDLEndEvent extends JPDLComponent {
  
	
	protected JPDLEndEvent() {
	  
    }
	
	
	public String toXML() { 
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("<end name=\"%s\" g=\"%d,%d,%d,%d\">\n", name,x1,y1,x2-x1,y2-y1));
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
