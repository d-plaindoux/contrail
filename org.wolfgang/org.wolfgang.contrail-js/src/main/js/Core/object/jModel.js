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

define([ "./jType" ],
    function (jType) {
        "use strict";

        var jModel = {};

        jModel.specificationIsEnable = true;

        jModel.enableSpecification = function (value) {
            jModel.specificationIsEnable = value;
        };

        /**
         * Facility used when an exception must be created
         *
         * @param message
         * @param cause
         * @return an exception
         */
        jModel.exception = function (message, cause) {
            return {
                message:message,
                cause:cause,
                toString:function () {
                    return "RuntimeException: " + message;
                }
            };
        };

        /**
         * Facility used to raise an error
         *
         * @param error
         */
        jModel.raise = function (error) {
            throw error;
        };

        /**
         * Method called whether a given exception must be thrown
         * @param e
         */
        jModel.throwError = function (e) {
            throw e;
        };

        /**
         * Method called whether a construction must be defined
         *
         * @param profil
         * @param constructor
         * @return a constructor function
         */
        jModel.constructor = function (profil, constructor) {
            var result;

            if (!jModel.specificationIsEnable) {
                result = constructor;
            } else {
                result = function () {
                    if (arguments.length > profil.length) {
                        throw jModel.exception("L.profil.arguments.length.error", { method:constructor, expect:profil.length, actual:arguments.length});
                    } else {
                        var index;

                        for (index = 0; index < arguments.length; index += 1) {
                            jType.checkType(arguments[index], profil[index]);
                        }

                        for (index = arguments.length; index < profil.length; index += 1) {
                            jType.checkType(undefined, profil[index]);
                        }

                        return constructor.apply(this, arguments);
                    }
                };
            }

            return result;
        };


        /**
         * Method called whether a procedure must be defined
         *
         * @param profil
         * @param method
         * @return a prodecure
         */
        jModel.procedure = function (profil, method) {
            return jModel.method(profil, undefined, method);
        };

        jModel.abstractDefinition = function () {
            throw jModel.exception("L.abstract.method", { arity:arguments.length});
        };

        /**
         * Method called whether a method must be defined
         *
         * @param profil
         * @param returns
         * @param method
         * @return a method
         */
        jModel.method = function (profil, returns, method) {
            var result;

            if (!jModel.specificationIsEnable) {
                result = method || function () {
                    throw jModel.exception("L.abstract.method", { method:method, arity:arguments.length});
                };
            } else {
                result = function () {
                    if (arguments.length > profil.length) {
                        throw jModel.exception("L.profile.arguments.length.error", { method:method, expect:profil.length, actual:arguments.length});
                    } else {
                        var index;

                        for (index = 0; index < arguments.length; index += 1) {
                            jType.checkType(arguments[index], profil[index]);
                        }

                        for (index = arguments.length; index < profil.length; index += 1) {
                            jType.checkType(undefined, profil[index]);
                        }

                        if (method === undefined) {
                            jModel.abstractDefinition.apply(this, arguments);
                        } else {
                            return jType.checkType(method.apply(this, arguments), returns);
                        }
                    }
                };
            }

            return result;
        };

        return jModel;

    });
