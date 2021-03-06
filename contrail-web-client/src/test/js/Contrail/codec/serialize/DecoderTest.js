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

require(["Core/object/jObj", "Contrail/codec/jCodec", "Core/test/jCC", "Core/io/jMarshaller"],
    function (jObj, Factory, jCC, jMarshaller) {
        "use strict";

        var epsilon = 0.01;

        jCC.scenario("String decoding", function () {
            var object, decoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.String].concat(jMarshaller.shortNumberToBytes(message.length)).concat(jMarshaller.stringToBytes(message));
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.String), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0], message, "Checking encoded value");
                });
        });

        jCC.scenario("Number decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Number].concat(jMarshaller.numberToBytes(-256));
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Number), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0], -256, "Checking encoded value");
                });
        });


        jCC.scenario("Float decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Float].concat(jMarshaller.floatToBytes(256.23));
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Float), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(true, result[0] - 256.23 < epsilon, "Checking encoded value");
                });
        });

        jCC.scenario("Undefined decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Undefined];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Undefined), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0], undefined, "Checking encoded value");
                });
        });

        jCC.scenario("Boolean true decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.BooleanTrue];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Boolean), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0], true, "Checking encoded value");
                });
        });

        jCC.scenario("Boolean false decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.BooleanFalse];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Boolean), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0], false, "Checking encoded value");
                });
        });


        jCC.scenario("Empty array decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 0];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 0, "Checking encoding length");
                });
        });

        jCC.scenario("Boolean array decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 1, jMarshaller.types.BooleanTrue];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 1, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], true, "Checking encoded value[0]");
                });
        });

        jCC.scenario("String array decoding", function () {
            var object, decoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 1].
                        concat([jMarshaller.types.String]).concat(jMarshaller.shortNumberToBytes(message.length)).concat(jMarshaller.stringToBytes(message));

                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 1, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], message, "Checking encoded value[0]");
                });
        });

        jCC.scenario("Booleans array decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 2, jMarshaller.types.BooleanTrue, jMarshaller.types.BooleanFalse];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], true, "Checking encoded value[0]");
                }).
                And(function () {
                    jCC.equal(result[0][1], false, "Checking encoded value[1]");
                });
        });

        jCC.scenario("Mixed Boolean and Number array decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 2, jMarshaller.types.Number, 0, 0, 0, 128, jMarshaller.types.BooleanFalse];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], 128, "Checking encoded value[0]");
                }).
                And(function () {
                    jCC.equal(result[0][1], false, "Checking encoded value[1]");
                });
        });


        jCC.scenario("String array decoding", function () {
            var object, decoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 1].
                        concat([jMarshaller.types.String]).concat(jMarshaller.shortNumberToBytes(message.length)).concat(jMarshaller.stringToBytes(message));
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 1, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], message, "Checking encoded value[0]");
                });
        });

        jCC.scenario("Mixed String, Boolean, Number array decoding", function () {
            var object, decoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 3].
                        concat([jMarshaller.types.String]).concat(jMarshaller.shortNumberToBytes(message.length)).concat(jMarshaller.stringToBytes(message)).
                        concat([jMarshaller.types.Number]).concat(jMarshaller.numberToBytes(2013)).
                        concat([jMarshaller.types.BooleanTrue]);
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 3, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0], message, "Checking encoded value[0]");
                }).
                And(function () {
                    jCC.equal(result[0][1], 2013, "Checking encoded value[0]");
                }).
                And(function () {
                    jCC.equal(result[0][2], true, "Checking encoded value[1]");
                });
        });

        jCC.scenario("Array of array decoding", function () {
            var object, decoder, result, message = "Hello, World!";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Array, 0, 2].
                        concat([jMarshaller.types.Array, 0, 2]).
                        concat([jMarshaller.types.String]).concat(jMarshaller.shortNumberToBytes(message.length)).concat(jMarshaller.stringToBytes(message)).
                        concat([jMarshaller.types.Number]).concat(jMarshaller.numberToBytes(2013)).
                        concat([jMarshaller.types.BooleanTrue]);
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0].length, 2, "Checking encoding length");
                }).
                And(function () {
                    jCC.equal(result[0][0][0], message, "Checking encoded value[0][0]");
                }).
                And(function () {
                    jCC.equal(result[0][0][1], 2013, "Checking encoded value[0][1]");
                }).
                And(function () {
                    jCC.equal(result[0][1], true, "Checking encoded value[1]");
                });
        });

        jCC.scenario("Empty object decoding", function () {
            var object, decoder, result;

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Object, 0, 0];
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Object), true, "Checking result type");
                });
        });

        jCC.scenario("Not empty object decoding", function () {
            var object, decoder, result, foo = "foo", bar = "bar";

            jCC.
                Given(function () {
                    object = [jMarshaller.types.Object, 0, 2].
                        concat(jMarshaller.shortNumberToBytes(foo.length)).concat(jMarshaller.stringToBytes(foo)).
                        concat([jMarshaller.types.Number]).concat(jMarshaller.numberToBytes(2013)).
                        concat(jMarshaller.shortNumberToBytes(bar.length)).concat(jMarshaller.stringToBytes(bar)).
                        concat([jMarshaller.types.BooleanTrue]);
                }).
                And(function () {
                    decoder = Factory.serialize.decoder();
                }).
                When(function () {
                    result = decoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Object), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].hasOwnProperty(foo), true, "Checking foo property existence");
                }).
                And(function () {
                    jCC.equal(result[0].foo, 2013, "Checking foo property value");
                }).
                And(function () {
                    jCC.equal(result[0].hasOwnProperty(bar), true, "Checking bar property existence");
                }).
                And(function () {
                    jCC.equal(result[0].bar, true, "Checking bar property value");
                });
        });
    });
