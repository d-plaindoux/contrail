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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.crypto.NoSuchMechanismException;

import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
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
	private final EcosystemFactoryImpl factoryImpl;
	private final EcosystemSymbolTableImpl symbolTable;

	{
		this.importations = new HashMap<String, Object>();
	}

	/**
	 * Constructor
	 * 
	 * @param factoryImpl
	 * @param symbolTableImpl
	 */
	public Bootstrap(EcosystemFactoryImpl factoryImpl, EcosystemSymbolTableImpl symbolTable) {
		super();
		this.factoryImpl = factoryImpl;
		this.symbolTable = symbolTable;
	}

	/**
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object getComponent(String packageName) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		if (!importations.containsKey(packageName)) {
			final Class<?> forName = Class.forName(packageName);
			Constructor<?> constructor;
			try {
				constructor = forName.getConstructor(ContextFactory.class);
			} catch (SecurityException e) {
				constructor = forName.getConstructor();
			} catch (NoSuchMethodException e) {
				constructor = forName.getConstructor();
			}

			Object object = constructor.newInstance(factoryImpl);
			this.importations.put(packageName, object);
		}

		return this.importations.get(packageName);
	}

	/**
	 * 
	 * @param nativeName
	 * @param component
	 * @param methodName
	 * @param arity
	 */
	@SuppressWarnings("rawtypes")
	private void findAndRegisterMethod(String nativeName, Object component, String methodName, int arity) {
		final Method[] declaredMethods = LibraryBuilder.getDeclaredMethods(methodName, component.getClass());

		for (Method method : declaredMethods) {
			if (method.getParameterTypes().length == arity) {
				final String[] names = LibraryBuilder.getParametersName(method);
				final ClosureValue closureValue = new ClosureValue(this.factoryImpl, this.symbolTable, ModelFactory.function(ModelFactory.reference(nativeName), names));

				this.symbolTable.putImportation(nativeName, new MethodImportation(factoryImpl, component, method));
				this.symbolTable.putDefinition(nativeName, closureValue);

				return;
			}
		}

		throw new NoSuchMechanismException(nativeName);
	}

	/**
	 * External method able to mount other external methods: the external
	 * bootstrap
	 * 
	 * @param packageName
	 * @param methodName
	 * @param arity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	public CodeValue external(@ContrailArgument("object") String packageName, @ContrailArgument("method") String methodName, @ContrailArgument("arity") Integer arity) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

		final String nativeName = packageName + "#" + methodName + "/" + arity;

		if (!symbolTable.hasDefinition(nativeName)) {
			this.findAndRegisterMethod(nativeName, this.getComponent(packageName), methodName, arity);
		}

		return symbolTable.getDefinition(nativeName);
	}
}
