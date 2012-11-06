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

define("Core/jObj", [ "require", "jquery", "Utils/jStrict" ],
    function (require, jQuery, jStrict) {

        var jObj = {};

        jObj.modelisation = true;

        jObj.activateModelisation = function (value) {
            jObj.modelisation = value;
        };

        /**
         * Type definitions
         */
        jObj.types = {
            Any:"Any",
            Array:"Array",
            Object:"Object",
            Number:"number",
            String:"string",
            Boolean:"boolean",
            Undefined:"undefined"
        };

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
                // Extension and supers
                for (i = 1; i < arguments.length; i++) {
                    jQuery.extend(arguments[0], arguments[i]);
                }

                // Inheritance
                arguments[0].inherit = {};

                for (i = 1; i < arguments.length; i++) {
                    for (key in arguments[i].inherit) {
                        arguments[0].inherit[key] = true;
                    }
                    arguments[0].inherit[jObj.getClass(arguments[i])] = true;
                }
            }
        };

        /**
         * Check if the parameter is an object with a given type
         * @param object
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jObj.isObjectType = function (object, type) {
            return Object.prototype.toString.apply(object) === '[object ' + type + ']';
        };

        /**
         * Method called to check if a given object has a given type
         *
         * @param object
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jObj.instanceOf = function (object, type) {
            if (typeof object === type) {
                return true;
            } else if (jObj.isObjectType(object, type)) {
                return true;
            } else if (jObj.getClass(object) === type) {
                return true;
            } else if (object && object.inherit && object.inherit.hasOwnProperty(type)) {
                return true;
            } else if (type === jObj.types.Any) {
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

                for (key in object) {
                    content = driver.visitAttribute(content, key, jObj.transform(object[key], driver));
                }

                return driver.exitObject(content);
            } else {
                return driver.visitNative(object);
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
                enterObject:function (object) {
                    return "{";
                },
                visitAttribute:function (content, key, value) {
                    return content + " " + key + ":" + value + ";\n";
                },
                exitObject:function (content) {
                    return content + " }\n";
                },
                visitNative:function (value) {
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
        jObj.toType = function (object) {
            var driverToType = {
                enterObject:function (object) {
                    return {};
                },
                visitAttribute:function (content, key, value) {
                    content[key] = value;
                    return content;
                },
                exitObject:function (content) {
                    return content;
                },
                visitNative:function (value) {
                    return typeof value;
                }
            };

            return jObj.transform(object, driverToType);
        };

        /**
         * Facility used when an exception must be created
         *
         * @param message
         * @param cause
         * @return an exception
         */
        jObj.exception = function (message, cause) {
            throw { message:message, cause:cause };
        };

        /**
         * Method called whether a construction must be defined
         *
         * @param profil
         * @param constructor
         * @return a constructor function
         */
        jObj.constructor = function (profil, constructor) {
            if (!jObj.modelisation) {
                return constructor;
            } else {
                return function () {
                    if (arguments.length !== profil.length) {
                        throw jObj.exception("L.profil.error");
                    } else {
                        var index;

                        for (index = 0; index < arguments.length; index++) {
                            jStrict.assertType(arguments[index], profil[index]);
                        }

                        return constructor.apply(this, arguments);
                    }
                };
            }
        };


        /**
         * Method called whether a procedure must be defined
         *
         * @param profil
         * @param method
         * @return a prodecure
         */
        jObj.procedure = function (profil, method) {
            return jObj.method(profil, undefined, method);
        };


        /**
         * Method called whether a method must be defined
         *
         * @param profil
         * @param returns
         * @param method
         * @return a method
         */
        jObj.method = function (profil, returns, method) {
            if (method === undefined) {
                return undefined;
            } else if (!jObj.modelisation) {
                return method;
            } else {
                return function () {
                    if (arguments.length !== profil.length) {
                        throw jObj.exception("L.profil.error");
                    } else {
                        var index, result;

                        for (index = 0; index < arguments.length; index++) {
                            jStrict.assertType(arguments[index], profil[index]);
                        }

                        result = method.apply(this, arguments);

                        return jStrict.assertType(result, returns);
                    }
                };
            }
        };

        /**
         * Facility used to determine a value using a default one when
         * the parametric one is undefined.
         *
         * @param v the value
         * @param d the default
         * @return v if not undefined; d otherwise
         */
        jObj.value = function (v, d) {
            if (v === undefined) {
                return d;
            } else {
                return v;
            }
        };

        return jObj;

    });
