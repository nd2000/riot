package org.riotfamily.common.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

public class ImageMagickThumbnailer implements Thumbnailer {

	private static Log log = LogFactory.getLog(ImageMagickThumbnailer.class);
	
	private String convertCommand = "/usr/bin/convert";
	
	private int maxWidth;
	
	private int maxHeight;
	
	private boolean crop;
	
	private String backgroundColor;
	
	private String format = "jpg";
	
	
	public void setConvertCommand(String convertCommand) {
		this.convertCommand = convertCommand;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	public void setCrop(boolean crop) {
		this.crop = crop;
	}
	
	/**
	 * If a background color is specified, the image will be exactly resized to 
	 * <code>maxWidth x maxHeight</code>, unused space will be filled with the
	 * given color. If <code>crop</code> is enabled, the value is ignored.
	 * <p>
	 * Colors are represented in ImageMagick in the same form used by SVG:
	 * <pre>
  	 * name                 (color name like red, blue, white, etc.)
  	 * #RGB                 (R,G,B are hex numbers, 4 bits each)
  	 * #RRGGBB              (8 bits each)
  	 * #RRRGGGBBB           (12 bits each)
  	 * #RRRRGGGGBBBB        (16 bits each)
  	 * #RGBA                (4 bits each)
  	 * #RRGGBBOO            (8 bits each)
  	 * #RRRGGGBBBOOO        (12 bits each)
  	 * #RRRRGGGGBBBBOOOO    (16 bits each)
  	 * rgb(r,g,b)           0-255 for each of rgb
  	 * rgba(r,g,b,a)        0-255 for each of rgb and 0-1 for alpha
  	 * cmyk(c,m,y,k)        0-255 for each of cmyk
  	 * cmyka(c,m,y,k,a)     0-255 for each of cmyk and 0-1 for alpha
  	 * </pre>
  	 * </p>
  	 * For a transparent background specify an alpha value, 
  	 * like in <code>#ffff</code>.
	 */
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean supports(String mimeType) {
		return mimeType.startsWith("image");
	}
	
	public void renderThumbnail(File source, String mimeType, OutputStream out) 
			throws IOException {
		
		File dest = File.createTempFile(".magick", "." + format, 
				source.getParentFile());
		
		try {
			if (convert(source, dest)) {
				FileCopyUtils.copy(new FileInputStream(dest), out);
			}
			else {
				throw new IOException("Failed to create thumbnail.");
			}
		}
		finally {
			dest.delete();
		}
	}
	
	private boolean convert(File source, File dest) throws IOException {
		ArrayList cmd = new ArrayList();
		cmd.add(convertCommand);
		cmd.add(source.getAbsolutePath());
		cmd.add("-resize");
		if (crop) {
			cmd.add("x" + maxHeight * 2);
			cmd.add("-resize");
			cmd.add(maxWidth * 2 + "x<");
			cmd.add("-resize");
			cmd.add("50%");
			cmd.add("-crop");
			cmd.add(maxWidth + "x" + maxHeight + "+0+0");
			cmd.add("+repage");
		}
		else {
			cmd.add(maxWidth + "x" + maxHeight + ">");
			if (backgroundColor != null) {
				cmd.add("-size");
				cmd.add(maxWidth + "x" + maxHeight);
				cmd.add("xc:" + backgroundColor);
				cmd.add("+swap");
				cmd.add("-gravity");
				cmd.add("center");
				cmd.add("-composite");
			}
		}
		cmd.add("-colorspace");
		cmd.add("RGB");
		cmd.add(dest.getAbsolutePath());
		return exec(StringUtils.toStringArray(cmd));
	}


	private boolean exec(String[] command) throws IOException {
		log.debug(StringUtils.arrayToDelimitedString(command, " "));
		Process proc = Runtime.getRuntime().exec(command);
		try {
			int exitStatus = proc.waitFor();
			return exitStatus == 0;
		}
		catch (InterruptedException e) {
			return false;
		}
	}

}
