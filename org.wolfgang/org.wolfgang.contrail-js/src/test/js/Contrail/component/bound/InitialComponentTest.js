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

require([ "qunit", "Core/jObj", "Contrail/Factory" ],
    function (QUnit, jObj, Factory) {
        "use strict";

        /**
         * Test generation
         */
        QUnit.test("Check Component generation", function () {
            var component1, component2;
            component1 = Factory.component.bound.initial(Factory.flow.basic());
            component2 = Factory.component.bound.initial(Factory.flow.basic());
            QUnit.notEqual(component1.getComponentId(), component2.getComponentId(), "Two fresh components must be different");
        });

        /**
         * Test generation
         */
        QUnit.test("Check Component down stream mechanism", function () {
            var component, dataFlow;
            dataFlow = Factory.flow.basic();
            dataFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                this.content = jObj.value(this.content, "") + data;
            });
            component = Factory.component.bound.initial(dataFlow);
            component.getDownStreamDataFlow().handleData("Hello, World!");

            QUnit.equal(dataFlow.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
        });
    });