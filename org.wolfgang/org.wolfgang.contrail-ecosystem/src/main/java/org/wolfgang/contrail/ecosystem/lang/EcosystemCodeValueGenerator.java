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

package org.wolfgang.contrail.ecosystem.lang;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.router.OnLinkAcceptanceComponent;
import org.wolfgang.contrail.component.router.RouterComponent;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;
import org.wolfgang.contrail.ecosystem.lang.delta.RouterComponentFactory;
import org.wolfgang.contrail.ecosystem.lang.delta.SwitchComponentFactory;
import org.wolfgang.contrail.ecosystem.lang.model.Apply;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.Case;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.ExpressionVisitor;
import org.wolfgang.contrail.ecosystem.lang.model.Flow;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.contrail.ecosystem.lang.model.Reference;
import org.wolfgang.contrail.ecosystem.lang.model.Router;
import org.wolfgang.contrail.ecosystem.lang.model.Switch;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>Interpret</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EcosystemCodeValueGenerator implements ExpressionVisitor<CodeValue, EcosystemCodeValueGeneratorException> {

	/**
	 * The related synbol table
	 */
	private final EcosystemSymbolTable symbolTable;

	/**
	 * The environment
	 */
	private final Map<String, CodeValue> environment;

	/**
	 * Constructor
	 * 
	 * @param environment
	 */
	EcosystemCodeValueGenerator(EcosystemSymbolTable factory, Map<String, CodeValue> environment) {
		super();
		this.symbolTable = factory;
		this.environment = environment;
	}

	/**
	 * @param environment
	 * @return
	 */
	public EcosystemCodeValueGenerator create(Map<String, CodeValue> environment) {
		final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
		newEnvironment.putAll(environment);
		return new EcosystemCodeValueGenerator(symbolTable, newEnvironment);
	}

	/**
	 * @param expressions
	 * @return
	 * @throws EcosystemCodeValueGeneratorException
	 */
	public CodeValue visit(final List<Expression> expressions) throws EcosystemCodeValueGeneratorException {
		final CodeValue[] values = new CodeValue[expressions.size()];
		final EcosystemCodeValueGenerator interpret = new EcosystemCodeValueGenerator(symbolTable, environment);

		for (int i = 0; i < values.length; i++) {
			values[i] = expressions.get(i).visit(interpret);
		}

		if (values.length == 1) {
			return values[0];
		} else {
			return new FlowValue(values);
		}
	}

	@Override
	public CodeValue visit(final Reference expression) throws EcosystemCodeValueGeneratorException {
		final String name = expression.getValue();

		if (symbolTable.hasImportation(name)) {
			return new ComponentValue(environment, symbolTable.getImportation(name));
		} else if (environment.containsKey(name)) {
			return environment.get(name);
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "definition.not.found");
			throw new EcosystemCodeValueGeneratorException(message.format(name));
		}
	}

	@Override
	public CodeValue visit(Atom expression) throws EcosystemCodeValueGeneratorException {
		return new ConstantValue(expression.getValue());
	}

	@Override
	public CodeValue visit(Apply expression) throws EcosystemCodeValueGeneratorException {
		final CodeValue interpreted = expression.getFunction().visit(this);

		if (interpreted instanceof ClosureValue) {
			final ClosureValue closure = (ClosureValue) interpreted;
			return closure.apply(expression.getBinding(), expression.getParameter().visit(this));
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "function.required");
			throw new EcosystemCodeValueGeneratorException(message.format());
		}
	}

	@Override
	public CodeValue visit(Flow expression) throws EcosystemCodeValueGeneratorException {
		return this.visit(expression.getExpressions());
	}

	@Override
	public CodeValue visit(Function expression) throws EcosystemCodeValueGeneratorException {
		final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
		newEnvironment.putAll(environment);
		return new ClosureValue(this, expression, newEnvironment);
	}

	@Override
	public CodeValue visit(Router expression) throws EcosystemCodeValueGeneratorException {
		/*
		try {
			final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased(expression.getSelf()));
			final RouterComponent routerComponent = RouterComponentFactory.create(reference);
			final ConstantValue constantRouter = new ConstantValue(routerComponent);
			final List<Case> cases = expression.getCases();

			for (Case aCase : cases) {
				final List<String> filters = aCase.getFilters();
				final List<DirectReference> references = new ArrayList<DirectReference>();
				for (String filter : filters) {
					references.add(ReferenceFactory.directReference(UUIDUtils.digestBased(filter)));
				}

				final DirectReference primary = references.remove(0);
				final DirectReference[] secundaries = references.toArray(new DirectReference[references.size()]);
				final CodeValue visit = this.visit(aCase.getBody());

				final RouterSourceTable.Entry entry = new RouterSourceTable.Entry() {
					@Override
					public SourceComponent<Event, Event> create() throws CannotCreateComponentException {
						final OnLinkAcceptanceComponent onLinkAcceptanceComponent = new OnLinkAcceptanceComponent(primary);
						try {
							final ConstantValue constantValue1 = new ConstantValue(onLinkAcceptanceComponent);
							final FlowValue flowValue = new FlowValue(constantValue1, constantRouter);
							visit.apply(flowValue);
							return onLinkAcceptanceComponent;
						} catch (EcosystemCodeValueGeneratorException e) {
							throw new CannotCreateComponentException(e);
						}
					}
				};
				
				routerComponent.getRouterSourceTable().insert(entry, primary, secundaries);
			}


			if (expression.getDefaultCase() != null) {
				// TODO
			}

			return constantRouter;
		} catch (NoSuchAlgorithmException e) {
			throw new EcosystemCodeValueGeneratorException(e);
		} catch (ReferenceEntryAlreadyExistException e) {
			throw new EcosystemCodeValueGeneratorException(e);
		}
		*/
		
		return null;
	}

	@Override
	public CodeValue visit(Switch expression) throws EcosystemCodeValueGeneratorException {
		return null;
	}
}