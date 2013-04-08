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

package org.wolfgang.contrail.network.connection.web.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.network.connection.exception.WebClientConnectionException;

/**
 * <code>WSRequestHander</code> defines handler for web socket requests
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface WebClientSocketHandler {

	/**
	 * 
	 * @param contex
	 * @param frame
	 * @throws Exception
	 */
	void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception;

	/**
	 * 
	 * @param context
	 * @param sourceComponentPromise 
	 * @return
	 * @throws WebClientConnectionException 
	 */
	void notifyHandShake(ChannelHandlerContext context, Promise<SourceComponent<String,String>,Exception> sourceComponentPromise) throws WebClientConnectionException;

}