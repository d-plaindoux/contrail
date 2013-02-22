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

package org.wolfgang.contrail.network.connection.web.client;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.wolfgang.contrail.network.connection.web.handler.WebClientSocketHandler;

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

	public WebClientPipelineFactory(WebSocketClientHandshaker handshaker, WebClientSocketHandler wsRequestHandler) {
		this.handshaker = handshaker;
		this.wsRequestHandler = wsRequestHandler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = pipeline();
		pipeline.addLast("http_encoder", new HttpRequestEncoder());
		pipeline.addLast("http_decoder", new HttpResponseDecoder());
		pipeline.addLast("ws-handler", new WebClientHandler(handshaker, wsRequestHandler));
		return pipeline;
	}
}