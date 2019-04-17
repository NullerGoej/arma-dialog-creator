package com.armadialogcreator.data;

import com.armadialogcreator.application.Configurable;
import com.armadialogcreator.canvas.CanvasComponent;
import com.armadialogcreator.canvas.NamedUINode;
import com.armadialogcreator.canvas.UINode;
import com.armadialogcreator.util.DoubleIterable;
import com.armadialogcreator.util.KeyValueString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 @author K
 @since 4/8/19 */
public class UINodeConfigurable implements Configurable {

	@NotNull
	public static List<Configurable> fromConfigurable(@NotNull Configurable c, @NotNull UINode node, boolean loadPositionInfo) {
		List<Configurable> childConfs = new ArrayList<>(c.getNestedConfigurableCount());
		Configurable component = c.getConfigurable("component");
		CanvasComponent nodeComponent = node.getComponent();
		if (component == null) {
			if (nodeComponent != null) {
				throw new IllegalArgumentException();
			}
		} else {
			if (nodeComponent == null) {
				throw new IllegalArgumentException();
			}
			String enabledAtt = component.getAttributeValueNotNull("enabled");
			nodeComponent.setEnabled(enabledAtt.equals("true"));
			String ghostAtt = component.getAttributeValueNotNull("ghost");
			nodeComponent.setGhost(ghostAtt.equals("true"));
			if (loadPositionInfo) {
				String x1Att = component.getAttributeValueNotNull("x1");
				String y1Att = component.getAttributeValueNotNull("y1");
				String x2Att = component.getAttributeValueNotNull("x2");
				String y2Att = component.getAttributeValueNotNull("y2");
				nodeComponent.setX1(Integer.parseInt(x1Att));
				nodeComponent.setX2(Integer.parseInt(x2Att));
				nodeComponent.setY1(Integer.parseInt(y1Att));
				nodeComponent.setY2(Integer.parseInt(y2Att));
			}
		}
		return childConfs;
	}

	@Nullable
	public static String getUINodeNameFromConfigurable(@NotNull Configurable c) {
		return c.getAttributeValue("UINodeName");
	}

	private final UINode node;
	private boolean savePositionInfo;
	private final List<KeyValueString> atts = new ArrayList<>();
	private final List<Configurable> additionalConfs = new ArrayList<>();

	public UINodeConfigurable(@NotNull UINode node, boolean savePositionInfo) {
		this.node = node;
		this.savePositionInfo = savePositionInfo;
		Configurable.Simple componentConf = new Simple("component");
		additionalConfs.add(componentConf);

		CanvasComponent component = node.getComponent();
		if (component != null) {
			// componentConf.addAttribute("class", component.getClass().getName());
			componentConf.addAttribute("enabled", component.isEnabled());
			componentConf.addAttribute("ghost", component.isGhost());
			if (savePositionInfo) {
				componentConf.addAttribute("x1", component.getX1() + "");
				componentConf.addAttribute("y1", component.getY1() + "");
				componentConf.addAttribute("x2", component.getX2() + "");
				componentConf.addAttribute("y2", component.getY2() + "");
			}
		}

		if (node instanceof NamedUINode) {
			NamedUINode namedUINode = (NamedUINode) node;
			atts.add(new KeyValueString("UINodeName", namedUINode.getUINodeName().getValue()));
		}
	}

	@Override
	@NotNull
	public Iterable<Configurable> getNestedConfigurables() {
		List<Configurable> list = new ArrayList<>(node.getChildCount() + additionalConfs.size());
		for (UINode node : node.iterateChildNodes()) {
			list.add(new UINodeConfigurable(node, savePositionInfo));
		}
		return new DoubleIterable<>(list, additionalConfs);
	}

	@Override
	public int getNestedConfigurableCount() {
		return node.getChildCount() + additionalConfs.size();
	}

	@Override
	@NotNull
	public Iterable<KeyValueString> getConfigurableAttributes() {
		return atts;
	}

	@Override
	public int getConfigurableAttributeCount() {
		return atts.size();
	}

	@Override
	public void addNestedConfigurable(@NotNull Configurable c) {
		additionalConfs.add(c);
	}

	@Override
	public void addAttribute(@NotNull String key, @NotNull String value) {
		atts.add(new KeyValueString(key, value));
	}

	@Override
	@NotNull
	public String getConfigurableName() {
		return "UINode";
	}

	@Override
	@NotNull
	public String getConfigurableBody() {
		return "";
	}
}
