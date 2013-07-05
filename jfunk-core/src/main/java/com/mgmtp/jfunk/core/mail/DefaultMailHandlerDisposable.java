/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.mail;

import javax.inject.Singleton;

import com.mgmtp.jfunk.common.util.Disposable;

/**
 * 
 * @author rnaegele
 */
@Singleton
public class DefaultMailHandlerDisposable implements Disposable<MailHandler> {

	@Override
	public void dispose(final MailHandler source) {
		source.releaseAllMailAccountsForThread();
	}
}