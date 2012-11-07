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

require([ "Core/jObj", "qunit" ],
    function (jObj, QUnit) {
        "use strict";

        /**
         * Test Type Checking
         */
        function A() {
            jObj.bless(this);
        }

        QUnit.test("Check Subtype a:A <? A", function () {
            var a = new A();
            jObj.checkType(a, "A");
            QUnit.equal(true, true, " a is an instance of A");
        });

        QUnit.test("Check Subtype a:A <? B", function () {
            var a = new A();
            try {
                jObj.checkType(a, "B");
                QUnit.equal(true, false, "a is not an instance of B");
            } catch (e) {
                QUnit.equal(jObj.instanceOf(e, jObj.types.Named("RuntimeTypeError")), true, "Checking throws error to be a TypeError");
            }
        });
    });

