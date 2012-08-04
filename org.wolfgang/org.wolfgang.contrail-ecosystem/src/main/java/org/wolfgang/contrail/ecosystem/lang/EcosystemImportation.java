package org.wolfgang.contrail.ecosystem.lang;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;

/**
 * <code>ImportEntry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface EcosystemImportation<T extends Component> {

	/**
	 * Hook to invoke underlying factories
	 * 
	 * @param parameters
	 * @return
	 * @throws CannotCreateComponentException
	 */
	T create(String... parameters) throws CannotCreateComponentException;
}
