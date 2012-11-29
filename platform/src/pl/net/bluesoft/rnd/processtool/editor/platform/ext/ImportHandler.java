package pl.net.bluesoft.rnd.processtool.editor.platform.ext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;

import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.ParserException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedDiagramException;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedTypeOfTaskException;
import pl.net.bluesoft.rnd.processtool.editor.imports.parsers.Xpdl20Parser;
import pl.net.bluesoft.rnd.processtool.editor.imports.utils.DiagramBuilder;
import pl.net.bluesoft.rnd.processtool.editor.imports.utils.ModelerFileInputDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


@HandlerConfiguration(uri = "/import", rel = "save")
public class ImportHandler extends DeployHandler {
	
private static final String ATRIBUTE_NAME_FOR_XML ="data";	
private static final Logger logger = Logger.getLogger(ImportHandler.class);

    public ImportHandler(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
        doPost(req, res, token, sbo);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public <T extends FsSecureBusinessObject> void doPost(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
    	res.setStatus(200);
    	Map attribute = (Map) req.getAttribute("javaParams");
    	ArrayList<String> list = (ArrayList<String>) attribute.get(ATRIBUTE_NAME_FOR_XML);
    	String codedXmlFromClient = list.get(0);

    	try {
    		String decodedFileFromModeler = ModelerFileInputDecoder.decodeFileFromModeler(codedXmlFromClient);
			/*
			 * If request would contained format file it would be possible to chose other parser. 
			 */
    		
    		Xpdl20Parser xpdlParser = new Xpdl20Parser(decodedFileFromModeler);
			 BasicDiagram diagram = DiagramBuilder.buildDiagram(xpdlParser);
			  
			 JSONObject json = diagram.getJSON();
			 res.setContentType("text/html");
			  PrintWriter writer = null;
			  writer = res.getWriter();
			  writer.print(json);
			  writer.close();
			 
	
		} catch (UnsupportedDiagramException e) {
			logger.error("Error while encoding diagram from client.",e);
			throw new RequestException("Error while encoding diagram from client.", e);
		} catch (ParserException e) {
			logger.error("Error while parsing diagram from client.",e);
			throw new RequestException("Error while parsing diagram from client.", e);
		} catch (UnsupportedTypeOfTaskException e) {
			logger.error("Unsuported xml format",e);
			throw new RequestException("Unsuported xml format", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       
		
	
	 
    }

}
