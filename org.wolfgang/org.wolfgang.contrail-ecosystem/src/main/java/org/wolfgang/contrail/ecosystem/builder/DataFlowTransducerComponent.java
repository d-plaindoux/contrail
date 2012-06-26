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

package org.wolfgang.contrail.ecosystem.builder;

import static org.wolfgang.common.message.MessagesProvider.message;
import static org.wolfgang.contrail.codec.CodecFactory.Loader.load;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.codec.CodecFactory;
import org.wolfgang.contrail.codec.CodecFactoryCreationException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.transducer.TransducerComponent;

/**
 * <code>DataFlowTransducerComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DataFlowTransducerComponent implements DataFlowComponent {

	/**
	 * The factory
	 */
	private final String factory;

	/**
	 * The parameters used for the coding decoding mechanisms creation
	 */
	private final String[] parameters;

	/**
	 * The input name
	 */
	private final String input;

	/**
	 * The output name
	 */
	private final String output;

	/**
	 * Constructor
	 * 
	 * @param factory
	 * @param parameters
	 * @param input
	 * @param output
	 */
	public DataFlowTransducerComponent(String factory, String[] parameters, String input, String output) {
		super();
		this.factory = factory;
		this.parameters = parameters;
		this.input = input;
		this.output = output;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TransducerComponent getComponent(ClassLoader loader, String[] imports) throws DataFlowComponentCreationException {
		for (String anImport : imports) {
			final CodecFactory codec;
			try {
				codec = load(loader, anImport + this.factory, this.parameters.clone());
			} catch (CodecFactoryCreationException e) {
				throw new DataFlowComponentCreationException(e);
			}
			return new TransducerComponent(codec.getDecoder(), codec.getEncoder());
		}

		final Message message = message("org.wolgang.contrail.message", "dataflow.factory.not.found");
		throw new DataFlowComponentCreationException(message.format(this.factory));
	}

	@Override
	public String[] getInputs() {
		return new String[] { input };
	}

	@Override
	public String[] getOutputs() {
		return new String[] { output };
	}
}
