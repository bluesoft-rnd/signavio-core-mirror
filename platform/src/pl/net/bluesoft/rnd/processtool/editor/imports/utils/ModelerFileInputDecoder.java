package pl.net.bluesoft.rnd.processtool.editor.imports.utils;

import org.apache.commons.codec.binary.Base64;

import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedDiagramException;

public class ModelerFileInputDecoder {

	
	public static String decodeFileFromModeler(String base64String) throws UnsupportedDiagramException {
		String decodedString = null;
		if(base64String!=null){
		String[] splitedInput = base64String.split(",");
		if (testIfBase64AndNotNull(splitedInput)) {

			byte[] decodedBase64 = Base64.decodeBase64(splitedInput[1]);
			decodedString = new String(decodedBase64);
		}
		else{
			throw new UnsupportedDiagramException();
		}
		}
		return decodedString;

	}

	private static boolean testIfBase64AndNotNull(String[] splitedInput) {
		if (splitedInput.length>1 && !splitedInput[0].isEmpty() && splitedInput[0].contains("base64")
				 && !splitedInput[1].isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
