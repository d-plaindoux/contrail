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

require([ "Core/io/jMarshaller", "qunit", "test/jCC" ],
    function (jMarshaller, QUnit, jCC) {
        "use strict";

        jCC.scenario("Checking intToBytes length", function () {
            var i, b;

            jCC.
                Given(function () {
                    i = 0x89ABCDEF;
                }).
                And(function () {
                    b = jMarshaller.numberToBytes(i);
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(b.length, 4, "Size of bytes from int is 4");
                });
        });

        jCC.scenario("Checking intToBytes content", function () {
            var i, b;

            jCC.
                Given(function () {
                    i = 0x89ABCDEF;
                }).
                And(function () {
                    b = jMarshaller.numberToBytes(i);
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(b[0], 0x89, "Content of bytes at index 0");
                }).
                And(function () {
                    QUnit.equal(b[1], 0xAB, "Content of bytes at index 1");
                }).
                And(function () {
                    QUnit.equal(b[2], 0xCD, "Content of bytes at index 2");
                }).
                And(function () {
                    QUnit.equal(b[3], 0xEF, "Content of bytes at index 3");
                });
        });
    });
