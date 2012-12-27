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


require([ "Core/object/jObj", "Contrail/codec", "qunit" , "test/jCC" ],
    function (jObj, Factory, QUnit, jCC) {
        "use strict";

        jCC.scenario("Object decoding", function () {
            var string , decoder, result;

            jCC.
                Given(function () {
                    string = '{"a":true}';
                }).
                And(function () {
                    decoder = Factory.json.decoder();
                }).
                When(function () {
                    result = decoder.transform(string);
                }).
                Then(function () {
                    QUnit.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(result[0], jObj.types.Object), true, "Checking result type");
                }).
                And(function () {
                    QUnit.equal(result[0].a, true, "Checking decoding value");
                });
        });
    });

