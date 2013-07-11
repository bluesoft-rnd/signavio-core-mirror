package pl.net.bluesoft.rnd.processtool.editor.platform.ext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import org.apache.batik.transcoder.TranscoderException;
import pl.net.bluesoft.rnd.processtool.editor.AperteWorkflowDefinitionGenerator;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@HandlerConfiguration(uri = "/save", rel = "save")
public class SaveHandler extends DeployHandler {

    public SaveHandler(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
        doPost(req, res, token, sbo);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public <T extends FsSecureBusinessObject> void doPost(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
        res.setStatus(200);

        File tempJar = null;
        try {
            tempJar = File.createTempFile("aw-deploy-", ".tmp");
            AperteWorkflowDefinitionGenerator gen = fillProcessDeploymentBundle(token, tempJar,
                    req.getParameter("name"),
                    req.getParameter("parent-id"));

            FileInputStream fis = new FileInputStream(tempJar);
            try {
                ServletOutputStream os = res.getOutputStream();
                try {
                    int c;
                    res.setContentType("application/java-archive");
                    res.addHeader("Content-Disposition", "attachment; filename=\"" + gen.getProcessFileName() + ".jar" + "\"");
                    while ((c = fis.read())>=0) {
                        os.write(c);
                    }
                } finally {
                    os.close();
                }
            } finally {
                fis.close();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RequestException("UnsupportedEncodingException", e);
        } catch (TranscoderException e) {
            throw new RequestException("Error while creating PNG file", e);
        } catch (IOException e) {
            throw new RequestException("Error while creating JAR file", e);
        } finally {
            if (tempJar != null) tempJar.delete();
        }
    }

}
