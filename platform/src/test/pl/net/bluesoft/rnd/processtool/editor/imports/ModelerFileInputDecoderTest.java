package test.pl.net.bluesoft.rnd.processtool.editor.imports;

import org.junit.Test;
import pl.net.bluesoft.rnd.processtool.editor.imports.exception.UnsupportedDiagramException;
import pl.net.bluesoft.rnd.processtool.editor.imports.utils.ModelerFileInputDecoder;

import static org.junit.Assert.assertTrue;

/**
 * The class <code>ModelerFileInputDecoderTest</code> contains tests for the
 * class {@link <code>ModelerFileInputDecoder</code>}
 * 
 * @author kkolodziej@bluesoft.net.pl
 * 
 * @version $Revision$
 */
public class ModelerFileInputDecoderTest {

	@Test
	public void test_DecodingWithCorrectString() throws UnsupportedDiagramException {
		String textToDecode = "data:base64,PD94bWwgdmVyc";

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);

		assertTrue(decodeFileFromModeler != null);

	}

	@Test(expected = UnsupportedDiagramException.class)
	public void test_DecodingWithoutCodingInformation() throws UnsupportedDiagramException {
		String textToDecode = "PD94bWwgdmVyc";

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);

	}
	
	@Test(expected = UnsupportedDiagramException.class)
	public void test_DecodingWithoutCodedText() throws UnsupportedDiagramException {
		String textToDecode = "data:base64,";

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);

	

	}
	
	@Test(expected = UnsupportedDiagramException.class)
	public void test_DecodingWithWrongString() throws UnsupportedDiagramException {
		String textToDecode = "data:base32,PD94bWwgdmVyc";

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);


	}
	
	@Test
	public void test_DecodingWithNoString() throws UnsupportedDiagramException {
		String textToDecode = null;

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);

		assertTrue(decodeFileFromModeler == null);

	}
	
	@Test(expected = UnsupportedDiagramException.class)
	public void test_DecodingWithEmptyString() throws UnsupportedDiagramException {
		String textToDecode = "";

		String decodeFileFromModeler = ModelerFileInputDecoder
				.decodeFileFromModeler(textToDecode);


	}
	
	

}
