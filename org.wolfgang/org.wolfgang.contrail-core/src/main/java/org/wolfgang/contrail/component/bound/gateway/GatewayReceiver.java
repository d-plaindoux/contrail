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

package org.wolfgang.contrail.component.bound.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.connection.ConnectionFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>GatewayReceiverFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class GatewayReceiver implements DataReceiver<byte[]>, DataSender<byte[]> {

	protected DataReceiver<byte[]> clientReceiver;
	protected DataSender<byte[]> componentSender;

	/**
	 * Constructor
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws CannotCreateClientException
	 * @throws ClientFactoryCreationException
	 */
	GatewayReceiver(ConnectionFactory factory, String... args) throws URISyntaxException, CannotCreateClientException, ClientFactoryCreationException {
		super();

		assert args.length == 1;

		final URI uri = new URI(args[0]);
		final Client client = factory.getClientFactory().get(uri.getScheme());

		client.connect(uri, new DataSenderFactory<byte[], byte[]>() {
			@Override
			public DataSender<byte[]> create(DataReceiver<byte[]> component) throws CannotCreateDataSenderException {
				GatewayReceiver.this.clientReceiver = component;
				return GatewayReceiver.this;
			}
		});
	}

	@Override
	public void close() throws IOException {
		clientReceiver.close();
		componentSender.close();
	}

	/**
	 * Set the value of componentSender
	 * 
	 * @param componentSender
	 *            the componentSender to set
	 */
	void setComponentSender(DataSender<byte[]> componentSender) {
		this.componentSender = componentSender;
	}

	@Override
	public void sendData(byte[] data) throws DataHandlerException {
		componentSender.sendData(data);
	}

	@Override
	public void receiveData(byte[] data) throws DataHandlerException {
		clientReceiver.receiveData(data);
	}
}
