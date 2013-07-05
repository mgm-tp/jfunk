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
package com.mgmtp.jfunk.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Random;

/**
 * This class helps to send POST HTTP requests with files.
 * 
 * @author Vlad Patryshev
 * @deprecated no longer necessary when HTML validation is change. See MQA-820
 */
@Deprecated
public class MultipartPostRequest {
	URLConnection connection;
	OutputStream os = null;

	protected void connect() throws IOException {
		if (os == null) {
			os = connection.getOutputStream();
		}
	}

	protected void write(final char c) throws IOException {
		connect();
		os.write(c);
	}

	protected void write(final String s) throws IOException {
		connect();
		os.write(s.getBytes());
	}

	protected void newline() throws IOException {
		connect();
		write("\r\n");
	}

	protected void writeln(final String s) throws IOException {
		connect();
		write(s);
		newline();
	}

	private static Random random = new Random();

	protected static String randomString() {
		return Long.toString(random.nextLong(), 36);
	}

	String boundary = "---------------------------" + randomString() + randomString() + randomString();

	private void boundary() throws IOException {
		write("--");
		write(boundary);
	}

	/**
	 * Creates a new multipart POST HTTP request on a freshly opened URLConnection
	 * 
	 * @param connection
	 *            an already open URL connection
	 */
	public MultipartPostRequest(final URLConnection connection) {
		this.connection = connection;
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	private void writeName(final String name) throws IOException {
		newline();
		write("Content-Disposition: form-data; name=\"");
		write(name);
		write('"');
	}

	private static void pipe(final InputStream in, final OutputStream out) throws IOException {
		byte[] buf = new byte[500000];
		int nread;
		synchronized (in) {
			while ((nread = in.read(buf, 0, buf.length)) >= 0) {
				out.write(buf, 0, nread);
			}
		}
		out.flush();
	}

	/**
	 * Adds a file parameter to the request
	 * 
	 * @param name
	 *            parameter name
	 * @param filename
	 *            the name of the file
	 * @param is
	 *            input stream to read the contents of the file from
	 */
	public void setParameter(final String name, final String filename, final InputStream is) throws IOException {
		boundary();
		writeName(name);
		write("; filename=\"");
		write(filename);
		write('"');
		newline();
		write("Content-Type: ");
		String type = URLConnection.guessContentTypeFromName(filename);
		if (type == null) {
			type = "application/octet-stream";
		}
		writeln(type);
		newline();
		pipe(is, os);
		newline();
	}

	/**
	 * Posts the request to the server, with all the parameters that were added.
	 * 
	 * @return input stream with the server response
	 */
	public InputStream post() throws IOException {
		boundary();
		writeln("--");
		os.close();
		return connection.getInputStream();
	}
}