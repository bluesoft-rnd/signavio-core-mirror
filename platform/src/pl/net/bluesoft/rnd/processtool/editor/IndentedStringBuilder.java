package pl.net.bluesoft.rnd.processtool.editor;

/**
 * User: POlszewski
 * Date: 2013-10-01
 * Time: 13:02
 */
public class IndentedStringBuilder {
	private final StringBuilder sb;
	private int indentationLevel = 0;
	private boolean generateIndentation = false;

	public IndentedStringBuilder() {
		this(new StringBuilder());
	}

	public IndentedStringBuilder(int capacity) {
		this(new StringBuilder(capacity));
	}

	private IndentedStringBuilder(StringBuilder sb) {
		this.sb = sb;
	}

	public IndentedStringBuilder begin() {
		++indentationLevel;
		return this;
	}

	public IndentedStringBuilder end() {
		--indentationLevel;
		return this;
	}

	public IndentedStringBuilder append(char value) {
		sb.append(value);
		return this;
	}

	public IndentedStringBuilder append(int value) {
		sb.append(value);
		return this;
	}

	public IndentedStringBuilder append(float value) {
		sb.append(value);
		return this;
	}

	public IndentedStringBuilder append(Object value) {
		sb.append(value);
		return this;
	}

	public IndentedStringBuilder append(String value) {
		if (generateIndentation) {
			generateIndentation = false;
			for (int i = 0; i < indentationLevel; ++i) {
				sb.append('\t');
			}
		}
		sb.append(value);
		if (value != null && value.endsWith("\n")) {
			generateIndentation = true;
		}
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
