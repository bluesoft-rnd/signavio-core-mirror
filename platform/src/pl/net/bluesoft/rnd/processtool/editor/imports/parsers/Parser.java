package pl.net.bluesoft.rnd.processtool.editor.imports.parsers;

import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.w3c.dom.Element;

import pl.net.bluesoft.rnd.processtool.editor.imports.exception.ParserException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedTypeOfTaskException;
import pl.net.bluesoft.rnd.processtool.editor.imports.utils.NodeTypeEnum;

public interface Parser {
/**
 * Returns the bounds of Activity element.
 * WARNING! Actual implementation supports only TASK, START, END, XOR GATEWAY. 
 * @param eElement
 * @return
 * @throws ParserException
 * @throws UnsupportedTypeOfTaskException
 */
	Bounds getActivityBound(Element eElement) throws ParserException,
			UnsupportedTypeOfTaskException;
/**
 * Returns node type. 
 * WARNING! Actual implementation supports only TASK, START, END, XOR GATEWAY. 
 * @param node
 * @return
 * @throws UnsupportedTypeOfTaskException
 * @throws ParserException
 */
	NodeTypeEnum getNodeType(Element node)
			throws UnsupportedTypeOfTaskException, ParserException;

	String getElementName(Element node);

	String getElementId(Element node);

	/**
	 * Get all activities(Tasks) from xml
	 * @return
	 * @throws ParserException
	 */
	List<Element> getActivitiesList() throws ParserException;

	/**
	 * Get all transitions from xml
	 * @return
	 * @throws ParserException
	 */
	List<Element> getTransitionList() throws ParserException;
	
	
	/**
	 * Return 2 element table containing FROM and TO attributes. In Transition element.
	 * 
	 * 
	 * @param element 
	 * @return String[2] - First Element is FROM attribute Second is TO attribute.
	 * @throws ParserException - When is Element is not a Transition.
	 */
	String[] getFromAndToTransition(Element element) throws ParserException;

	List<Point> getTransitionDockers(Element node) throws ParserException;
	String getEdgeType();


}
