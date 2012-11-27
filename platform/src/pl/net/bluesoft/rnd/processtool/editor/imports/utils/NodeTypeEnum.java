package pl.net.bluesoft.rnd.processtool.editor.imports.utils;

public enum NodeTypeEnum {
	TASK("Task"), START("StartNoneEvent"), END("EndNoneEvent"), XOR("Exclusive_Databased_Gateway");

	private String stencilId;

	private NodeTypeEnum(String stencilId) {
		this.stencilId = stencilId;
	}

	@Override
	public String toString() {
		return stencilId;
	}
}