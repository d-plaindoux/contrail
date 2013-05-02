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

define([],
    function () {
        "use strict";

        var jType = {}, Primitives;

        function RuntimeTypeError(message) {
            this.message = message;
            this.toString = function () {
                return "RuntimeTypeError: " + this.message;
            };
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
                var result, parent;

                if (type === typeof object) {
                    result = true;
                } else if (type === Primitives.Any) {
                    result = true;
                } else if (jType.getClass(object) === type) {
                    result = true;
                } else if (object && object.extensions) {
                    if (object.extensions.hasOwnProperty(type)) {
                        result = true;
                    } else {
                        for (parent in object.extensions) {
                            if (object.extensions.hasOwnProperty(parent) && !result) {
                                if (ofPrimitiveType(type).check(object.extensions[parent])) {
                                    result = true;
                                }
                            }
                        }
                    }
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
            Float:"float",
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
            Null:typeRule("null", function (object) {
                return object === null;
            }),

            Float:typeRule(Primitives.Float,
                function (object) {
                    var result = false;

                    if (jType.ofType(object, jType.types.Number)) {
                        result = Math.round(object) !== object;
                    }

                    return result;
                }),

            Named:ofPrimitiveType,

            // Root type
            Any:typeRule(Primitives.Any, function (object) {
                return true;
            }),

            // Function type only
            Function:ofPrimitiveType(Primitives.Function),

            // Complex function type
            FunctionOf:function (parameters, result) {
                return typeRule(Primitives.Function,
                    function (object) {
                        var result = true;

                        // parameter type checking TODO in this section

                        return result;
                    });
            },
            // Object type only - An array is not an object in this type system
            Object:typeRule(Primitives.Object, function (object) {
                return object && typeof object === "object" && !jType.ofType(object, jType.types.Array);
            }),

            // Complex object type (Structural sub-typing)
            ObjectOf:function (objectType) {
                return typeRule(Primitives.Object,
                    function (object) {
                        var entry, result = false;

                        if (jType.ofType(object, jType.types.Object)) {
                            result = true;

                            for (entry in objectType) {
                                if (objectType.hasOwnProperty(entry)) {
                                    result = result && jType.ofType(object[entry], objectType[entry]);
                                }
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

                    if (jType.ofType(object, type1)) {
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

                    if (jType.ofType(object, type1)) {
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
                return jType.types.Or(jType.types.Or(jType.types.Null, jType.types.Undefined), type);
            }
        };

        /**
         * Method checking the object nature ...
         *
         * @param object
         * @return {Boolean}
         */
        jType.isAClassInstance = function (object) {
            return object && object.hasOwnProperty("extensions");
        };

        /**
         * Method called whether the class name must be retrieved
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
            }

            return object;
        };

        return jType;
    });
