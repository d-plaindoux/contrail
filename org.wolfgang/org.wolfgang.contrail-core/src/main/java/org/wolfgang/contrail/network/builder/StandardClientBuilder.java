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

import java.util.Arrays;
import java.util.List;

import org.wolfgang.actor.event.Request;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.factory.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.ObjectTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.SerializationTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.StringifyTransducerFactory;
import org.wolfgang.contrail.data.JSonifier;
import org.wolfgang.network.packet.Packet;

public abstract class StandardClientBuilder extends ClientBuilder {

	private final List<JSonifier> jSonifiers;

	public StandardClientBuilder(String endPoint) {
		super(endPoint);

		this.jSonifiers = Arrays.asList(Packet.jSonifable(), Request.jSonifable());
	}

	@SuppressWarnings("unchecked")
	protected PipelineComponent<String, String, Packet, Packet> getIntermediateComponent() throws ComponentConnectionRejectedException {

		final PipelineComponent<String, String, Bytes, Bytes> stringify = new StringifyTransducerFactory().createComponent();
		final PipelineComponent<Bytes, Bytes, Object, Object> serialize = new SerializationTransducerFactory().createComponent();
		final PipelineComponent<Object, Object, Object, Object> objectify = new ObjectTransducerFactory(jSonifiers).createComponent();
		final PipelineComponent<Object, Object, Packet, Packet> coercion = new CoercionTransducerFactory<Packet>(Packet.class).createComponent();

		return (PipelineComponent<String, String, Packet, Packet>) Components.compose(stringify, serialize, objectify, coercion);
	}
}