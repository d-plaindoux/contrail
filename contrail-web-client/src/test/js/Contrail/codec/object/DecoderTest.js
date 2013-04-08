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


require([ "Core/object/jObj", "Contrail/codec/jCodec", "Core/test/jCC" ],
    function (jObj, Factory, jCC) {
        "use strict";

        function B() {
            jObj.bless(this);
        }

        function A(name, object) {
            jObj.bless(this);
            this.name = name;
            this.object = object;
        }


        jCC.scenario("Object decoding", function () {
            var value , decoder, result;

            jCC.
                Given(function () {
                    value = { jN:"A", jV:{ name:"name of A", object:{jN:"B", jV:{}}}};
                }).
                And(function () {
                    B.init = jObj.jSonifable(function () {
                        return new B();
                    }).nameAndType("B","B").withKeys();
                    A.init = jObj.jSonifable(function (name, object) {
                        return new A(name, object);
                    }).nameAndType("A","A").withKeys("name", "object");

                    decoder = Factory.object.decoder([A.init, B.init]);
                }).
                When(function () {
                    result = decoder.transform(value);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(result[0], jObj.types.Named("A")), true, "Checking result type");
                });
        });
    });

