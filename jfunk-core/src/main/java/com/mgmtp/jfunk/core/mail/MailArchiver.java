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
package com.mgmtp.jfunk.core.mail;

import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.write;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.core.config.ModuleArchiveDir;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Class for archiving downloaded e-mail messages in a test archive.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class MailArchiver {
	private static final Pattern INVALID_FILE_NAME_CHARS = Pattern.compile("[#%&{}\\\\<>*?/\\s$!'\":@+`|=]");
	private static final String FILE_NAME_FORMAT = "%04d_%s.txt";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Provider<File> moduleArchiveDirProvider;
	private final Provider<MutableInt> counterProvider;

	@Inject
	MailArchiver(@ModuleArchiveDir final Provider<File> moduleArchiveDirProvider, final Provider<MutableInt> counterProvider) {
		this.moduleArchiveDirProvider = moduleArchiveDirProvider;
		this.counterProvider = counterProvider;
	}

	/**
	 * Saves a message's test content (including headers) in the current module archive in the
	 * specified sub-directory relative to the archive root. File names are prefixed with a
	 * left-padded four-digit integer counter (format: {@code %04d_%s.txt}).
	 * 
	 * @param message
	 *            the message
	 */
	public void archiveMessage(final MailMessage message) {
		try {
			String subject = message.getSubject();
			String fileName = subject == null
					? "no_subject"
					: INVALID_FILE_NAME_CHARS.matcher(subject).replaceAll("_");

			MutableInt counter = counterProvider.get();
			File file = new File("e-mails", String.format(FILE_NAME_FORMAT, counter.intValue(), fileName));
			file = new File(moduleArchiveDirProvider.get(), file.getPath());
			createParentDirs(file);
			log.debug("Archiving e-mail: {}", file);

			StrBuilder sb = new StrBuilder(500);
			for (Entry<String, String> header : message.getHeaders().entries()) {
				sb.append(header.getKey()).append('=').appendln(header.getValue());
			}
			sb.appendln("");
			sb.append(message.getText());

			write(sb.toString(), file, Charsets.UTF_8);
			counter.increment();
		} catch (IOException ex) {
			throw new MailException("Error archiving mail.", ex);
		}
	}
}
