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

package org.contrail.web.builder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.contrail.common.concurrent.Promise;
import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.Components;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.network.builder.StandardClientBuilder;
import org.contrail.web.connection.exception.WebClientConnectionException;
import org.contrail.web.connection.web.client.WebClient;
import org.contrail.web.connection.web.client.WebClientFactory;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>WebSocketClientBuilder</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebSocketClientBuilder extends StandardClientBuilder {

	private final WebClientFactory webClientFactory;

	public WebSocketClientBuilder(String endPoint) {
		super(endPoint);
		this.webClientFactory = WebClientFactory.create();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourceComponent<Packet, Packet> activate() throws ComponentConnectionRejectedException {
		try {
			return (SourceComponent<Packet, Packet>) Components.compose(newClientInstance(), this.getIntermediateComponent());
		} catch (Exception e) {
			throw new ComponentConnectionRejectedException(e);
		}
	}

	/**
	 * @return
	 * @throws org.contrail.web.connection.exception.WebClientConnectionException
	 * @throws Exception
	 * @throws URISyntaxException
	 */
	private SourceComponent<String, String> newClientInstance() throws WebClientConnectionException, Exception, URISyntaxException {
		final Promise<SourceComponent<String, String>, Exception> promise = Promise.<SourceComponent<String, String>, Exception> create();
		final WebClient instance = this.webClientFactory.client(new URI(this.getEndPoint())).connect(promise).awaitEstablishment();

		try {
			return promise.getFuture().get(10, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			abortClientInstance(instance);
			throw new ComponentConnectionRejectedException(e.getCause());
		} catch (Exception e) {
			abortClientInstance(instance);
			throw new ComponentConnectionRejectedException(e);
		}
	}

	/**
	 * @param instance
	 * @throws WebClientConnectionException
	 * @throws IOException
	 */
	private void abortClientInstance(final WebClient instance) throws WebClientConnectionException, IOException {
		instance.close();
	}
}
