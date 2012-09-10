package org.wolfgang.contrail.ecosystem.lang;

import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;

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
	 * @param environment
	 * @return
	 * @throws CannotCreateComponentException
	 */
	T create(Map<String, CodeValue> environment) throws CannotCreateComponentException;
}
