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

import static org.jboss.netty.channel.Channels.pipeline;

import org.contrail.web.connection.web.handler.WebClientSocketHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.contrail.common.concurrent.Promise;
import org.contrail.stream.component.SourceComponent;

/**
 * <code>WebServerPipelineFactory</code> is able to create the right channel
 * pipeline for both web and eb socket management.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class WebClientPipelineFactory implements ChannelPipelineFactory {

	private final WebSocketClientHandshaker handshaker;
	private final WebClientSocketHandler wsRequestHandler;
	private final Promise<Integer,Exception> connectionEstablished;
	private final Promise<SourceComponent<String, String>, Exception> sourceComponentPromise;

	public WebClientPipelineFactory(WebSocketClientHandshaker handshaker, WebClientSocketHandler wsRequestHandler, Promise<Integer, Exception> connectionEstablished, Promise<SourceComponent<String,String>,Exception> sourceComponentPromise) {
		this.handshaker = handshaker;
		this.wsRequestHandler = wsRequestHandler;
		this.connectionEstablished = connectionEstablished;
		this.sourceComponentPromise = sourceComponentPromise;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = pipeline();
		pipeline.addLast("http_encoder", new HttpRequestEncoder());
		pipeline.addLast("http_decoder", new HttpResponseDecoder());
		pipeline.addLast("ws-handler", new WebClientHandler(handshaker, wsRequestHandler, connectionEstablished, sourceComponentPromise));
		return pipeline;
	}
}