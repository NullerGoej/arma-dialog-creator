package com.armadialogcreator.application;

import com.armadialogcreator.util.KeyValueString;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 A facade interface for a config XML file

 @author K
 @since 01/06/2019 */
public interface Configurable {
	@NotNull Iterable<Configurable> getNestedConfigurables();

	/** XML safe attributes (attribute_name='attribute_value') */
	@NotNull Iterable<KeyValueString> getConfigurableAttributes();

	void addNestedConfigurable(@NotNull Configurable c);

	void addAttribute(@NotNull String key, @NotNull String value);

	/** An XML safe name (<get_name_result></get_name_result>). If empty string, <b>no data</b> will be saved to file. */
	@NotNull
	@NonNls
	String getConfigurableName();

	/** Misc text that can contain anything */
	@NonNls
	@NotNull
	String getConfigurableBody();

	class Simple implements Configurable {
		@NotNull
		private final String configName;
		@NotNull
		private List<Configurable> nested = new ArrayList<>();
		@NotNull
		private List<KeyValueString> atts = new ArrayList<>();
		@NotNull
		private String body = "";

		public Simple(@NotNull String configName) {
			this.configName = configName;
		}

		public Simple(@NotNull String configName, @NotNull String body) {
			this.configName = configName;
			this.body = body;
		}

		public void setBody(@NotNull String body) {
			this.body = body;
		}

		@Override
		@NotNull
		public Iterable<Configurable> getNestedConfigurables() {
			return nested;
		}

		@Override
		@NotNull
		public Iterable<KeyValueString> getConfigurableAttributes() {
			return atts;
		}

		@Override
		public void addNestedConfigurable(@NotNull Configurable c) {
			nested.add(c);
		}

		@Override
		public void addAttribute(@NotNull String key, @NotNull String value) {
			atts.add(new KeyValueString(key, value));
		}

		@Override
		@NotNull
		public String getConfigurableName() {
			return configName;
		}

		@Override
		@NotNull
		public String getConfigurableBody() {
			return body;
		}
	}

	Configurable EMPTY = new Configurable() {

		@Override
		public @NotNull List<Configurable> getNestedConfigurables() {
			// always return new list so that it is mutable (Collections.emptyList() isn't mutable)
			// and so the previous use case of EMPTY doesn't carry over to a different use case of EMPTY
			return new ArrayList<>();
		}

		@Override
		@NotNull
		public List<KeyValueString> getConfigurableAttributes() {
			//always return new list so that it is mutable (Collections.emptyList() isn't mutable)
			// and so the previous use case of EMPTY doesn't carry over to a different use case of EMPTY
			return new ArrayList<>();
		}

		@Override
		public void addNestedConfigurable(@NotNull Configurable c) {

		}

		@Override
		public void addAttribute(@NotNull String key, @NotNull String value) {

		}

		@Override
		@NotNull
		public String getConfigurableName() {
			return "";
		}

		@Override
		@NotNull
		public String getConfigurableBody() {
			return "";
		}
	};
}
