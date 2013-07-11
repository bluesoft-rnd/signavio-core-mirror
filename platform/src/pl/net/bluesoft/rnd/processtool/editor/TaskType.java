package pl.net.bluesoft.rnd.processtool.editor;

/**
 * Created with IntelliJ IDEA.
 * User: Kamil
 * Date: 20.06.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public enum TaskType {
    USER("User"),SEQUENCE_FLOW("SequenceFlow"),DATABASED_GATEWAY("Exclusive_Databased_Gateway"),COLAPSED_SUBPROCESS("CollapsedSubprocess");

    private String name;
    private TaskType(String name) {
        this.name =name;
    }

    public String getName() {
        return name;
    }
}
