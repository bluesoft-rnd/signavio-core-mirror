package de.hpi.bpmn2_0.factory.jbpm5.utils;

/**
 * Created with IntelliJ IDEA.
 * User: kkolodziej@bluesoft.net.pl
 * Date: 03.07.13
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public enum IoSpecyficationNames {
          TASK_NAME("TaskName"),PRIORITY("Priority"), COMMENT("Comment"), SKIPPABLE("Skippable"), CONTENT("Content"), GROUP_ID("GroupId");
    private String name;

    private IoSpecyficationNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
