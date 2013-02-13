/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.data.GeneratorState;

/**
 * Object to control value generation. A FieldCase has a value and a Flag. The Flag determines
 * whether allowed or also forbidden characters are part of the value. The integer value mainly
 * determines where within the allowed range the generated value is located. Depending on the field
 * type this value can also show the field length or - for fields with fixed values - the index of
 * the value to be chosen.
 * 
 * @version $Id$
 */
public class FieldCase {

	public static final int JUST_ABOVE_MAX = -41;
	public static final int JUST_BELOW_MIN = -42;

	/**
	 * Generates a field shorter than allowed
	 */
	public static final FieldCase LT_MIN = new FieldCase(-1000, 0, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			Range r = ctrl.getRange();
			MathRandom random = GeneratorState.currentState().getMathRandom();
			int min = r.getMin();
			int rnd = random.getInt(min);
			if (min > 0 && rnd < 1) {
				rnd = 1;
			}
			return new FieldCase(min - rnd, true);
		}
	};

	/**
	 * Generates a field shorter by one point than allowed. If min = 0 a false character will be
	 * generated.
	 */
	public static final FieldCase ONE_LT_MIN = new FieldCase(-1000, 0, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(JUST_BELOW_MIN, true);
		}
	};

	/**
	 * Generates a field longer than allowed
	 */
	public static final FieldCase GT_MAX = new FieldCase(-1500, 0, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			Range r = ctrl.getRange();
			MathRandom random = GeneratorState.currentState().getMathRandom();
			int rnd = random.getInt(r.getRange());
			// at least one more than the maximum
			if (rnd < 1) {
				rnd = 1;
			}
			return new FieldCase(r.getMax() + rnd, true);
		}
	};

	/**
	 * Generates a field longer by one point than allowed.
	 */
	public static final FieldCase ONE_GT_MAX = new FieldCase(JUST_ABOVE_MAX, 0, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(JUST_ABOVE_MAX, true);
		}
	};

	/**
	 * Generates a field with average length containing - depending on the setting of the control
	 * object for the field - allowed or also forbidden characters.
	 */
	public static final FieldCase AVG = new FieldCase(-2000) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(getRandomSize(ctrl));
		}
	};
	/**
	 * Generates a field with average length containing only allowed characters.
	 */
	public static final FieldCase AVG_GOOD = new FieldCase(-2000) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(getRandomSize(ctrl));
		}
	};
	/**
	 * Generates a field with average length also containing forbidden characters.
	 */
	public static final FieldCase AVG_BAD = new FieldCase(-2000, -1, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(getRandomSize(ctrl), -1, true);
		}
	};
	/**
	 * Generates a field with average length containing exactly one forbidden character.
	 */
	public static final FieldCase ONE_BAD = new FieldCase(-2000, 1, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(getRandomSize(ctrl), 1, true);
		}
	};
	/**
	 * Generates a fiel with length 1 with a forbidden character
	 */
	public static final FieldCase ONE_BAD_CHAR = new FieldCase(-2000, 1, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(1, 1, true);
		}
	};
	/**
	 * Generates a field with maximal length containing only forbidden characters.
	 */
	public static final FieldCase ALL_BAD = new FieldCase(-2000, -2, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(ctrl.getRange().getMax(), -2, true);
		}
	};
	/**
	 * Generates a field with minimum length containing only forbidden characters
	 */
	public static final FieldCase MIN_BAD = new FieldCase(-2500, 1, true) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(ctrl.getRange().getMin(), -2, true);
		}
	};

	/**
	 * Generates a field with minimum length
	 */
	public static final FieldCase MIN = new FieldCase(-2500) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(ctrl.getRange().getMin());
		}
	};
	/**
	 * Generates a field with maximum length
	 */
	public static final FieldCase MAX = new FieldCase(-3000) {
		@Override
		public FieldCase getInstance(final FieldControl ctrl) {
			return new FieldCase(ctrl.getRange().getMax());
		}
	};

	/**
	 * Generates a field that is empty (null)
	 */
	public static final FieldCase NULL = new FieldCase(-5000);

	/**
	 * Generates a character string consisting only of spaces
	 */
	public static final FieldCase BLANK = new FieldCase(-6000);

	private final int bad;
	private final int size;
	private final boolean negative;

	public FieldCase(final int size) {
		this(size, 0, false);
	}

	public FieldCase(final int size, final boolean negative) {
		this(size, 0, negative);
	}

	public FieldCase(final int size, final int bad) {
		this(size, bad, false);
	}

	/**
	 * @param bad
	 *            Number of forbidden characters. If the value is 0 no bad values are generated
	 * @param negative
	 *            shows whether it is a FieldCase that generates valid {@code false} or invalid
	 *            {@code true} Data
	 */
	public FieldCase(final int size, final int bad, final boolean negative) {
		this.size = size;
		this.bad = bad;
		this.negative = negative;
	}

	/**
	 * Returns a FieldCase instance depending on the specified range. The standard implementation
	 * just returns {@code this}. Special implementations have to override this method to initialize
	 * the respective FieldCase object.
	 * 
	 * @param fieldControl
	 *            the FieldControl object, requesting the FieldCase.
	 */
	public FieldCase getInstance(final FieldControl fieldControl) {
		return this;
	}

	private static int getRandomSize(final FieldControl fieldControl) {
		MathRandom rnd = GeneratorState.currentState().getMathRandom();
		Range range = fieldControl.getRange();
		int c = range.getRange();
		return range.getMin() + (c > 1 ? 1 + rnd.getInt(c - 2) : rnd.getInt(c));
	}

	public boolean isBad() {
		return bad != 0;
	}

	public int getBad() {
		return bad;
	}

	public int getSize() {
		return size;
	}

	public boolean isNegative() {
		return negative;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldCase other = (FieldCase) obj;
		if (bad != other.bad) {
			return false;
		}
		if (negative != other.negative) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bad;
		result = prime * result + (negative ? 1231 : 1237);
		result = prime * result + size;
		return result;
	}
}