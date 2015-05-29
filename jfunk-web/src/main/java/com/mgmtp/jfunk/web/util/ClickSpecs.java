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

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Enum specifying where to click within the bounds of a {@link WebElement}.
 *
 * @author rnaegele
 */
public enum ClickSpecs {
	TOP_LEFT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(leftX(rect), topY(rect));
		}
	},
	TOP_MIDDLE {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(middleX(rect), topY(rect));
		}
	},
	TOP_RIGHT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(rightX(rect), topY(rect));
		}
	},
	MIDDLE_LEFT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(leftX(rect), middleY(rect));
		}
	},
	CENTER {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(middleX(rect), middleY(rect));
		}
	},
	MIDDLE_RIGHT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(rightX(rect), middleY(rect));
		}
	},
	BOTTOM_LEFT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(leftX(rect), bottomY(rect));
		}
	},
	BOTTOM_MIDDLE {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(middleX(rect), bottomY(rect));
		}
	},
	BOTTOM_RIGHT {
		@Override
		public Point getPoint(final Rectangle rect) {
			return new Point(rightX(rect), bottomY(rect));
		}
	};

	public abstract Point getPoint(Rectangle rect);

	private static int leftX(@SuppressWarnings("unused") final Rectangle rect) {
		return 0;
	}

	private static int rightX(final Rectangle rect) {
		return rect.right - rect.left - 1;
	}

	private static int middleX(final Rectangle rect) {
		return rightX(rect) / 2;
	}

	private static int topY(@SuppressWarnings("unused") final Rectangle rect) {
		return 0;
	}

	private static int bottomY(final Rectangle rect) {
		return rect.bottom - rect.top - 1;
	}

	private static int middleY(final Rectangle rect) {
		return bottomY(rect) / 2;
	}
}
