package pl.net.bluesoft.rnd.processtool.editor.imports.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.w3c.dom.Element;

import pl.net.bluesoft.rnd.processtool.editor.imports.exception.ParserException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedTypeOfTaskException;
import pl.net.bluesoft.rnd.processtool.editor.imports.parsers.Parser;

//TODO This is very basic import, more work is required.
public class DiagramBuilder {

	public static BasicDiagram buildDiagram(Parser parser)
			throws ParserException, UnsupportedTypeOfTaskException {
		BasicDiagram diagram = new BasicDiagram("Test");
		Map<String, BasicNode> basicNodesMap = new HashMap<String, BasicNode>();
		List<Element> activitiesList = parser.getActivitiesList();
		for (Element element : activitiesList) {
			BasicNode basicNode = buildNode(element, parser);
			basicNodesMap.put(parser.getElementId(element), basicNode);
			diagram.addChildShape(basicNode);
		}

		List<Element> transitionList = parser.getTransitionList();
		for (Element element : transitionList) {
			BasicEdge basicEdge = buildEdge(element, parser, basicNodesMap);
			diagram.addChildShape(basicEdge);
		}
		return diagram;
	}

	private static BasicNode buildNode(Element element, Parser parser)
			throws ParserException, UnsupportedTypeOfTaskException {

		Map<String, String> propertiesMap = new HashMap<String, String>();
		NodeTypeEnum nodeType = parser.getNodeType(element);
		BasicNode basicNode = new BasicNode(parser.getElementId(element));

		String activityName = parser.getElementName(element);
		propertiesMap.put("name", activityName);

		basicNode.setStencilId(nodeType.toString());
		basicNode.setBounds(parser.getActivityBound(element));
		basicNode.setProperties(propertiesMap);
		return basicNode;
	}

	private static BasicEdge buildEdge(Element element, Parser parser,
			Map<String, BasicNode> basicNodesMap) throws ParserException {
		Map<String, String> propertiesMap = new HashMap<String, String>();
		BasicEdge basicEdge = new BasicEdge(parser.getElementId(element));
		basicEdge.setStencilId(parser.getEdgeType());
		String transitionName = parser.getElementName(element);
		propertiesMap.put("name", transitionName);
		basicEdge.setProperties(propertiesMap);

		String[] fromAndToTransition = parser.getFromAndToTransition(element);
		createConnections(fromAndToTransition, basicEdge, basicNodesMap);

		return basicEdge;
	}

	private static void createConnections(String[] fromAndToTransition,
			BasicEdge basicEdge, Map<String, BasicNode> basicNodesMap) {
		String from = fromAndToTransition[0];
		String to = fromAndToTransition[1];
		BasicNode fromNode = basicNodesMap.get(from);
		BasicNode toNode = basicNodesMap.get(to);
		Bounds fromBounds = toNode.getBounds();
		Bounds toBounds = fromNode.getBounds();
		Point middle = fromBounds.getMiddle();

		basicEdge.addDocker(middle);
		fromNode.addOutgoingAndUpdateItsIncomings(basicEdge);
		toBounds = toNode.getBounds();
		middle = toBounds.getMiddle();
		basicEdge.addDocker(middle);
		toNode.addIncomingAndUpdateItsOutgoings(basicEdge);

	}

}
