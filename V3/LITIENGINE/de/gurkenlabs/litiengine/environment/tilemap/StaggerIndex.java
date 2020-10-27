package de.gurkenlabs.litiengine.environment.tilemap;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum StaggerIndex {
	@XmlEnumValue("odd")
	ODD, @XmlEnumValue("even")
	EVEN;

	public String value() {
		return this.name().toLowerCase();
	}
}
