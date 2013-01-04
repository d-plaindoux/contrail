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

require([ "Core/object/jObj", "Contrail/codec/jCodec", "qunit", "test/jCC", "Core/io/jMarshaller"],
    function (jObj, Factory, QUnit, jCC, jMarshaller) {
        "use strict";

        jCC.scenario("String decoding", function () {
            var object, encoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.String ].
                        concat(jMarshaller.numberToBytes(message.length)).
                        concat(jMarshaller.stringToBytes(message));
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
                    QUnit.equal(result[0], message, "Checking encoded value");
                });
        });

        jCC.scenario("Number decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Number ].concat(jMarshaller.numberToBytes(-256));
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
                    QUnit.equal(result[0], -256, "Checking encoded value");
                });
        });

        jCC.scenario("Undefined decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Undefined ];
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
                    QUnit.equal(result[0], undefined, "Checking encoded value");
                });
        });

        jCC.scenario("Boolean true decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.BooleanTrue ];
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
                    QUnit.equal(result[0], true, "Checking encoded value");
                });
        });

        jCC.scenario("Boolean false decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.BooleanFalse ];
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
                    QUnit.equal(result[0], false, "Checking encoded value");
                });
        });


        jCC.scenario("Empty array decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Array, 0, 0, 0, 0 ];
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
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 0, "Checking encoding length");
                });
        });

        jCC.scenario("Boolean array decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Array, 0, 0, 0, 1, jMarshaller.types.BooleanTrue ];
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
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 1, "Checking encoding length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], true, "Checking encoded value[0]");
                });
        });

        jCC.scenario("String array decoding", function () {
            var object, encoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Array, 0, 0, 0, 1, jMarshaller.types.String, 0, 0, 0, message.length ].concat(jMarshaller.stringToBytes(message));
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
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 1, "Checking encoding length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], message, "Checking encoded value[0]");
                });
        });

        jCC.scenario("Booleans array decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Array, 0, 0, 0, 2, jMarshaller.types.BooleanTrue, jMarshaller.types.BooleanFalse ];
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
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], true, "Checking encoded value[0]");
                }).
                And(function () {
                    QUnit.equal(result[0][1], false, "Checking encoded value[1]");
                });
        });

        jCC.scenario("Mixed Boolean and Number array decoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = [ jMarshaller.types.Array, 0, 0, 0, 2, jMarshaller.types.Number, 0, 0, 0, 128, jMarshaller.types.BooleanFalse ];
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
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], 128, "Checking encoded value[0]");
                }).
                And(function () {
                    QUnit.equal(result[0][1], false, "Checking encoded value[1]");
                });
        });
    });

