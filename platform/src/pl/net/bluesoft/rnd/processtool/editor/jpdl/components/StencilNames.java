package pl.net.bluesoft.rnd.processtool.editor.jpdl.components;

/**
 * Created with IntelliJ IDEA.
 * User: Kamil
 * Date: 10.07.13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public enum StencilNames {
    TASK("Task"),USER("User"),SEQUENCE_FLOW("SequenceFlow"),COLLAPSED_SUBPROCESS("CollapsedSubprocess"),  END_EVENT("EndNoneEvent"), EXCLUSIVE_DATABASED_GATEWAY("Exclusive_Databased_Gateway");
    private String name;

    private StencilNames(String name) {
        this.name = name;
    }

    public boolean equalsStencilName(String stencilName){
              return  name.equals(stencilName);

    }

}
