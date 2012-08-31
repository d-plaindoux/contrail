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

package org.wolfgang.contrail.network.connection.web;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>WebSocketServerPipelineFactory</code> is able to create the right
 * channel pipeline for both web and eb socket management.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class WebServerPipelineFactory implements ChannelPipelineFactory {

	private final UpStreamDataFlowFactory<String, String> factory;

	/**
	 * Constructor
	 */
	public WebServerPipelineFactory(UpStreamDataFlowFactory<String, String> upStreamDataHandlerFactory) {
		this.factory = upStreamDataHandlerFactory;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = pipeline();
		pipeline.addLast("http_decoder", new HttpRequestDecoder());
		pipeline.addLast("http_aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("http_encoder", new HttpResponseEncoder());
		pipeline.addLast("websocket_handler", new WebServerHandler(this.factory));
		return pipeline;
	}
}