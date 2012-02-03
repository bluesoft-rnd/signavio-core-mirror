package pl.net.bluesoft.rnd.processtool.editor.platform.ext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@HandlerConfiguration(uri = "/aperte_post", rel = "aperte")
public class ApertePostHandler extends BasisHandler {

    private static final Logger logger = Logger.getLogger(ApertePostHandler.class);

    public ApertePostHandler(ServletContext servletContext) {
        super(servletContext);
    }

    public <T extends FsSecureBusinessObject> void doPost(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
        res.setStatus(200);
        res.setContentType("text/html");

        JSONObject jParams = (JSONObject) req.getAttribute("params");
        
        String stepEditor = jParams.optString("step_editor");
        String actionEditor = jParams.optString("action_editor");
        String processEditor = jParams.optString("process_editor");

        PrintWriter out = null;
        try {
            out = res.getWriter();
            if (stepEditor != null && !stepEditor.trim().isEmpty()) {
                out.println("<html><head></head><body><script type=\"text/javascript\">window.parent.editorSetStepData(\"" + stepEditor + "\"); " + "</script></body></html>");
            } else if (actionEditor != null && !actionEditor.trim().isEmpty()) {
            	out.println("<html><head></head><body><script type=\"text/javascript\">window.parent.editorSetActionData(\"" + actionEditor + "\"); " + "</script></body></html>");
            } else if (processEditor != null && !processEditor.trim().isEmpty()) {
                out.println("<html><head></head><body><script type=\"text/javascript\">window.parent.editorSetProcessData(\"" + processEditor + "\"); " + "</script></body></html>");
            }

            out.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}