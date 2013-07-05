/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.common.random;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Generic class for accessing a list of arbitrary elements randomly. It is guaranteed that all
 * elements are returned as fast as possible i.e. no element is returned twice as long there are
 * elements in the list which weren't returned yet. When all elements were returned at least once,
 * this class switches to "normal" operation i.e. all elements are returned uniformly distributed.
 * 
 */
public class RandomCollection<E> implements Randomizable<E>, Iterable<E> {

	/**
	 * Copy of the list which was passed as an argument during construction of this object. It is
	 * never changed so the original state is always available.
	 */
	private final List<E> originalElements;

	/**
	 * List of elements to be handled with priority. When this list is empty, all elements from the
	 * original list were returned exactly once. This list remains empty afterwards.
	 */
	private List<E> priorityElements;

	/**
	 * This list will be used during "normal" operation. As soon as the number of elements drops
	 * below the original number of elements all original elements are added again.
	 */
	private List<E> currentElements;

	private final MathRandom random;

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> itr = originalElements.iterator();
		return new Iterator<E>() {
			@Override
			public boolean hasNext() {
				return itr.hasNext();
			}

			@Override
			public E next() {
				return itr.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove is not allowed");
			}
		};
	}

	/**
	 * Creates a new instance with the elements of specified list.
	 */
	public RandomCollection(final MathRandom random, final Collection<E> elements) {
		this.random = random;
		this.originalElements = Lists.newArrayList(elements);
		this.priorityElements = Lists.newArrayList(elements);
		Collections.shuffle(priorityElements);
		this.currentElements = Lists.newArrayListWithExpectedSize(2 * elements.size());
		this.currentElements.addAll(elements);
	}

	/**
	 * Returns the next random element.
	 * 
	 * @return if the original list was empty, {@code null} is returned
	 */
	@Override
	public E get() {
		if (originalElements.isEmpty()) {
			return null;
		}
		if (!priorityElements.isEmpty()) {
			return priorityElements.remove(0);
		}
		if (currentElements.size() <= originalElements.size()) {
			currentElements.addAll(originalElements);
		}
		int index = random.getInt(currentElements.size() - 1);
		return currentElements.remove(index);
	}

	/**
	 * Returns true if all elements were returned at least once.
	 */
	@Override
	public boolean isAllHit() {
		return priorityElements.isEmpty();
	}

	/**
	 * Resets this object so it behaves like a newly constructed instance.
	 */
	@Override
	public void reset() {
		this.priorityElements = Lists.newArrayList(originalElements);
		Collections.shuffle(priorityElements);
		this.currentElements = Lists.newArrayListWithExpectedSize(2 * originalElements.size());
		this.currentElements.addAll(originalElements);
	}

	@Override
	public int size() {
		return originalElements.size();
	}
}