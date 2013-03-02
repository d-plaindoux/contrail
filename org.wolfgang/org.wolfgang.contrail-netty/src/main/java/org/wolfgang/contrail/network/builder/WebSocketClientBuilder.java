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

package org.wolfgang.contrail.network.builder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.SourceComponentNotifier;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.network.connection.exception.WebClientConnectionException;
import org.wolfgang.contrail.network.connection.web.client.WebClient;
import org.wolfgang.contrail.network.connection.web.client.WebClient.Instance;
import org.wolfgang.network.packet.Packet;

/**
 * <code>WebSocketClientBuilder</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebSocketClientBuilder extends StandardClientBuilder {

	private final WebClient webClient;
	private final Map<Integer, Promise<SourceComponent<String, String>, Exception>> waitingSources;

	public WebSocketClientBuilder(String endPoint) {
		super(endPoint);
		this.waitingSources = new HashMap<Integer, Promise<SourceComponent<String, String>, Exception>>();
		this.webClient = WebClient.create(new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				synchronized (waitingSources) {
					final Promise<SourceComponent<String, String>, Exception> promise = waitingSources.remove(identifier);
					if (promise != null) {
						promise.success(source);
					} else {
						throw new CannotCreateComponentException("TODO");
					}
				}
			}
		});
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
	 * @throws WebClientConnectionException
	 * @throws Exception
	 * @throws URISyntaxException
	 */
	private SourceComponent<String, String> newClientInstance() throws WebClientConnectionException, Exception, URISyntaxException {
		final Promise<SourceComponent<String, String>, Exception> promise;
		final Instance instance;
		synchronized (waitingSources) {
			instance = this.webClient.instance(new URI(this.getEndPoint())).awaitEstablishment();
			promise = Promise.<SourceComponent<String, String>, Exception> create();
			this.waitingSources.put(instance.getId(), promise);
		}

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
	private void abortClientInstance(final Instance instance) throws WebClientConnectionException, IOException {
		synchronized (waitingSources) {
			this.waitingSources.remove(instance.getId());
			instance.close();
		}
	}
}
