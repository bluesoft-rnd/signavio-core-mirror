package de.hpi.bpmn2_0.factory.jbpm5.utils;

/**
 * Created with IntelliJ IDEA.
 * User: kkolodziej@bluesoft.net.pl
 * Date: 03.07.13
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public enum AllowedActionsVariables {
          ACTION("ACTION"),RESULT("RESULT");
    private String name;

    private AllowedActionsVariables(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
