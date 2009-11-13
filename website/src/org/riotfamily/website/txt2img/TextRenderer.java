/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.Locale;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.ColorUtils;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * Class to render text as image. Supports resampling to improve the kerning
 * at small font sizes.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class TextRenderer implements ApplicationContextAware, InitializingBean {
	
	private RiotLog log = RiotLog.get(TextRenderer.class);
	
	private float fontSize = 22;
	
	private int paddingTop = 0;
	
	private int paddingRight = 0;
	
	private int paddingBottom = 0;
	
	private int paddingLeft = 0;
	
	private int lineSpacing = 0;
	
	private Integer maxWidth;
	
	private boolean shrinkToFit;
	
	private Color color = Color.BLACK;
	
	private boolean antiAlias = true;
	
	private boolean resample = true;
	
	private int internalFontSize = 120;
	
	private int scale = 1;

	private String fontName;
	
	private FontManager fontManager;

	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void setFontManager(FontManager fontManager) {
		this.fontManager = fontManager;
	}
	
	/**
	 * Sets the font to use. The resource must either point to a Type 1
	 * or a TrueType font or a directory containing fonts. In case a directory
	 * is specified, the font that contains the most glyphs needed to render a
	 * given text is used. 
	 * The font type is determined by the filename extension. 
	 * Supported extensions are <i>.pfa</i>, <i>.pfb</i> and <i>.ttf</i>.
	 */
	public void setFont(String fontName) {
		this.fontName = fontName;
	}
	
	/**
	 * Sets the text foreground color. Examples of supported formats:
	 * <pre>
	 * #fff
	 * #fffff
	 * rgb(255,255,255)
	 * rgb(100%, 100%, 100%)
	 * </pre>
	 */
	public void setColor(String color) {
		this.color = ColorUtils.parseColor(color);
	}
		
	/**
	 * Sets the font size.
	 */
	public void setSize(float size) {
		this.fontSize = size;
	}

	/**
	 * Turns anti-aliasing off or on (default).
	 */
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}
	
	/**
	 * Sets the interline spacing in pixels. If not set, 
	 * {@link TextLayout#getLeading()} is used.
	 */
	public void setLineSpacing(int lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	/**
	 * Sets the maximum image width.
	 */
	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	/**
	 * Sets whether the font-size should be reduced if the text does not
	 * fit within the max-width.
	 */
	public void setShrinkToFit(boolean shrinkToFit) {
		this.shrinkToFit = shrinkToFit;
	}
	
	/**
	 * Sets the padding at the top of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}
		
	/**
	 * Sets the padding at the right side of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}
		
	/**
	 * Sets the padding at the bottom of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}
	
	/**
	 * Sets the padding at the left side of the image in pixels. 
	 * The default value is <code>0</code>.
	 */
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	
	/**
	 * If set to true, the text will be rendered at a larger size internally
	 * and scaled down to the requested size.
	 */
	public void setResample(boolean resample) {
		this.resample = resample;
	}
	
	/**
	 * Sets the font size at which the text is rendered internally when 
	 * resampling is enabled.
	 */
	public void setInternalFontSize(int internalFontSize) {
		this.internalFontSize = internalFontSize;
	}
	
	public void afterPropertiesSet() {
		if (fontManager == null) {
			fontManager = SpringUtils.beanOfTypeIncludingAncestors(
					applicationContext, FontManager.class);
		}
		if (resample) {
			scale = Math.round(internalFontSize / fontSize);
			fontSize *= scale;
			paddingTop *= scale;
			paddingRight *= scale;
			paddingBottom *= scale;
			paddingLeft *= scale;
			lineSpacing *= scale;
			if (maxWidth != null) {
				maxWidth = new Integer(maxWidth.intValue() * scale 
						- paddingLeft - paddingRight);
			}
		}
	}

	public final BufferedImage generate(String text, int maxWidth, String color) {
		return generate(text, null, maxWidth, color);
	}

	public BufferedImage generate(String text, Locale locale, int maxWidth, String color) {
		if (!StringUtils.hasText(text)) {
			text = " ";
		}
		if (this.maxWidth != null) {
			maxWidth = this.maxWidth.intValue();
		}
		else if (resample && maxWidth < Integer.MAX_VALUE) {
			maxWidth = maxWidth * scale - paddingLeft - paddingRight;
		}
		
		float fontSize = this.fontSize;
		Dimension size;
		
		if (shrinkToFit && maxWidth < Integer.MAX_VALUE) {
			size = getSize(text, locale, fontSize, Integer.MAX_VALUE);
			while (size.getWidth() > maxWidth) {
				double delta = fontSize - fontSize * (maxWidth / size.getWidth());
				fontSize -= Math.max(Math.round(delta), 1);
				size = getSize(text, locale, fontSize, Integer.MAX_VALUE);
			}
			maxWidth = Integer.MAX_VALUE;
		}
		else {
			size = getSize(text, locale, fontSize, maxWidth);
		}
		
		BufferedImage image = createImage(size);
		drawText(text, locale, maxWidth, color, fontSize, image);
		
		if (resample) {
			int w = checkSize((int) (size.getWidth() / scale));
			int h = checkSize((int) (size.getHeight() / scale));
			Image scaledImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			g.drawImage(scaledImage, 0, 0, w, h, null);
			g.dispose();
		}
		return image;
	}
	
	protected Dimension getSize(String text, Locale locale, float fontSize, float maxWidth) {
	    return layout(text, locale, maxWidth, null, fontSize, 
	    		createImage(new Dimension(1, 1)), false);
	}
	
	protected void drawText(String text, Locale locale, float maxWidth, String color, 
			float fontSize, BufferedImage image) {
		
	    layout(text, locale, maxWidth, color, fontSize, image, true);
	}
	
	protected Dimension layout(String text, Locale locale, float maxWidth,
			String color, float fontSize, BufferedImage image, boolean draw) {
		
		Color fg = this.color;
		if (color != null) {
			try {
				fg = ColorUtils.parseColor(color);
			}
			catch (IllegalArgumentException e) {
				log.warn("Browser didn't send a valid color value, using default.");
			}
		}
				
		Font font = fontManager.getFont(fontName, locale).deriveFont(fontSize);
		
		Graphics2D graphics = createGraphics(image);
		FontRenderContext fc = graphics.getFontRenderContext();

        HyphenatedLineBreakMeasurerer measurer = new HyphenatedLineBreakMeasurerer(text, font, fg, fc);
	    int y = paddingTop;
	    int maxX = 1;
	    while (measurer.hasNext()) {
	    	TextLayout layout = measurer.nextLayout(maxWidth);
			y += layout.getAscent();
			int x = paddingLeft + (int) layout.getVisibleAdvance();
			if (draw) {
				layout.draw(graphics, paddingLeft, y);
			}
			y += layout.getDescent();
			maxX = Math.max(maxX, x + paddingRight);
			y += layout.getLeading();
			if (measurer.hasNext()) {
				y += lineSpacing;
			}
	    }
	    y += paddingBottom;
	    graphics.dispose();
	    return new Dimension(checkSize(maxX), checkSize(y));
	}
	
	protected int checkSize(int size) {
		if (size <= 0) {
			return 1;
		}
		return size;
	}
	
	protected Graphics2D createGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias 
        		? RenderingHints.VALUE_ANTIALIAS_ON
        		: RenderingHints.VALUE_ANTIALIAS_OFF);
        
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias 
        		? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        		: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, antiAlias 
        		? RenderingHints.VALUE_FRACTIONALMETRICS_ON
        		: RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        
        return graphics;
	}
	
	protected BufferedImage createImage(Dimension size) {
		return new BufferedImage(
				(int) size.getWidth(),
				(int) size.getHeight(), 
				BufferedImage.TYPE_INT_ARGB);
	}
	
}
