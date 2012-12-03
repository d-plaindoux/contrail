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

require([ "Core/jObj", "Codec/Factory", "qunit", "test/jCC", "IO/jMarshaller"],
    function (jObj, Factory, QUnit, jCC, Marshaller) {
        "use strict";

        jCC.scenario("Object encoding string", function () {
            var string, decoder, result;

            jCC.
                Given(function () {
                    string = "Hello, World!";
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(string);
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
                    QUnit.equal(result[0].length, string.length * 2 + 1, "Checking first result length");
                }).
                And(function () {
                    QUnit.equal(result[0][0], Marshaller.types.String, "Checking encoding type");
                }).
                And(function () {
                    result[0].splice(0, 1);
                    QUnit.equal(Marshaller.bytesToString(result[0]), string, "Checking first result value");
                });
        });


        jCC.scenario("Object encoding number", function () {
            var integer, decoder, result;

            jCC.
                Given(function () {
                    integer = -123;
                }).
                And(function () {
                    decoder = Factory.serialize.encoder();
                }).
                When(function () {
                    result = decoder.transform(integer);
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
                    QUnit.equal(result[0][0], Marshaller.types.Number, "Checking encoding type");
                }).
                And(function () {
                    result[0].splice(0, 1);
                    QUnit.equal(Marshaller.bytesToNumber(result[0]), integer, "Checking first result value");
                });
        });
    });

