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

require([ "Contrail/Factory", "Core/jObj", "qunit" ],
    function (Factory, jObj, QUnit) {
        "use strict";

        /**
         * Test generation
         */
        QUnit.test("Check Component generation", function () {
            var c1 = Factory.component.basic.source(),
                c2 = Factory.component.basic.source();
            QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
        });

        /**
         * Test source
         */
        QUnit.test("Check Component destination linkage acceptation", function () {
            var c1 = Factory.component.basic.destination(),
                c2 = Factory.component.basic.source();
            QUnit.equal(c2.acceptDestination(c1.getComponentId()), true, "Destination must be unbound");
        });

        /**
         * Test source
         */
        QUnit.test("Check Component source linkage", function () {
            var c1 = Factory.component.basic.destination(),
                c2 = Factory.component.basic.source(),
                lm = Factory.link.manager();
            QUnit.equal(c2.acceptDestination(c1.getComponentId()), true, "Destination must be unbound");
            lm.link(c2, c1);
            QUnit.equal(c2.acceptDestination(c1.getComponentId()), false, "Destination must be setup");
        });

        /**
         * Test type
         */
        QUnit.test("Check Component type #1", function () {
            var c1 = Factory.component.basic.source();

            QUnit.equal(jObj.instanceOf(c1, "SourceComponent"), true, "Checking c1 instance of SourceComponent");
        });

        QUnit.test("Check Component type #2", function () {
            var c1 = Factory.component.basic.source();

            QUnit.equal(jObj.instanceOf(c1, "Component"), true, "Checking c1 instance of Component");
        });
    });