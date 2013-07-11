package pl.net.bluesoft.rnd.processtool.editor.imports.parsers;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.ParserException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedTypeOfTaskException;
import pl.net.bluesoft.rnd.processtool.editor.imports.utils.NodeTypeEnum;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Xpdl20Parser implements Parser {
	private static Document doc;

	public Xpdl20Parser(String xmlDiagram) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			ByteArrayInputStream decodedStringInputStream = new ByteArrayInputStream(
					xmlDiagram.getBytes());
			doc = dBuilder.parse(decodedStringInputStream);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Bounds getActivityBound(Element eElement) throws ParserException,
			UnsupportedTypeOfTaskException {
		Point activityCoordinates = getActivityCoordinates(eElement);
		Point activityWidthAndHeight = getActivityWidthAndHeight(eElement);
		activityWidthAndHeight.add(activityCoordinates);
		return new Bounds(activityCoordinates, activityWidthAndHeight);
	}

	private Point getActivityCoordinates(Element node) throws ParserException {
		Element coordinatesElement = getElementByTagFromNode(node,
				"Coordinates");
		String xCoordinate = coordinatesElement
				.getAttribute(Xpdl20ParserConstants.X_COORDINATES);
		String yCoordinate = coordinatesElement
				.getAttribute(Xpdl20ParserConstants.Y_COORDINATES);
		int florx = (int) Double.parseDouble(xCoordinate);
		int flory = (int) Double.parseDouble(yCoordinate);
		return new Point(florx, flory);

	}

	private static Element getElementByTagFromNode(Element eElement,
			String nodeName) throws ParserException {
		Element element = null;
		List<Element> listOfElementsByTagFromNode = getListOfElementsByTagFromNode(
				eElement, nodeName);
		if (listOfElementsByTagFromNode.size() > 0) {
			element = listOfElementsByTagFromNode.get(0);
		} else {
			throw new ParserException();
		}
		return element;
	}

	private static List<Element> getListOfElementsByTagFromNode(
			Element eElement, String nodeName) throws ParserException {
		List<Element> listoOfElements = new ArrayList<Element>();
		NodeList elementsByTagName = eElement.getElementsByTagName(nodeName);
		for (int i = 0; i < elementsByTagName.getLength(); i++) {
			Node nNode = elementsByTagName.item(i);
			listoOfElements.add(castNodeToElement(nNode));
		}
		return listoOfElements;
	}

	private Point getActivityWidthAndHeight(Element node)
			throws ParserException, UnsupportedTypeOfTaskException {
		NodeTypeEnum nodeType = null;
		int florw = 0;
		int florh = 0;

		nodeType = getNodeType(node);

		if (nodeType.equals(NodeTypeEnum.TASK)) {
			Element coordinatesElement = getElementByTagFromNode(node,
					"NodeGraphicsInfo");
			String width = coordinatesElement
					.getAttribute(Xpdl20ParserConstants.WIDTH);
			String height = coordinatesElement
					.getAttribute(Xpdl20ParserConstants.HEIGHT);
			florw = countFlorFromStringDouble(width);
			florh = countFlorFromStringDouble(height);
		}
		if (nodeType.equals(NodeTypeEnum.START)) {
			florw = Xpdl20ParserConstants.START_SIZE;
			florh = Xpdl20ParserConstants.START_SIZE;
		} else if (nodeType.equals(NodeTypeEnum.END)) {
			florw = Xpdl20ParserConstants.END_SIZE;
			florh = Xpdl20ParserConstants.END_SIZE;
		} else if (nodeType.equals(NodeTypeEnum.XOR)) {
			florw = Xpdl20ParserConstants.GATEWAY_SIZE;
			florh = Xpdl20ParserConstants.GATEWAY_SIZE;
		} else {
			throw new UnsupportedTypeOfTaskException();
		}
		return new Point(florw, florh);
	}

	private int countFlorFromStringDouble(String number) {
		return (int) Double.parseDouble(number);
	}

	private static Element castNodeToElement(Node nNode) throws ParserException {
		if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) nNode;
		} else {
			throw new ParserException();
		}
	}

	@Override
	public NodeTypeEnum getNodeType(Element node)
			throws UnsupportedTypeOfTaskException, ParserException {
		if (node.getElementsByTagName(Xpdl20ParserConstants.TASK).item(0) != null) {

			return NodeTypeEnum.TASK;
		}
		if (node.getElementsByTagName(Xpdl20ParserConstants.START).item(0) != null) {

			return NodeTypeEnum.START;
		}
		if (node.getElementsByTagName(Xpdl20ParserConstants.END).item(0) != null) {

			return NodeTypeEnum.END;
		}
		if (node.getElementsByTagName(Xpdl20ParserConstants.XOR).item(0) != null) {
			if (getElementByTagFromNode(node, Xpdl20ParserConstants.XOR)
					.getAttribute("GatewayType").equals("XOR")) {
				return NodeTypeEnum.XOR;
			}
		}
		throw new UnsupportedTypeOfTaskException();
	};

	@Override
	public String getElementName(Element node) {
		return node.getAttribute(Xpdl20ParserConstants.ELEMENT_NAME);
	}

	@Override
	public String getElementId(Element node) {
		return node.getAttribute(Xpdl20ParserConstants.ELEMENT_ID);
	}

	@Override
	public List<Element> getActivitiesList() throws ParserException {
		return getXmlElementsList(Xpdl20ParserConstants.ACTIVITY);
	}

	@Override
	public List<Element> getTransitionList() throws ParserException {
		return getXmlElementsList(Xpdl20ParserConstants.TRANSITION);
	}

	private List<Element> getXmlElementsList(String elementName)
			throws ParserException {
		ArrayList<Element> nodeList = new ArrayList<Element>();
		NodeList nList = doc.getElementsByTagName(elementName);
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			Element eElement = (Element) castNodeToElement(nNode);
			nodeList.add(eElement);
		}
		return nodeList;
	}

	@Override
	public String[] getFromAndToTransition(Element element)
			throws ParserException {
		if (element.getTagName().equals(Xpdl20ParserConstants.TRANSITION)) {
			String[] fromTo = new String[2];
			fromTo[0] = element
					.getAttribute(Xpdl20ParserConstants.TRANSITION_FROM);
			fromTo[1] = element.getAttribute(Xpdl20ParserConstants.TRANSITION_TO);
			return fromTo;
		}
		throw new ParserException();
	}

	/**
	 * Unused but potentially can be useful Returns list of all element dockers
	 * 
	 * @param node
	 *            - use only edge
	 * @return - list od all dockers
	 * @throws ParserException
	 */
	@Override
	public List<Point> getTransitionDockers(Element node)
			throws ParserException {
		ArrayList<Point> dockerCoordinates = new ArrayList<Point>();

		List<Element> listOfElementsByTagFromNode = getListOfElementsByTagFromNode(
				node, "Coordinates");
		for (Element coordinatesElement : listOfElementsByTagFromNode) {
			String xCoordinate = coordinatesElement
					.getAttribute(Xpdl20ParserConstants.X_COORDINATES);
			String yCoordinate = coordinatesElement
					.getAttribute(Xpdl20ParserConstants.Y_COORDINATES);
			int florx = (int) Double.parseDouble(xCoordinate);
			int flory = (int) Double.parseDouble(yCoordinate);

			dockerCoordinates.add(new Point(florx, flory));
		}
		return dockerCoordinates;
	}
	
	/**
	 * In Xpdl there is only one edge type.
	 */
	@Override
	public String getEdgeType(){
		
		return Xpdl20ParserConstants.SEQUENCEFLOW;
	}

}
