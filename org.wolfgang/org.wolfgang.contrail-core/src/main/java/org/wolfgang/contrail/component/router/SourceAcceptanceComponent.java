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

package org.wolfgang.contrail.component.router;

import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolfgang.contrail.reference.DirectReference;

/**
 * <code>NetworkLinkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class SourceAcceptanceComponent extends AbstractComponent implements PipelineComponent<Event, Event, Event, Event> {

	private SourceComponentLink<Event, Event> sourceComponentLink;
	private DestinationComponentLink<Event, Event> destinationComponentLink;
	private UpStreamDataFlow<Event> intermediateUpStreamHandler;

	private RouterComponent networkComponent;

	{
		this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
	}

	/**
	 * Constructor
	 */
	public SourceAcceptanceComponent() {
		super();
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// TODO Auto-generated method stub
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		// TODO Auto-generated method stub
	}

	@Override
	public UpStreamDataFlow<Event> getUpStreamDataFlow() {
		if (intermediateUpStreamHandler == null) {
			intermediateUpStreamHandler = DataFlows.closable(new UpStreamDataFlowAdapter<Event>() {
				@Override
				public void handleData(Event data) throws DataFlowException {
					try {
						// Retrieve the component reference
						final DirectReference senderReference = data.getSender();
						final ComponentLinkManager linkManager = destinationComponentLink.getComponentLinkManager();
						final SourceComponent<Event, Event> source = sourceComponentLink.getSourceComponent();
						final DestinationComponent<Event, Event> destination = destinationComponentLink.getDestinationComponent();

						sourceComponentLink.dispose();
						destinationComponentLink.dispose();

						linkManager.connect(source, destination);

						if (senderReference == null) {
							source.closeDownStream();
						} else {
							networkComponent.filter(source.getComponentId(), senderReference);
						}

						// Re-send the event to the network component
						networkComponent.getDownStreamDataFlow().handleData(data);
					} catch (Throwable e) {
						e.printStackTrace();
						throw new DataFlowException(e);
					}
				}
			});
		}

		return this.intermediateUpStreamHandler;
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.sourceComponentLink);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<Event, Event> handler) throws ComponentConnectionRejectedException {
		final SourceComponent<Event, Event> source = handler.getSourceComponent();
		final ComponentId componentId = source.getComponentId();
		if (this.acceptSource(componentId)) {
			this.sourceComponentLink = handler;
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectSource(componentId);
				}
			};
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException("TODO");
		}
	}

	@Override
	public DownStreamDataFlow<Event> getDownStreamDataFlow() {
		return this.sourceComponentLink.getSourceComponent().getDownStreamDataFlow();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.destinationComponentLink);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<Event, Event> handler) throws ComponentConnectionRejectedException {
		final DestinationComponent<Event, Event> destination = handler.getDestinationComponent();
		final ComponentId componentId = destination.getComponentId();
		if (this.acceptDestination(componentId) && Coercion.canCoerce(destination, RouterComponent.class)) {
			this.destinationComponentLink = handler;
			this.networkComponent = Coercion.coerce(destination, RouterComponent.class);
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectDestination(componentId);
				}
			};
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException(MessagesProvider.message("org.wolfgang.contrail.message", "destination.not.a.router").format());
		}
	}

	private void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		if (!this.acceptDestination(componentId) && destinationComponentLink.getDestinationComponent().getComponentId().equals(componentId)) {
			this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	private void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		if (!acceptSource(componentId) && this.sourceComponentLink.getSourceComponent().getComponentId().equals(componentId)) {
			this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
