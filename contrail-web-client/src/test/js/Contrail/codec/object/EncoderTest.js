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
            jObj.bless(this, new B());
            this.name = name;
            this.object = object;
        }

        jCC.scenario("Object encoding", function () {
            var object, encoder, result;

            jCC.
                Given(function () {
                    object = new A("name of A", new B());
                }).
                And(function () {
                    jObj.jSonifable(B).nameAndType("B","B").withKeys();
                    jObj.jSonifable(A).nameAndType("A","A").withKeys("name", "object");

                    encoder = Factory.object.encoder([A, B]);
                }).
                When(function () {
                    result = encoder.transform(object);
                }).
                Then(function () {
                    jCC.equal(result.length, 1, "Checking result length");
                }).
                And(function () {
                    jCC.equal(result[0].hasOwnProperty("jN"), true, "Result must have 'jN' property defined");
                    jCC.equal(result[0].hasOwnProperty("jV"), true, "Result must have 'jV' property defined");
                }).
                And(function () {
                    jCC.equal(result[0].jN, "A", "Result 'jN' must be 'A'");
                }).
                And(function () {
                    jCC.equal(result[0].jV.hasOwnProperty("name"), true, "Result value must have 'name' property defined");
                }).
                And(function () {
                    jCC.equal(result[0].jV.name, "name of A", "Result value 'name' must have the value 'name of A'");
                }).
                And(function () {
                    jCC.equal(result[0].jV.hasOwnProperty("object"), true, "Result value must have 'object' property defined");
                }).
                And(function () {
                    jCC.equal(result[0].jV.object.hasOwnProperty("jN"), true, "Result 'object' must have 'jN' property defined");
                    jCC.equal(result[0].jV.object.hasOwnProperty("jV"), true, "Result 'object' must have 'jV' property defined");
                }).
                And(function () {
                    jCC.equal(result[0].jV.object.jN, "B", "Result 'object.jN' must be 'B'");
                });
        });
    })
;

