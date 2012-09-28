package org.wolfgang.contrail.ecosystem.lang;

import org.wolfgang.contrail.component.CannotCreateComponentException;

/**
 * <code>ImportEntry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface EcosystemImportation<T> {

	/**
	 * Hook to invoke underlying factories
	 * 
	 * @param symbolTable
	 * @return
	 * @throws CannotCreateComponentException
	 */
	T create(EcosystemSymbolTable symbolTable) throws CannotCreateComponentException;
}
