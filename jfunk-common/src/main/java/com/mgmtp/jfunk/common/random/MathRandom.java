package com.mgmtp.jfunk.common.random;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Utility class for generating random numbers
 * 
 * @version $Id$
 */
public class MathRandom {

	private final Random random;
	private final long seed;

	/**
	 * Will be increased on each access to get an unique seed value
	 */
	private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(3214984168468513L);

	public MathRandom() {
		seed = nextSeed();
		random = new Random(seed);
	}

	public MathRandom(final long seed) {
		this.seed = seed;
		random = new Random(seed);
	}

	public static long nextSeed() {
		return SEED_UNIQUIFIER.addAndGet(System.nanoTime());
	}

	public long getSeed() {
		return seed;
	}

	public Random getRandom() {
		return random;
	}

	/**
	 * Returns a random boolean value
	 */
	public boolean getBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Returns a random integer number in the range of [0, max].
	 * 
	 * @param max
	 *            maximum value for generated number
	 */
	public int getInt(final int max) {
		if (max <= 0) {
			return 0;
		}
		double rnd = (max + 1) * random.nextDouble();
		return (int) rnd;
	}

	/**
	 * Returns a random integer number in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated number
	 * @param max
	 *            maximum value for generated number
	 */
	public int getInt(final int min, final int max) {
		return min(min, max) + getInt(abs(max - min));
	}

	/**
	 * Returns a random decimal number in the range of [0, max].
	 * 
	 * @param max
	 *            maximum value for generated number
	 */
	public double getDouble(final double max) {
		return (max + Math.ulp(max)) * random.nextDouble();
	}

	/**
	 * Returns a random decimal number in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated number
	 * @param max
	 *            maximum value for generated number
	 */
	public double getDouble(final double min, final double max) {
		return min(min, max) + getDouble(abs(max - min));
	}

	/**
	 * Returns a random integer number in the range of [0, max].
	 * 
	 * @param max
	 *            maximum value for generated number
	 */
	public long getLong(final long max) {
		if (max <= 0) {
			return 0;
		}
		double rnd = (max + 1) * random.nextDouble();
		return (long) rnd;
	}

	/**
	 * Returns a random integer number in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated number
	 * @param max
	 *            maximum value for generated number
	 */
	public long getLong(final long min, final long max) {
		return min(min, max) + getLong(abs(max - min));
	}

	/**
	 * Returns a random date in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated date
	 * @param max
	 *            maximum value for generated date
	 */
	public Date getDate(final Date min, final Date max) {
		return getDate(min.getTime(), max.getTime());
	}

	/**
	 * Returns a random date in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated date (in milliseconds)
	 * @param max
	 *            maximum value for generated date (in milliseconds)
	 */
	public Date getDate(final long min, final long max) {
		long millis = getLong(min, max);
		return DateUtils.truncate(new Date(millis), Calendar.DATE);
	}

	/**
	 * Returns a random Calendar object in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated Calendar object
	 * @param max
	 *            maximum value for generated Calendar object
	 */
	public Calendar getCalendar(final Calendar min, final Calendar max) {
		long millis = getLong(min.getTimeInMillis(), max.getTimeInMillis());
		return createCalendar(millis);
	}

	/**
	 * Returns a random Calendar object in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated Calendar object
	 * @param max
	 *            maximum value for generated Calendar object
	 */
	public Calendar getCalendar(final Date min, final Date max) {
		long millis = getLong(min.getTime(), max.getTime());
		return createCalendar(millis);
	}

	/**
	 * Returns a random Calendar object in the range of [min, max].
	 * 
	 * @param min
	 *            minimum value for generated Calendar object (in milliseconds)
	 * @param max
	 *            maximum value for generated Calendar object (in milliseconds)
	 */
	public Calendar getCalendar(final long min, final long max) {
		long millis = getLong(min, max);
		return createCalendar(millis);
	}

	private Calendar createCalendar(final long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return DateUtils.truncate(cal, Calendar.DATE);
	}
}