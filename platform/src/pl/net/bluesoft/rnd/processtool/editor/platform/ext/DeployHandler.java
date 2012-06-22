package pl.net.bluesoft.rnd.processtool.editor.platform.ext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.ModelTypeFileExtension;
import com.signavio.warehouse.model.business.ModelTypeManager;
import com.signavio.warehouse.model.business.modeltype.SignavioModelType;
import com.signavio.warehouse.revision.business.RepresentationType;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

@HandlerConfiguration(uri = "/deploy", rel="deploy")
public class DeployHandler extends BasisHandler {

	public DeployHandler(ServletContext servletContext) {
		super(servletContext);
	}

	private Manifest getManifest(String bundleName, String bundleDescription, String processToolDeployment) {
		Manifest mf = new Manifest();
		mf.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mf.getMainAttributes().put(new Attributes.Name("Created-By"), "Aperte Modeler");
		mf.getMainAttributes().put(new Attributes.Name("Built-By"), "Aperte Modeler");
		mf.getMainAttributes().put(new Attributes.Name("Bundle-ManifestVersion"), "2");
		mf.getMainAttributes().put(new Attributes.Name("Bundle-SymbolicName"), processToolDeployment);
		mf.getMainAttributes().put(new Attributes.Name("Bundle-Version"), "1");
		mf.getMainAttributes().put(new Attributes.Name("Bundle-Name"), bundleName);
		mf.getMainAttributes().put(new Attributes.Name("Bundle-Description"), bundleDescription);
		mf.getMainAttributes().put(new Attributes.Name("Import-Package"), "org.osgi.framework");
		mf.getMainAttributes().put(new Attributes.Name("ProcessTool-Process-Deployment"), processToolDeployment);
		return mf;
	}
	
    private List<Element> getChildrenByName(Element parent, String name) {
        ArrayList<Element> res = new ArrayList<Element>();
        NodeList g = parent.getChildNodes();
        for (int i=0; i < g.getLength(); i++) {
            Node item = g.item(i);
            if (item instanceof Element) {
                Element e = (Element) item;
                if (name.equals(e.getTagName())) {
                    res.add(e);
                }
            }
        }
        return res;
    }
	private byte[] svg2png(byte[] svg) throws TranscoderException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            DOMSource source = new DOMSource(document);
//            ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
//            Result result = new StreamResult(tmpBaos);
//            transformer.transform(source, result);

        ByteArrayInputStream bais = new ByteArrayInputStream(svg);
        TranscoderInput input = new TranscoderInput(bais);
        TranscoderOutput output = new TranscoderOutput(baos);
        PNGTranscoder pngTranscoder = new PNGTranscoder();
        pngTranscoder.transcode(input, output);
        return baos.toByteArray();      
	}

    private String[] getGraphOffset(byte[] svg) {
        String[] vs;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(svg));

            for (Element e : getChildrenByName(document.getDocumentElement(), "g")) {
                for (Element ee : getChildrenByName(e, "g")) {

                    String transform = ee.getAttribute("transform");
                    if (transform != null) {//GOTCHA!
                        //e.g. translate(-194.30433654785156, -36)
                        transform = transform.replace("translate(", "").replace(")", "").replace(" ", "");
                        return transform.split(",", 2);

                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String[]{"0", "0"};
    }

    protected void addPackageDirs(String packageName, JarOutputStream target) throws IOException {
		String[] packageElems = packageName.split("\\.");
		String[] dirNames = new String[packageElems.length];
		for (int i = 0; i < packageElems.length; i++) {
		   dirNames[i] = "";
		   for (int j = 0; j <= i; j++) {
			   dirNames[i] = dirNames[i] + packageElems[j] + "/";
		   }
		}
		for (String d : dirNames) {
			JarEntry entry = new JarEntry(d);
			entry.setTime(new Date().getTime());
			target.putNextEntry(entry);
			target.closeEntry();
		}
	}
	
	protected void addEntry(String entryName, JarOutputStream target, InputStream in) throws IOException {
		BufferedInputStream bin = null;
		try {  
            JarEntry entry = new JarEntry(entryName);
            entry.setTime(new Date().getTime());
            target.putNextEntry(entry);
            bin = new BufferedInputStream(in);
            byte[] buffer = new byte[1024];
            while (true) {
            int count = in.read(buffer);
            if (count == -1)
                break;
            target.write(buffer, 0, count);
            }
            target.closeEntry();
            bin.close();
		} finally {
			if (bin != null) {
                bin.close();
            }
		}
	}
	
	@Override
	@HandlerMethodActivation
	public Object postRepresentation(Object params, FsAccessToken token) {
		JSONObject jsonParams = (JSONObject) params;
        File tempJar = null;
		try {
            tempJar = File.createTempFile("aw-deploy-", ".tmp");
            AperteWorkflowDefinitionGenerator gen = fillProcessDeploymentBundle(token, jsonParams, tempJar);
            // copy to osgi-plugins
            PlatformProperties props =  Platform.getInstance().getPlatformProperties();
            String osgiPluginsDir = props.getAperteOsgiPluginsDir();
            copy(tempJar, new File(osgiPluginsDir + File.separator + gen.getProcessFileName() + ".jar"));

		} catch (JSONException e) {
			throw new RequestException("JSONException", e);
		} catch (UnsupportedEncodingException e) {
			throw new RequestException("UnsupportedEncodingException", e);
		} catch (TranscoderException e) {
			throw new RequestException("Error while creating PNG file", e);
		} catch (IOException e) {
			throw new RequestException("Error while creating JAR file", e);
		} finally {
            if (tempJar != null) tempJar.delete();
        }

		return "OK";
	}

    protected AperteWorkflowDefinitionGenerator fillProcessDeploymentBundle(FsAccessToken token, JSONObject jsonParams, File tempJar) throws JSONException, TranscoderException, IOException {
        String name = jsonParams.getString("name");
        String parentId = jsonParams.getString("parent");
        parentId = parentId.replace("/directory/", "");
        return fillProcessDeploymentBundle(token, tempJar, name, parentId);
    }

    protected AperteWorkflowDefinitionGenerator fillProcessDeploymentBundle(FsAccessToken token, File tempJar,
                                                        String name, String parentId) throws TranscoderException, IOException {
        FsDirectory parent = FsSecurityManager.getInstance().loadObject(FsDirectory.class, parentId, token);
        String signavioXMLExtension = SignavioModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension();
        String fileName = name + signavioXMLExtension;
        String fileNameWithPath = parent.getPath() + File.separator + fileName;
        
        byte [] jsonData = ModelTypeManager.getInstance().getModelType(signavioXMLExtension).getRepresentationInfoFromModelFile(RepresentationType.JSON, fileNameWithPath);
        byte [] svgData = ModelTypeManager.getInstance().getModelType(signavioXMLExtension)
                .getRepresentationInfoFromModelFile(RepresentationType.SVG, fileNameWithPath);
        String jsonRep = new String(jsonData, "utf-8");

        String[] vs = getGraphOffset(svgData);
        int offsetX = Math.round(Float.parseFloat(vs[0]));
        int offsetY = Math.round(Float.parseFloat(vs[1]));
        AperteWorkflowDefinitionGenerator gen = new AperteWorkflowDefinitionGenerator(offsetX, offsetY);
        gen.init(jsonRep);
        
        // MANIFEST
        Manifest mf = getManifest(gen.getBundleName(), gen.getBundleDesc(), gen.getProcessToolDeployment());

        // convert SVG to PNG format
        byte[] png = svg2png(svgData);

        // create new temporary JAR
//        File tempJar = File.createTempFile("aw-deploy-", ".tmp");
        tempJar.deleteOnExit();
        JarOutputStream target = new JarOutputStream(new FileOutputStream(tempJar), mf);
        addPackageDirs(gen.getProcessToolDeployment(), target);

        String processDir = gen.getProcessToolDeployment().replace('.','/') + '/';

        String language = gen.getProcessDefinitionLanguage();
        // adding PNG and XML files
        addEntry(processDir + "processdefinition.png", target, new ByteArrayInputStream(png));
        addEntry(processDir + "processdefinition." + language + ".xml", target, new ByteArrayInputStream(gen.generateDefinition().getBytes("UTF-8")));
        addEntry(processDir + "processtool-config.xml", target, new ByteArrayInputStream(gen.generateProcessToolConfig().getBytes("UTF-8")));
        addEntry(processDir + "queues-config.xml", target, new ByteArrayInputStream(gen.generateQueuesConfig().getBytes("UTF-8")));

        // messages are not
        Map<String, String> messages = gen.getMessages();
        if (messages != null && !messages.isEmpty()) {
            String propertiesFiles = "";
            for (String langCode : messages.keySet()) {
                String propertiesFilename = "messages_" + langCode + ".properties";

                String messagesContent = messages.get(langCode);
                if (messagesContent != null) {
                    addEntry(processDir + propertiesFilename, target, new ByteArrayInputStream(messagesContent.getBytes("US-ASCII")));
                    propertiesFiles += propertiesFilename + ",";
                }
            }

            if (propertiesFiles != null && !propertiesFiles.isEmpty()) {
                if (propertiesFiles.endsWith(",")) {
                    propertiesFiles = propertiesFiles.substring(0, propertiesFiles.length() - 1);
                }
                mf.getMainAttributes().put(new Attributes.Name("ProcessTool-I18N-Property"), propertiesFiles);
            }
        }
        
        
     // dictionary is not
        String dictionary = gen.getDictionary();
        if (dictionary != null && !dictionary.isEmpty()) {
            String propertiesFiles = "";
                String propertiesFilename = "dictionary.xml";
                    addEntry(processDir + propertiesFilename, target, new ByteArrayInputStream(dictionary.getBytes()));
                mf.getMainAttributes().put(new Attributes.Name("ProcessTool-I18N-Dictionary"), propertiesFilename);           
        }
        
        // logo may not exist so make sure to check
        if (gen.getProcessIcon() != null) {
            addEntry(processDir + "processdefinition-logo.png", target, new ByteArrayInputStream(gen.getProcessIcon()));
        }
        
        // close the JAR
        target.close();

        return gen;
    }


    protected void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}

}
