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

require([ "Core/io/jMarshaller", "Core/test/jCC" ],
    function (jMarshaller, jCC) {
        "use strict";

        var epsilon = 0.0001;

        jCC.scenario("Checking numberToBytes length", function () {
            var i, b;

            jCC.
                Given(function () {
                    i = 0x89ABCDEF;
                }).
                And(function () {
                    b = jMarshaller.numberToBytes(i);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(b.length, 4, "Size of bytes from int is 4");
                });
        });

        jCC.scenario("Checking numberToBytes content", function () {
            var i, b;

            jCC.
                Given(function () {
                    i = 0x89ABCDEF;
                }).
                And(function () {
                    b = jMarshaller.numberToBytes(i);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(b[0], 0x89, "Content of bytes at index 0");
                }).
                And(function () {
                    jCC.equal(b[1], 0xAB, "Content of bytes at index 1");
                }).
                And(function () {
                    jCC.equal(b[2], 0xCD, "Content of bytes at index 2");
                }).
                And(function () {
                    jCC.equal(b[3], 0xEF, "Content of bytes at index 3");
                });
        });

        jCC.scenario("Checking positive bytesToNumber content", function () {
            var b, i;

            jCC.
                Given(function () {
                    b = jMarshaller.numberToBytes(12345678);
                }).
                And(function () {
                    i = jMarshaller.bytesToNumber(b);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(i, 12345678, "Value of the integer");
                });
        });

        jCC.scenario("Checking negative bytesToNumber content", function () {
            var b, i;

            jCC.
                Given(function () {
                    b = jMarshaller.numberToBytes(-12345678);
                }).
                And(function () {
                    i = jMarshaller.bytesToNumber(b);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(i, -12345678, "Value of the integer");
                });
        });


        jCC.scenario("Checking positive bytesToFloat content", function () {
            var b, i;

            jCC.
                Given(function () {
                    b = jMarshaller.floatToBytes(1234.5678);
                }).
                And(function () {
                    i = jMarshaller.bytesToFloat(b);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(true, i - 1234.5678 < epsilon, "Value of the float");
                });
        });

        jCC.scenario("Checking negative bytesToFloat content", function () {
            var b, i;

            jCC.
                Given(function () {
                    b = jMarshaller.floatToBytes(-1234.5678);
                }).
                And(function () {
                    i = jMarshaller.bytesToFloat(b);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(true, i + 1234.5678 < epsilon, "Value of the float");
                });
        });
    });
