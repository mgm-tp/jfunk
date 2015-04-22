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
package com.mgmtp.jfunk.web.ssl;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 * {@link LayeredSchemeSocketFactory} implementation for SSL and client authentication. As
 * HttpClient seems to have a problem when connecting to SSL sites which require a client
 * certificate we are using this SocketFactory implementation.
 * 
 */
public class JFunkSSLSocketFactory implements LayeredSchemeSocketFactory {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final URL keyStoreUrl;
	private final char[] keyStorePassword;
	private final String keyStoreType;
	private final URL trustStoreUrl;
	private final char[] trustStorePassword;
	private final String trustStoreType;

	private volatile SSLContext sslContext;

	/**
	 * @param keyStoreUrl
	 *            URL of the key store file. May be <tt>null</tt> if HTTPS client authentication is
	 *            not to be used.
	 * @param keyStorePassword
	 *            Password to unlock the key store. IMPORTANT: this implementation assumes that the
	 *            same password is used to protect the key and the key store itself.
	 * @param keyStoreType
	 *            Type of the key store, e. g. jks, PKCS12 (default is jks)
	 * @param trustStoreUrl
	 *            URL of the trust store file. May be <tt>null</tt> if HTTPS server authentication
	 *            is not to be used.
	 * @param trustStorePassword
	 *            Password to unlock the trust store.
	 * @param trustStoreType
	 *            Type of the trust store, e. g. jks, PKCS12 (default is jks)
	 */
	public JFunkSSLSocketFactory(final URL keyStoreUrl, final String keyStorePassword, final String keyStoreType, final URL trustStoreUrl,
			final String trustStorePassword, final String trustStoreType) {

		this.keyStoreUrl = keyStoreUrl;
		this.keyStorePassword = keyStorePassword != null ? keyStorePassword.toCharArray() : null;
		this.keyStoreType = keyStoreType != null ? keyStoreType : "jks";

		this.trustStoreUrl = trustStoreUrl;
		this.trustStorePassword = trustStorePassword != null ? trustStorePassword.toCharArray() : null;
		this.trustStoreType = trustStoreType != null ? trustStoreType : "jks";
	}

	private KeyStore createStore(final URL url, final char[] password, final String type) throws KeyStoreException, IOException,
			NoSuchAlgorithmException, CertificateException {
		log.debug("Initializing key store");

		KeyStore keystore = KeyStore.getInstance(type);
		InputStream is = null;
		try {
			is = url.openStream();
			keystore.load(is, password);
			return keystore;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private KeyManager[] createKeyManagers(final KeyStore keyStore, final char[] password) throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		log.debug("Initializing key managers");

		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keyStore, password);

		return kmfactory.getKeyManagers();
	}

	private TrustManager[] createTrustManagers(final KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException {
		log.debug("Initializing trust managers");

		TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(keyStore);

		TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		for (int i = 0; i < trustmanagers.length; ++i) {
			if (trustmanagers[i] instanceof X509TrustManager) {
				trustmanagers[i] = new JFunkX509TrustManager((X509TrustManager) trustmanagers[i]);
			}
		}
		return trustmanagers;
	}

	private SSLContext createSSLContext() {
		try {
			KeyManager[] keymanagers = null;
			TrustManager[] trustmanagers = null;

			if (this.keyStoreUrl != null) {
				KeyStore keystore = createStore(this.keyStoreUrl, this.keyStorePassword, this.keyStoreType);

				if (log.isDebugEnabled()) {
					for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();) {
						String alias = aliases.nextElement();
						Certificate[] certs = keystore.getCertificateChain(alias);
						if (certs != null) {
							log.debug("Certificate chain '{}':", alias);
							for (int i = 0; i < certs.length; ++i) {
								if (certs[i] instanceof X509Certificate) {
									log.debug(" Certificate {}:", i + 1);
									logCertificate((X509Certificate) certs[i]);
								}
							}
						}
					}
				}

				keymanagers = createKeyManagers(keystore, this.keyStorePassword);
			}

			if (this.trustStoreUrl != null) {
				KeyStore keystore = createStore(this.trustStoreUrl, this.trustStorePassword, this.trustStoreType);

				if (log.isDebugEnabled()) {
					for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();) {
						String alias = aliases.nextElement();
						log.debug("Trusted certificate '{}':", alias);
						Certificate trustedcert = keystore.getCertificate(alias);
						if (trustedcert instanceof X509Certificate) {
							logCertificate((X509Certificate) trustedcert);
						}
					}
				}

				trustmanagers = createTrustManagers(keystore);
			}

			SSLContext context = SSLContext.getInstance("SSL");
			context.init(keymanagers, trustmanagers, null);

			return context;
		} catch (NoSuchAlgorithmException e) {
			throw new JFunkException("Unsupported algorithm exception: " + e.getMessage(), e);
		} catch (KeyStoreException e) {
			throw new JFunkException("Keystore exception: " + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new JFunkException("Key management exception: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new JFunkException("I/O error reading key store/trust store file: " + e.getMessage(), e);
		}
	}

	private void logCertificate(final X509Certificate cert) {
		log.debug("  Subject DN: {}", cert.getSubjectDN());
		log.debug("  Signature algorithm name: {}", cert.getSigAlgName());
		log.debug("  Valid from: {}", cert.getNotBefore());
		log.debug("  Valid until: {}", cert.getNotAfter());
		log.debug("  Issuer DN: {}", cert.getIssuerDN());
	}

	private SSLContext getSSLContext() {
		if (this.sslContext == null) {
			synchronized (this) {
				if (sslContext == null) {
					this.sslContext = createSSLContext();
				}
			}
		}
		return this.sslContext;
	}

	@Override
	public Socket createSocket(final HttpParams params) throws IOException {
		return getSSLContext().getSocketFactory().createSocket();
	}

	@Override
	public Socket connectSocket(final Socket sock, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress,
			final HttpParams params) throws IOException {
		checkArgument(remoteAddress != null, "Remote address may not be null");
		checkArgument(params != null, "HTTP parameters may not be null");

		Socket socket = sock != null ? sock : new Socket();
		if (localAddress != null) {
			socket.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			socket.bind(localAddress);
		}

		socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
		socket.connect(remoteAddress, HttpConnectionParams.getConnectionTimeout(params));

		if (socket instanceof SSLSocket) {
			return socket;
		}

		return getSSLContext().getSocketFactory().createSocket(socket, remoteAddress.getHostName(), remoteAddress.getPort(), true);
	}

	@Override
	public boolean isSecure(final Socket socket) throws IllegalArgumentException {
		checkArgument(socket != null, "Socket may not be null");
		checkArgument(socket instanceof SSLSocket, "Socket not created by this factory");
		checkArgument(!socket.isClosed(), "Socket is closed");
		return true;
	}

	@Override
	public Socket createLayeredSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
		return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}
}