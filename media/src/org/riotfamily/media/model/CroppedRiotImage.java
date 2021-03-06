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
package org.riotfamily.media.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.common.image.ImageCropper;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
@Entity
@DiscriminatorValue("crop")
public class CroppedRiotImage extends RiotImage {

	private RiotImage original;
	
	private int x;
	
	private int y;
	
	private int scaledWidth;

	public CroppedRiotImage() {
	}

	public CroppedRiotImage(RiotImage original, ImageCropper cropper,
			int width, int height, int x, int y, int scaledWidth) throws IOException {
		
		this.original = original;
		this.x = x;
		this.y = y;
		this.scaledWidth = scaledWidth;
		setCreationDate(new Date());
		setFileName(original.getFileName());
		setUri(mediaService.store(null, original.getFileName()));
		
		File croppedFile = getFile();
		cropper.cropImage(original.getFile(), croppedFile, width, height, x, y, 
				scaledWidth);

		setSize(croppedFile.length());
		inspect(croppedFile);
	}

	@ManyToOne
	@Cascade(CascadeType.ALL)
	public RiotImage getOriginal() {
		return this.original;
	}

	public void setOriginal(RiotImage original) {
		this.original = original;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public void setScaledWidth(int scaledWidth) {
		this.scaledWidth = scaledWidth;
	}
	
}
