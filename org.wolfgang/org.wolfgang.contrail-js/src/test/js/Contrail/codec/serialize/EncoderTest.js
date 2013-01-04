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
    function (jObj, Factory, QUnit, jCC, Marshaller) {
        "use strict";

        jCC.scenario("String encoding", function () {
            var value, decoder, length, result;

            jCC.
                Given(function () {
                    value = "Hello, World!";
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, value.length * 2 + 1 + Marshaller.sizeOf.ShortNumber, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.String, "Checking encoded type");
                }).
                When(function () {
                    length = Marshaller.bytesToShortNumberWithOffset(result[0], 1);
                }).
                Then(function () {
                    QUnit.equal(length, value.length, "Checking encoded size");
                }).
                And(function () {
                    QUnit.equal(Marshaller.bytesToStringWithOffset(result[0], 1 + Marshaller.sizeOf.ShortNumber, length), value, "Checking first result value");
                });
        });

        jCC.scenario("Number encoding", function () {
            var value, decoder, result;

            jCC.
                Given(function () {
                    value = -123;
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 4 + 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.Number, "Checking encoded type");
                }).
                And(function () {
                    QUnit.equal(Marshaller.bytesToNumberWithOffset(result[0], 1), value, "Checking first result value");
                });
        });

        jCC.scenario("Undefined encoding", function () {
            var value, decoder, result;

            jCC.
                Given(function () {
                    value = undefined;
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.Undefined, "Checking encoded type");
                });
        });

        jCC.scenario("Boolean true encoding", function () {
            var value, decoder, result;

            jCC.
                Given(function () {
                    value = true;
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.BooleanTrue, "Checking encoded type");
                });
        });

        jCC.scenario("Boolean false encoding", function () {
            var value, decoder, result;

            jCC.
                Given(function () {
                    value = false;
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.BooleanFalse, "Checking encoded type");
                });
        });

        jCC.scenario("Empty array encoding", function () {
            var value, decoder, result, length;

            jCC.
                Given(function () {
                    value = [];
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 3, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.Array, "Checking encoded type");
                }).
                When(function () {
                    length = Marshaller.bytesToShortNumberWithOffset(result[0], 1);
                }).
                Then(function () {
                    QUnit.equal(length, 0, "Checking encoded length");
                });
        });

        jCC.scenario("Boolean array encoding", function () {
            var value, decoder, result, length;

            jCC.
                Given(function () {
                    value = [ true ];
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 3 + 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.Array, "Checking encoded type");
                }).
                When(function () {
                    length = Marshaller.bytesToShortNumberWithOffset(result[0], 1);
                }).
                Then(function () {
                    QUnit.equal(length, 1, "Checking encoded length");
                }).
                And(function () {
                    QUnit.equal(result[0][3], Marshaller.types.BooleanTrue, "Checking encoded type for value[0]");
                });
        });

        jCC.scenario("Number array encoding", function () {
            var value, decoder, result, length;

            jCC.
                Given(function () {
                    value = [ 2013 ];
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(result, jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking first result type");
                }).
                And(function () {
                    QUnit.equal(result[0].length, 3 + 5, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.Array, "Checking encoded type");
                }).
                When(function () {
                    length = Marshaller.bytesToShortNumberWithOffset(result[0], 1);
                }).
                Then(function () {
                    QUnit.equal(length, 1, "Checking encoded length");
                }).
                And(function () {
                    QUnit.equal(result[0][3], Marshaller.types.Number, "Checking encoded type for value[0]");
                }).
                And(function () {
                    QUnit.equal(Marshaller.bytesToNumberWithOffset(result[0], 3 + 1), 2013, "Checking encoded type for value[0]");
                });
        });
    });
