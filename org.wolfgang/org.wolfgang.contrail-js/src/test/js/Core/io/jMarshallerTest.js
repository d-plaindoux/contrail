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

require([ "IO/jMarshaller", "qunit" ], function (jMarshaller, QUnit) {
    "use strict";

    QUnit.test("Checking intToBytes length", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b.length, 4, "Size of bytes from int is 4");
    });

    QUnit.test("Checking intToBytes content at index 0", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[0], 0x89, "Content of bytes at index 0");
    });

    QUnit.test("Checking intToBytes content at index 1", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[1], 0xAB, "Content of bytes at index 1");
    });

    QUnit.test("Checking intToBytes content at index 2", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[2], 0xCD, "Content of bytes at index 2");
    });

    QUnit.test("Checking intToBytes content at index 3", function () {
        var i = 0x89ABCDEF, b;
        b = jMarshaller.intToBytes(i);
        QUnit.equal(b[3], 0xEF, "Content of bytes at index 3");
    });
});
