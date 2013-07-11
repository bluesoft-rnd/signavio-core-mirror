package com.signavio.warehouse.directory.handler;

import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;
import org.json.JSONArray;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractParentDirectoriesHandler extends AbstractHandler {

	public AbstractParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Get an ordered list of all (indirect) parent directories 
	 */
	@Override 
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		JSONArray result = new JSONArray();
		
		List<FsDirectory> parents = null;
		if(sbo instanceof FsModel) {
			parents = ((FsModel)sbo).getParentDirectories();
		} else if(sbo instanceof FsDirectory) {
			parents = ((FsDirectory)sbo).getParentDirectories();
		}
		
		if(parents != null) {
			DirectoryHandler dirHandler = new DirectoryHandler(this.getServletContext());
			for(FsDirectory parent : parents) {
				result.put(dirHandler.getDirectoryInfo(parent));
			}
		}
		
		return result;
	}
}
