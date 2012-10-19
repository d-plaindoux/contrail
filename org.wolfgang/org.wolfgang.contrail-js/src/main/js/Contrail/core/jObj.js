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

/*global define*/

define( [ "jquery" ], 
function ($) {

	var jObj = {};
    
	/**
	 * class exception used to notify any type error
	 */
	function TypeError () {
		this.message = "the object has not the right type";
	}
    
	/**
	 * Method called whether the class name nust be retrieved
	 * 
	 * @param object The object
	 * @return the type if it's an object; undefined otherwise
	 */
	jObj.getClass = function (object) {
		if (object && object.constructor && object.constructor.toString) {
			var arr = object.constructor.toString().match(/function\s*(\w+)/);
			if (arr && arr.length === 2) {
				return arr[1];
			}
		}

		return undefined;        
	};

	/**
	 * Method called whether an object must be extended and blessed as an instance
	 * of the extended class model.
	 * 
	 * @param arguments[0] The object to be blessed
	 * @param arguments[1..] The extensions
	 */
	jObj.bless = function (/*arguments*/) {
		var i, key;
        
		if (arguments.length > 0) {
			$.extend.apply($, arguments);            

			//
			// Special traitments for inherit
			//

			arguments[0].inherit = {};
                        
			for(i = 1; i < arguments.length; i++) {
				for(key in arguments[i].inherit) {
					arguments[0].inherit[key] = true;
				}
				arguments[0].inherit[jObj.getClass(arguments[i])] = true;
			}
		} 
	};
    
	/**
	 * Method called to check if a given object has a given type
	 * 
	 * @param object
	 * @param type
	 * @return true if the object is a type of type; false otherwise
	 */
	jObj.instanceOf = function (object, type) {
		if (jObj.getClass(object) === type) {
			return true;
		} else if (object.inherit && object.inherit.hasOwnProperty(type)) {
			return true;
		} else {
			return false;
		}
	};
        
	/**
	* Return an object transformation
	* 
	* @param object The object
	* @return the transformation
			*/
	jObj.transform = function (object, driver) {
		if (typeof object === "object") {
			var key, content = driver.enterObject(jObj.getClass(object));

			for(key in object) {
				driver.visitAttribute(content, key, jObj.transform(object[key], driver));
			}

			return driver.exitObject(content);
		} else {
			return driver.visiteNative(object);
		}
	};
    
	/**
	 * Return the string representation
	 * 
	 * @param object The object
	 * @return the type
	 */
	jObj.toString = function (object) {
		var driverToString = {
			enterObject : function (object) {
				return "{";
			},
			visitAttribute : function (content, key, value) {
				return content + " " + key + ":" + value + ";"; 
			},
			exitObject : function (content) {                
				return content + " }";
			},
			visitNative : function (value) {
				return value;
			}
		};
        
		return jObj.transform(object, driverToString);
	};
    
	/**
	 * Return the type of the parameter
	 * 
	 * @param object The object
	 * @return the type
	 */
	jObj.getType = function (object) {
		var driverToType = {
			enterObject : function (object) {
				return {};
			},
			visitAttribute : function (content, key, value) {
				content[key] = value; 
			},
			exitObject : function (content) {
				return this.content;
			},
			visitNative : function (value) {
				return typeof value;
			}
		};

		return jObj.transform(object,driverToType);
	};
    
	return jObj;
});
