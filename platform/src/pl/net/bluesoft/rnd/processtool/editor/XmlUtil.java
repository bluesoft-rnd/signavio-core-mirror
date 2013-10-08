package pl.net.bluesoft.rnd.processtool.editor;

public class XmlUtil {
    
    private static final String CDATA_WRAP_BEGIN = "<![CDATA[";

    private static final String CDATA_WRAP_END = "]]>";
    
    private static final StringPair[] XML_SPECIAL_STRINGS = {
        new StringPair("<", "&lt;"),
        new StringPair(">", "&gt;"),
        new StringPair("\"", "&quot;"),
        new StringPair("&", "&amp;"),
        new StringPair("\'", "&apos;")
    };

    /**
     * Does the string contain XML special characters
     *
     * @param input Input string
     * @return True if input contains XML special characters. Otherwise false.
     */
    public static boolean containsXmlEscapeCharacters(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            for (StringPair sp : XML_SPECIAL_STRINGS) {
                if (sp.getPlain().equals(Character.toString(c))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String encodeXmlEcscapeCharacters(String input) {
        for (StringPair sp : XML_SPECIAL_STRINGS) {
            input = input.replaceAll(sp.getPlain(), sp.getEncoded());
        }
        return input;
    }
    
	public static String decodeXmlEscapeCharacters(String input) {
        for (StringPair sp : XML_SPECIAL_STRINGS) {
            input = input.replaceAll(sp.getEncoded(), sp.getPlain());
        }
		return input;
	}

    /**
     * Wrap input with CDATA tag
     *
     * @param input Input string
     * @return Input string wrapped with CDATA
     */
    public static String wrapCDATA(String input) {
        return CDATA_WRAP_BEGIN + input + CDATA_WRAP_END;
    }

    private static class StringPair {
        
        private String plain;
        private String encoded;

        private StringPair(String plain, String encoded) {
            this.plain = plain;
            this.encoded = encoded;
        }

        public String getPlain() {
            return plain;
        }

        public String getEncoded() {
            return encoded;
        }
    }

	public static boolean hasText(String str) {
		return str != null && !str.isEmpty();
	}
}
