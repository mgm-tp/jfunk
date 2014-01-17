/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * Utility class for validating HTML markup against the W3C markup validation service.
 * 
 */
public class HtmlValidatorUtil {
	private static final Logger LOG = Logger.getLogger(HtmlValidatorUtil.class);
	private static final String ERRORS = "X-W3C-Validator-Errors";
	private static final String STATUS = "X-W3C-Validator-Status";
	private static final String WARNINGS = "X-W3C-Validator-Warnings";

	private HtmlValidatorUtil() {
		// don't allow instantiation
	}

	/**
	 * Validates an HTML file against the W3C markup validation service.
	 * 
	 * @param validationResultDir
	 *            target directory for validation result file
	 * @param props
	 *            properties must include the keys {@link WebConstants#W3C_MARKUP_VALIDATION_URL}
	 *            and {@link WebConstants#W3C_MARKUP_VALIDATION_LEVEL}
	 * @param file
	 *            HTML file which will be validated
	 */
	public static void validateHtml(final File validationResultDir, final Configuration props, final File file) throws IOException {
		Preconditions.checkArgument(StringUtils.isNotBlank(props.get(WebConstants.W3C_MARKUP_VALIDATION_URL)));
		InputStream is = null;
		BufferedReader br = null;
		InputStream fis = null;
		try {
			// Post HTML file to markup validation service as multipart/form-data
			URL url = new URL(props.get(WebConstants.W3C_MARKUP_VALIDATION_URL));
			URLConnection uc = url.openConnection();
			MultipartPostRequest request = new MultipartPostRequest(uc);
			fis = new FileInputStream(file);
			/*
			 * See http://validator.w3.org/docs/api.html#requestformat for a description of all
			 * parameters
			 */
			request.setParameter("uploaded_file", file.getPath(), fis);
			is = request.post();

			// Summary of validation is available in the HTTP headers
			String status = uc.getHeaderField(STATUS);
			int errors = Integer.parseInt(uc.getHeaderField(ERRORS));
			LOG.info("Page " + file.getName() + ": Number of HTML validation errors=" + errors);
			int warnings = Integer.parseInt(uc.getHeaderField(WARNINGS));
			LOG.info("Page " + file.getName() + ": Number of HTML validation warnings=" + warnings);

			// Check if result file has to be written
			String level = props.get(WebConstants.W3C_MARKUP_VALIDATION_LEVEL, "ERROR");
			boolean validate = false;
			if (StringUtils.equalsIgnoreCase(level, "WARNING") && (warnings > 0 || errors > 0)) {
				validate = true;
			} else if (StringUtils.equalsIgnoreCase(level, "ERROR") && errors > 0) {
				validate = true;
			} else if (StringUtils.equalsIgnoreCase("Invalid", status)) {
				validate = true;
			}

			if (validate) {
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				PrintWriter writer = null;
				String fileName = file.getName().substring(0, file.getName().length() - 5) + "_validation_result.html";
				FileUtils.forceMkdir(validationResultDir);
				File validationResultFile = new File(validationResultDir, fileName);
				try {
					writer = new PrintWriter(validationResultFile, "UTF-8");
					writer.write(sb.toString());
					LOG.info("Validation result saved in file " + validationResultFile.getName());
				} catch (IOException ex) {
					LOG.error("Could not write HTML file " + validationResultFile.getName() + "to directory", ex);
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(fis);
		}
	}
}