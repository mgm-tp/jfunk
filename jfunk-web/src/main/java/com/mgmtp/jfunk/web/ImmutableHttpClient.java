package com.mgmtp.jfunk.web;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Immutable {@link HttpClient} wrapper. Can be used to expose an {@link HttpClient} without
 * allowing for it to be changed.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ImmutableHttpClient implements HttpClient {

	private final HttpClient delegate;

	public ImmutableHttpClient(final HttpClient delegate) {
		this.delegate = delegate;
	}

	@Override
	public HttpParams getParams() {
		return delegate.getParams();
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return delegate.getConnectionManager();
	}

	@Override
	public HttpResponse execute(final HttpUriRequest request) throws IOException, ClientProtocolException {
		return delegate.execute(request);
	}

	@Override
	public HttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException, ClientProtocolException {
		return delegate.execute(request, context);
	}

	@Override
	public HttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException, ClientProtocolException {
		return delegate.execute(target, request);
	}

	@Override
	public HttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException,
			ClientProtocolException {
		return delegate.execute(target, request, context);
	}

	@Override
	public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		return delegate.execute(request, responseHandler);
	}

	@Override
	public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context)
			throws IOException,
			ClientProtocolException {
		return delegate.execute(request, responseHandler, context);
	}

	@Override
	public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException,
			ClientProtocolException {
		return delegate.execute(target, request, responseHandler);
	}

	@Override
	public <T> T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler,
			final HttpContext context) throws IOException,
			ClientProtocolException {
		return delegate.execute(target, request, responseHandler, context);
	}
}