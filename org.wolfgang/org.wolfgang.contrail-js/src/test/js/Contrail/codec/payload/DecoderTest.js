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

require([ "Core/jObj", "Codec/Factory", "qunit" , "test/jCC" ],
    function (jObj, Factory, QUnit, jCC) {
        "use strict";

        jCC.scenario("String decoding", function () {
            var bytes, decoder, result;

            jCC.
                Given(function () {
                    bytes = [0, 0, 0, 13].concat("Hello, World!".split("").concat([0, 0, 0, 50]).concat("Bla".split("")));
                }).
                And(function () {
                    decoder = Factory.payload.decoder();
                }).
                When(function () {
                    result = decoder.transform(bytes);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.isAType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].join(""), "Hello, World!".split("").join(""), "Checking decoding value");
                });
        });
    });

