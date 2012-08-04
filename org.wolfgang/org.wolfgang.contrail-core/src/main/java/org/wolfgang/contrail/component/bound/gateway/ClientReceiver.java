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
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>GatewayReceiverFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClientReceiver implements DataReceiver<byte[]> {

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
	ClientReceiver(ContextFactory factory, String reference) throws URISyntaxException, CannotCreateClientException, ClientFactoryCreationException {
		super();

		final URI uri = new URI(reference);
		final Client client = factory.getClientFactory().get(uri.getScheme());

		client.connect(uri, new DataSenderFactory<byte[], byte[]>() {
			@Override
			public DataSender<byte[]> create(DataReceiver<byte[]> component) throws CannotCreateDataSenderException {
				setComponentReceiver(component);

				return new DataSender<byte[]>() {
					@Override
					public void close() throws IOException {
						componentSender.close();
					}

					@Override
					public void sendData(byte[] data) throws DataHandlerException {
						componentSender.sendData(data);
					}
				};
			}
		});
	}

	@Override
	public void close() throws IOException {
		clientReceiver.close();
	}

	/**
	 * Set the value of component Receiver
	 * 
	 * @param componentSender
	 *            the component receiver to set
	 */
	void setComponentReceiver(DataReceiver<byte[]> componentReceiver) {
		this.clientReceiver = componentReceiver;
	}

	/**
	 * Set the value of component Sender
	 * 
	 * @param componentSender
	 *            the component sender to set
	 */
	void setComponentSender(DataSender<byte[]> componentSender) {
		this.componentSender = componentSender;
	}

	@Override
	public void receiveData(byte[] data) throws DataHandlerException {
		clientReceiver.receiveData(data);
	}
}
