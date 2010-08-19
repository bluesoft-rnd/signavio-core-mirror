package com.signavio.warehouse.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.util.fsbackend.FileSystemUtil;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.transformation.Json2XmlConverter;

public class BPMN20XMLFileUtil {

	public static void storeBPMN20XMLFile(String path, String jsonRep) throws IOException, JSONException, BpmnConverterException, JAXBException, SAXException {
		PlatformProperties props = Platform.getInstance().getPlatformProperties();
		
		Json2XmlConverter converter = new Json2XmlConverter(jsonRep, Platform.getInstance().getFile("/WEB-INF/xsd/BPMN20.xsd").getAbsolutePath());
		
		StringWriter xml = converter.getXml();
		
		FileSystemUtil.deleteFileOrDirectory(path);
		FileSystemUtil.createFile(path, xml.toString());
	}
}
