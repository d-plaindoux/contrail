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

define([ "require" ],
    function (require) {
        "use strict";

        var jType = {}, Primitives;

        function RuntimeTypeError(message) {
            require("Core/jObj").bless(this);
            this.message = message;
        }

        function typeRule(type, fun) {
            return {typingRule:true, type:type, check:fun};
        }

        /**
         * Method called to check if a given object has a given type
         *
         * @param type
         * @return Function
         */
        function ofPrimitiveType(type) {
            return typeRule(type, function (object) {
                var result;

                if (typeof object === type) {
                    result = true;
                } else if (type === Primitives.Any) {
                    result = true;
                } else if (jType.getClass(object) === type) {
                    result = true;
                } else if (object && object.inherit && object.inherit.hasOwnProperty(type)) {
                    result = true;
                } else {
                    result = false;
                }

                return result;
            });
        }

        /**
         * Type primitives
         */
        Primitives = {
            Any:"Any",
            Array:"Array",
            Object:"Object",
            Number:"number",
            String:"string",
            Boolean:"boolean",
            Undefined:"undefined",
            Function:"function"
        };

        /**
         * Type definitions
         */
        jType.types = {
            Array:ofPrimitiveType(Primitives.Array),
            Number:ofPrimitiveType(Primitives.Number),
            String:ofPrimitiveType(Primitives.String),
            Boolean:ofPrimitiveType(Primitives.Boolean),
            Undefined:ofPrimitiveType(Primitives.Undefined),
            Function:ofPrimitiveType(Primitives.Function),

            Named:ofPrimitiveType,

            // Root type
            Any:typeRule(Primitives.Any, function (object) {
                return true;
            }),

            // Object type only
            Object:typeRule(Primitives.Object, function (object) {
                return typeof object === "object";
            }),

            // Complex object type (Structural sub-typing)
            ObjectOf:function (objectType) {
                return typeRule(Primitives.Object,
                    function (object) {
                        var entry, result = true;

                        for (entry in objectType) {
                            if (objectType.hasOwnProperty(entry)) {
                                result = result && jType.ofType(object[entry], objectType[entry]);
                            }
                        }

                        return result;
                    });
            },

            // Complex object type (Array generic type)
            ArrayOf:function (type) {
                return typeRule(Primitives.Array,
                    function (object) {
                        var result = true;

                        object.forEach(function (value) {
                            result = result && jType.ofType(value, type);
                        });

                        return result;
                    });
            },

            // Disjunction
            Or:function (type1, type2) {
                return typeRule("Or", function (object) {
                    var result;

                    if (jType.ofType(object, type2)) {
                        result = true;
                    } else {
                        result = jType.ofType(object, type2);
                    }

                    return result;
                });
            },

            // Conjunction
            And:function (type1, type2) {
                return typeRule("And", function (object) {
                    var result;

                    if (jType.ofType(object, type2)) {
                        result = jType.ofType(object, type2);
                    } else {
                        result = false;
                    }

                    return result;
                });
            },

            // Negation
            Not:function (type) {
                return typeRule("And", function (object) {
                    return !jType.ofType(object, type);
                });
            },

            // Nullable
            Nullable:function (type) {
                return jType.types.Or(jType.types.Undefined, type);
            },

            VarArgs:function (type) {
                return jType.types.Or(jType.types.Undefined, type);
            }
        };

        /**
         * Method called whether the class name nust be retrieved
         *
         * @param object The object
         * @return the type if it's an object; undefined otherwise
         */
        jType.getClass = function (object) {
            var arr;

            if (object) {
                if (object.constructor && object.constructor.toString) {
                    arr = object.constructor.toString().match(/function\s*(\w+)/);
                    if (arr && arr.length === 2) {
                        return arr[1];
                    }
                }

                if (Object.prototype.toString.apply(object)) {
                    arr = Object.prototype.toString.apply(object).match(/\[object\s*(\w+)\]/);
                    if (arr && arr.length === 2) {
                        return arr[1];
                    }
                }
            }


            return undefined;
        };

        /**
         * Method called to check if a given object has a given type
         *
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jType.getTypeName = function (type) {
            var result;

            if (type && type.typingRule) {
                result = type.type;
            } else {
                result = type;
            }

            return result;
        };

        /**
         * Method called to check if a given object has a given type
         *
         * @param object
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jType.ofType = function (object, type) {
            var result;

            if (type && type.typingRule) {
                result = type.check(object);
            } else {
                result = ofPrimitiveType(type).check(object);
            }

            return result;
        };

        /**
         * Method called to check if a given object has a given type set
         *
         * @param object
         * @param types
         * @return true if the object is a type of types; false otherwise
         */
        jType.ofTypes = function (object, types) {
            var instance = types.length > 0;

            types.forEach(function (type) {
                instance = instance && jType.ofType(object, type);
            });

            return instance;
        };

        /**
         * Method dedicated to dynamic type checking
         *
         * @param object
         * @param type
         * @return {*}
         */
        jType.checkType = function (object, type) {
            if (!jType.ofType(object, type)) {
                throw new RuntimeTypeError(object + " must be an instance of " + jType.getTypeName(type));
            } else {
                return object;
            }
        };

        return jType;
    });
