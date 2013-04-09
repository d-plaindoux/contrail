/*
 * Copyright (C)2012 D. Plaindoux.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.contrail.web.connection.web.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.contrail.web.connection.exception.WebClientConnectionException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.contrail.common.concurrent.Promise;
import org.contrail.stream.component.SourceComponent;

public class WebClient implements Callable<ChannelFuture>, Closeable {

	private final URI uri;
	private final AtomicReference<ChannelFuture> reference;
	private final Promise<Integer, Exception> connectionEstablished;
	private final WebClientFactory webClientFactory;
	
	private Promise<SourceComponent<String, String>, Exception> sourceComponentPromise;

	WebClient(URI uri, WebClientFactory webClientFactory) {
		super();
		this.uri = uri;
		this.webClientFactory = webClientFactory;
		this.reference = new AtomicReference<ChannelFuture>();
		this.connectionEstablished = Promise.create();
	}

	public WebClient awaitEstablishment() throws WebClientConnectionException {
		return this.awaitEstablishment(10, TimeUnit.SECONDS);
	}

	public WebClient awaitEstablishment(int time, TimeUnit unit) throws WebClientConnectionException {
		try {
			this.connectionEstablished.getFuture().get(time, unit);
			return this;
		} catch (InterruptedException e) {
			throw new WebClientConnectionException(e);
		} catch (ExecutionException e) {
			throw new WebClientConnectionException(e.getCause());
		} catch (TimeoutException e) {
			throw new WebClientConnectionException(e);
		}
	}

	public WebClient connect(Promise<SourceComponent<String, String>, Exception> sourceComponentPromise) throws Exception {
		this.sourceComponentPromise = sourceComponentPromise;
		this.call();
		return this;
	}

	public int getId() throws WebClientConnectionException {
		try {
			return this.connectionEstablished.getFuture().get(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new WebClientConnectionException(e);
		} catch (ExecutionException e) {
			throw new WebClientConnectionException(e.getCause());
		} catch (TimeoutException e) {
			throw new WebClientConnectionException(e);
		}
	}

	@Override
	public ChannelFuture call() throws Exception {
		final Map<String, String> customHeaders = new HashMap<String, String>();
		final WebSocketClientHandshaker handshaker = new WebSocketClientHandshakerFactory().newHandshaker(uri, WebSocketVersion.V13, null, false, customHeaders);
		final ChannelPipelineFactory channelPipelineFactory = new WebClientPipelineFactory(handshaker, webClientFactory.getWsRequestHandler(), connectionEstablished ,sourceComponentPromise);

		final ChannelFuture channelFuture = webClientFactory.connect(uri.getHost(), uri.getPort(), channelPipelineFactory);
		channelFuture.awaitUninterruptibly(10, TimeUnit.SECONDS);

		final ChannelFuture handshakedChannelFuture = handshaker.handshake(channelFuture.getChannel());
		handshakedChannelFuture.awaitUninterruptibly(10, TimeUnit.SECONDS);

		this.reference.set(handshakedChannelFuture);

		return this.reference.get();
	}

	@Override
	public void close() throws IOException {
		if (this.reference.get() != null) {
			this.reference.get().getChannel().close();
		}
	}
}
