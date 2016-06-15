/*
 * Copyright (c) 2015 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.web.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.Point;

/**
 * @author rnaegele
 */
public class Rectangle {
	public int top, left, bottom, right, width, height;

	public Rectangle(final int top, final int left, final int bottom, final int right, final int width, final int height) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the point that specifies the center of the rectangle.
	 *
	 * @return center of the rectangle.
	 */
	public Point center() {
		return new Point(left + width / 2, top + height / 2);
	}

	/**
	 * Returns the intersection with the other rectangle or null if the two rectangles do not intersect.
	 *
	 * @param other the other rectangle.
	 *
	 * @return intersection rectangle or null.
	 */
	public Rectangle intersection(Rectangle other) {
		int left = Math.max(this.left, other.left);
		int top = Math.max(this.top, other.top);
		int right = Math.min(this.right, other.right);
		int bottom = Math.min(this.bottom, other.bottom);
		if (right >= left && bottom >= top) {
			int height = bottom - top;
			int width = right - left;
			return new Rectangle(top, left, bottom, right, width, height);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
