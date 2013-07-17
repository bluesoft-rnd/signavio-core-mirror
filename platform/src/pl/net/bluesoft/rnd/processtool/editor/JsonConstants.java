package pl.net.bluesoft.rnd.processtool.editor;

/**
 * Created with IntelliJ IDEA.
 * User: Kamil
 * Date: 21.06.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public enum JsonConstants {
    PROPERTIES("properties"),APERTE_CONF("aperte-conf"),CALL_ACTIVITY("callacitivity");

    private String name;

    private JsonConstants(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
