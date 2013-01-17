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

require([ "Core/object/jObj", "Contrail/codec/jCodec", "test/jCC" ],
    function (jObj, Factory, jCC) {
        "use strict";

        jCC.scenario("String encoding", function () {
            var bytes, encoder, result;

            jCC.
                Given(function () {
                    bytes = "Hello, World!".split("");
                }).
                And(function () {
                    encoder = Factory.payload.encoder();
                }).
                When(function () {
                    result = encoder.transform(bytes);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Array), true, "Checking result type");
                }).
                And(function () {
                    jCC.equal(result[0].length, bytes.length + 4, "Checking encoding length");
                });
        });
    });

