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

package org.wolfgang.contrail.ecosystem.lang.delta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;
import org.wolfgang.contrail.ecosystem.lang.EcosystemFactoryImpl;
import org.wolfgang.contrail.ecosystem.lang.EcosystemSymbolTableImpl;
import org.wolfgang.contrail.ecosystem.lang.MethodImportation;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.model.ModelFactory;

/**
 * <code>ReverseFunction</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Bootstrap {

	private final Map<String, Object> importations;
	private final EcosystemSymbolTableImpl symbolTable;
	private final EcosystemFactoryImpl factoryImpl;

	{
		this.importations = new HashMap<String, Object>();
		this.symbolTable = new EcosystemSymbolTableImpl();
	}

	public Bootstrap(EcosystemFactoryImpl factoryImpl) {
		super();
		this.factoryImpl = factoryImpl;
	}

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public CodeValue extern(@ContrailArgument("package") String packageName, @ContrailArgument("name") String methodName, @ContrailArgument("arity") int arity) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

		final String nativeName = packageName + "_" + methodName + "_" + arity;

		if (!symbolTable.hasDefinition(nativeName)) {
			if (!importations.containsKey(packageName)) {
				final Object object = Class.forName(packageName).getConstructor(ContextFactory.class).newInstance(factoryImpl);
				this.importations.put(packageName, object);
			}

			final Object component = this.importations.get(packageName);
			final Method[] declaredMethods = LibraryBuilder.getDeclaredMethods(methodName, component.getClass());

			for (Method method : declaredMethods) {
				if (method.getParameterTypes().length == arity) {
					symbolTable.putImportation(nativeName, new MethodImportation(factoryImpl, component, method));
					break;
				}
			}

			final String[] names = new String[arity];
			for (int i = 0; i < arity; i++) {
				names[i] = "$_" + i;
			}

			final ClosureValue closureValue = new ClosureValue(this.factoryImpl, symbolTable, ModelFactory.function(ModelFactory.reference(nativeName), names));
			this.symbolTable.putDefinition(nativeName, closureValue);
		}

		return symbolTable.getDefinition(nativeName);
	}
}
