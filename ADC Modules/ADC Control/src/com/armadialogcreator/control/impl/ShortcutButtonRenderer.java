package com.armadialogcreator.control.impl;

import com.armadialogcreator.canvas.CanvasContext;
import com.armadialogcreator.control.ArmaControl;
import com.armadialogcreator.control.ArmaControlRenderer;
import com.armadialogcreator.control.ArmaResolution;
import com.armadialogcreator.control.impl.utility.*;
import com.armadialogcreator.core.ConfigClass;
import com.armadialogcreator.core.ConfigProperty;
import com.armadialogcreator.core.ConfigPropertyLookup;
import com.armadialogcreator.core.ConfigPropertyLookupConstant;
import com.armadialogcreator.core.sv.*;
import com.armadialogcreator.expression.Env;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 @author Kayler
 @since 11/21/2016 */
public class ShortcutButtonRenderer extends ArmaControlRenderer implements BasicTextRenderer.UpdateCallback {
	private BasicTextRenderer textRenderer;
	private BlinkControlHandler blinkControlHandler;
	private TooltipRenderer tooltipRenderer;
	private Function<GraphicsContext, Void> tooltipRenderFunc = gc -> {
		tooltipRenderer.paint(gc, this.mouseOverX, this.mouseOverY);
		return null;
	};

	private final ImageOrTextureHelper animTextureNormal = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper animTextureDisabled = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper animTextureOver = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper animTexturePressed = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper animTextureFocused = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper animTextureDefault = new ImageOrTextureHelper(this);
	private final ImageOrTextureHelper textureNoShortcut = new ImageOrTextureHelper(this);

	/** secondary text color (text color alternates between "color" and "color2") */
	private Color color2 = Color.BLACK;
	private Color colorFocused = Color.BLACK;
	private Color colorDisabled = Color.BLACK;
	private Color colorBackgroundFocused = Color.BLACK;
	private Color colorBackground2 = Color.BLACK;
	private long periodFocusMillis = 500;
	private long periodOverMillis = 500;

	private double hitZone_top = 0;
	private double hitZone_right = 0;
	private double hitZone_bottom = 0;
	private double hitZone_left = 0;

	private double shortcutPos_top = 0;
	private double shortcutPos_left = 0;
	private double shortcutPos_w = 0;
	private double shortcutPos_h = 0;

	private double textPos_top = 0;
	private double textPos_right = 0;
	private double textPos_bottom = 0;
	private double textPos_left = 0;

	/**
	 Alternating color helper. if control has focus (but mouse isn't over control).
	 There is only one because both the alternating text color and alternating bg colors are synced
	 */
	private final AlternatorHelper focusedColorAlternator = new AlternatorHelper(500);

	public ShortcutButtonRenderer(ArmaControl control, ArmaResolution resolution, Env env) {
		super(control, resolution, env);
		textRenderer = new BasicTextRenderer(control, this, ConfigPropertyLookup.TEXT,
				ConfigPropertyLookup.COLOR, ConfigPropertyLookup.STYLE, ConfigPropertyLookup.SIZE,
				ConfigPropertyLookup.SHADOW, true, this
		);

		{
			ConfigProperty colorBackground = myControl.findProperty(ConfigPropertyLookup.COLOR_BACKGROUND);
			addValueListener(colorBackground.getName(), (observer, oldValue, newValue) -> {
				if (newValue instanceof SVColor) {
					getBackgroundColorObserver().updateValue((SVColor) newValue);
				}
			});
			colorBackground.setValue(new SVColorArray(getBackgroundColor()));
		}

		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_NORMAL, animTextureNormal);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_DISABLED, animTextureDisabled);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_OVER, animTextureOver);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_PRESSED, animTexturePressed);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_FOCUSED, animTextureFocused);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.ANIM_TEXTURE_DEFAULT, animTextureDefault);
		attachPicOrTexPropertyListener(ConfigPropertyLookup.TEXTURE_NO_SHORTCUT, textureNoShortcut);

		addValueListener(ConfigPropertyLookup.DEFAULT, (observer, oldValue, newValue) -> {
					requestFocus = newValue instanceof SVBoolean && ((SVBoolean) newValue).isTrue();
					requestRender();
				}
		);

		addValueListener(ConfigPropertyLookup.COLOR2, (observer, oldValue, newValue) -> {
			if (newValue instanceof SVColor) {
				color2 = ((SVColor) newValue).toJavaFXColor();
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.COLOR_FOCUSED, (observer, oldValue, newValue) -> {
			if (newValue instanceof SVColor) {
				colorFocused = ((SVColor) newValue).toJavaFXColor();
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.COLOR_DISABLED, (observer,
															   oldValue, newValue) -> {
			if (newValue instanceof SVColor) {
				colorDisabled = ((SVColor) newValue).toJavaFXColor();
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.COLOR_BACKGROUND_FOCUSED, (observer,
																		 oldValue, newValue) -> {
			if (newValue instanceof SVColor) {
				colorBackgroundFocused = ((SVColor) newValue).toJavaFXColor();
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.COLOR_BACKGROUND2, (observer,
																  oldValue, newValue) -> {
			if (newValue instanceof SVColor) {
				colorBackground2 = ((SVColor) newValue).toJavaFXColor();
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.PERIOD_FOCUS, (observer, oldValue, newValue) -> {
			if (newValue instanceof SVNumericValue) {
				periodFocusMillis = Math.round(((SVNumericValue) newValue).toDouble() * 1000);
				requestRender();
			}
		});
		addValueListener(ConfigPropertyLookup.PERIOD_OVER, (observer, oldValue, newValue) -> {
			if (newValue instanceof SVNumericValue) {
				periodOverMillis = Math.round(((SVNumericValue) newValue).toDouble() * 1000);
				requestRender();
			}
		});

		blinkControlHandler = new BlinkControlHandler(this, ConfigPropertyLookup.BLINKING_PERIOD);

		myControl.findProperty(ConfigPropertyLookup.COLOR).setValue(new SVColorArray(getTextColor()));
		myControl.findProperty(ConfigPropertyLookup.TEXT).setValue(SVString.newEmptyString());

		tooltipRenderer = new TooltipRenderer(
				this.myControl, this,
				ConfigPropertyLookup.TOOLTIP_COLOR_SHADE,
				ConfigPropertyLookup.TOOLTIP_COLOR_TEXT,
				ConfigPropertyLookup.TOOLTIP_COLOR_BOX,
				ConfigPropertyLookup.TOOLTIP
		);

		//nested classes
		ConfigClass hitZone = myControl.findNestedClass(ShortcutButtonControl.NestedClassName_HitZone);
		{
			addValueListener(hitZone, ConfigPropertyLookup.TOP, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					hitZone_top = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(hitZone, ConfigPropertyLookup.RIGHT, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					hitZone_right = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(hitZone, ConfigPropertyLookup.BOTTOM, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					hitZone_bottom = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(hitZone, ConfigPropertyLookup.LEFT, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					hitZone_left = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
		}

		ConfigClass textPos = myControl.findNestedClass(ShortcutButtonControl.NestedClassName_TextPos);
		{
			addValueListener(textPos, ConfigPropertyLookup.TOP, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					textPos_top = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(textPos, ConfigPropertyLookup.RIGHT, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					textPos_right = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(textPos, ConfigPropertyLookup.BOTTOM, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					textPos_bottom = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(textPos, ConfigPropertyLookup.LEFT, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					textPos_left = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
		}

		ConfigClass shortcutPos = myControl.findNestedClass(ShortcutButtonControl.NestedClassName_ShortcutPos);
		{
			addValueListener(shortcutPos, ConfigPropertyLookup.TOP, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					shortcutPos_top = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(shortcutPos, ConfigPropertyLookup.LEFT, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					shortcutPos_left = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(shortcutPos, ConfigPropertyLookup.W, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					shortcutPos_w = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
			addValueListener(shortcutPos, ConfigPropertyLookup.H, (observer, oldValue, newValue) -> {
				if (newValue instanceof SVNumericValue) {
					shortcutPos_h = ((SVNumericValue) newValue).toDouble();
					requestRender();
				}
			});
		}

		requestRender();
	}

	private void attachPicOrTexPropertyListener(ConfigPropertyLookupConstant lookup, ImageOrTextureHelper helper) {
		addValueListener(lookup, (observer, oldValue, newValue) -> {
			helper.updateAsync(newValue);
		});
	}

	@Override
	public void paint(@NotNull GraphicsContext gc, CanvasContext canvasContext) {
		boolean preview = paintPreview(canvasContext);

		final int controlWidth = getWidth();
		final int controlHeight = getHeight();
		ImageOrTextureHelper bgTexture = animTextureNormal;

		int textPosX = x1 + (int) (Math.round(controlWidth * textPos_left));
		int textPosY = y1 + (int) (Math.round(controlHeight * textPos_top));

		if (preview) {
			if (isEnabled()) {
				blinkControlHandler.paint(gc);
			}

			double ratio = focusedColorAlternator.updateAndGetRatio();
			Color colorBackground = this.backgroundColor;
			Color color = textRenderer.getTextColor();

			if (!isEnabled()) {
				//button is disabled
				bgTexture = animTextureDisabled;
				textRenderer.setTextColor(colorDisabled);
			} else if (mouseButtonDown == MouseButton.PRIMARY) {
				//button is being clicked
				bgTexture = animTexturePressed;
				//background color remains as "colorBackground" property value
				//text color remains as "color" property value
			} else if (mouseOver) {
				//mouse is over the button
				bgTexture = animTextureOver;
				//interpolate "color" with "colorFocused"
				if (periodOverMillis > 0) {
					textRenderer.setTextColor(colorFocused.interpolate(color2, ratio));
					setBackgroundColor(colorBackgroundFocused.interpolate(colorBackground2, ratio));
				}
				//interpolate "colorBackgroundFocused" with "colorBackground2"
				focusedColorAlternator.setAlternateMillis(periodOverMillis);
			} else if (focused) {
				bgTexture = animTextureFocused;
				if (periodFocusMillis > 0) {
					textRenderer.setTextColor(color2.interpolate(colorFocused, ratio));
					setBackgroundColor(colorBackground2.interpolate(colorBackgroundFocused, ratio));
				}
				focusedColorAlternator.setAlternateMillis(periodFocusMillis);
			}

			//paint the background texture/image
			switch (bgTexture.getMode()) {
				case Image: {
					// In arma 3, they do some weird as shit for manipulating the background texture.
					// Currently (July 2017), I don't know how they are doing it. So, I'll just stretch the image to
					// width of the control.
					Image image = bgTexture.getImage();
					if (image == null) {
						throw new IllegalStateException();
					}
					gc.drawImage(image, x1, y1, controlWidth, controlHeight);

					gc.setGlobalBlendMode(BlendMode.MULTIPLY);
					super.paint(gc, canvasContext);
					gc.setGlobalBlendMode(BlendMode.SRC_OVER);
					break;
				}
				case ImageError: {
					paintImageError(gc, x1, y1, controlWidth, controlHeight);
					break;
				}
				case LoadingImage: {
					super.paint(gc, canvasContext);
					break;
				}
				case Texture: {
					TexturePainter.paint(gc, bgTexture.getTexture(), backgroundColor, x1, y1, x2, y2);
					break;
				}
				case TextureError: {
					paintTextureError(gc, x1, y1, controlWidth, controlHeight);
					break;
				}
			}

			paintShortcutThing(gc);

			textRenderer.paint(gc, textPosX, textPosY);

			//reset the colors again
			setBackgroundColor(colorBackground);
			textRenderer.setTextColor(color);

			if (this.mouseOver) {
				canvasContext.paintLast(tooltipRenderFunc);
			}
		} else {
			super.paint(gc, canvasContext);
			paintShortcutThing(gc);
			textRenderer.paint(gc, textPosX, textPosY);
		}

	}

	private void paintShortcutThing(GraphicsContext gc) {
		final int controlWidth = getWidth();
		final int controlHeight = getHeight();

		int x1 = (int) (this.x1 + Math.round(controlWidth * shortcutPos_left));
		int y1 = (int) (this.y1 + Math.round(controlHeight * shortcutPos_top));
		int x2 = (int) (this.x1 + Math.round(controlWidth * shortcutPos_w));
		int y2 = (int) (this.y1 + Math.round(controlHeight * shortcutPos_h));
		if (textureNoShortcut.getValue() != null && textureNoShortcut.getValue().toString().length() == 0) {
			//ignore the texture if defined but no text is entered
			return;
		}
		switch (textureNoShortcut.getMode()) {
			case Image: {
				gc.drawImage(textureNoShortcut.getImage(), x1, y1, x2 - x1, y2 - y1);
				break;
			}
			case ImageError: {
				paintImageError(gc, x1, y1, 30, 30);
				break;
			}
			case LoadingImage: {
				break;
			}
			case Texture: {
				TexturePainter.paint(gc, textureNoShortcut.getTexture(), x1, y1, x2, y2);
				break;
			}
			case TextureError: {
				paintTextureError(gc, x1, y1, x2 - x1, y2 - y1);
				break;
			}
		}
	}

	@NotNull
	public Color getTextColor() {
		return textRenderer.getTextColor();
	}

	@Override
	public boolean canHaveFocus() {
		return true;
	}

	@Override
	public boolean containsPoint(int x, int y) {
		int controlWidth = getWidth();
		int controlHeight = getHeight();

		//how many pixels from left side that the mouse can't click on
		int leftCut = (int) (Math.round(controlWidth * this.hitZone_left));
		//how many pixels from right side that the mouse can't click on
		int rightCut = (int) (Math.round(controlWidth * this.hitZone_right));
		int topCut = (int) (Math.round(controlHeight * this.hitZone_top));
		int bottomCut = (int) (Math.round(controlHeight * this.hitZone_bottom));

		if (x1 + leftCut <= x && x2 - rightCut >= x) {
			if (y1 + topCut <= y && y2 - bottomCut >= y) {
				return true;
			}
		}
		return false;
	}
}
