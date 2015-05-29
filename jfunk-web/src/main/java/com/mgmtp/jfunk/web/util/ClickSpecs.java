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
