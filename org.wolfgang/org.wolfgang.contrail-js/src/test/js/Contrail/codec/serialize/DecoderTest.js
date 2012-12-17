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

/*global require */

require([ "Core/jObj", "Contrail/codec", "qunit", "test/jCC", "IO/jMarshaller"],
    function (jObj, Factory, QUnit, jCC, Marshaller) {
        "use strict";

        jCC.scenario("String decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ Marshaller.types.String ].concat(Marshaller.stringToBytes("Hello, World!"));
                }).
                And(function () {
                    encoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.String), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0], "Hello, World!", "Checking encoding length");
                });
        });

        jCC.scenario("Number decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ Marshaller.types.Number ].concat(Marshaller.numberToBytes(-256));
                }).
                And(function () {
                    encoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Number), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0], -256, "Checking encoding length");
                });
        });

        jCC.scenario("Undefined decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ Marshaller.types.Undefined ];
                }).
                And(function () {
                    encoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Undefined), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0], undefined, "Checking encoding length");
                });
        });

        jCC.scenario("Boolean true decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ Marshaller.types.BooleanTrue ];
                }).
                And(function () {
                    encoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Boolean), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0], true, "Checking encoding length");
                });
        });

        jCC.scenario("Boolean false decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ Marshaller.types.BooleanFalse ];
                }).
                And(function () {
                    encoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Boolean), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0], false, "Checking encoding length");
                });
        });
    });

