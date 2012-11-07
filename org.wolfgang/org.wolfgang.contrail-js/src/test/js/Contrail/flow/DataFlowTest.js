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

        QUnit.test("Checking data flow", function () {
            var dataFlow = Factory.flow.basic();

            dataFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                this.content = jObj.value(this.content, "") + data;
            });

            dataFlow.handleData("Hello,");
            QUnit.equal(dataFlow.content, "Hello,", "Checking content after handling 'Hello,'");

            dataFlow.handleData(" World!");
            QUnit.equal(dataFlow.content, "Hello, World!", "Checking content after handling 'Hello,' and ' World!'");
        });

        QUnit.test("Checking closed data flow", function () {
            var dataFlow, closeableDataFlow;

            dataFlow = Factory.flow.basic();
            closeableDataFlow = Factory.flow.closeable(dataFlow);

            dataFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                this.content = jObj.value(this.content, "") + data;
            });

            closeableDataFlow.handleData("Hello,");
            QUnit.equal(dataFlow.content, "Hello,", "Checking content after handling 'Hello,'");

            closeableDataFlow.handleClose();

            try {
                closeableDataFlow.handleData(" World!");
                QUnit.ok(false, "An Exception hasn't been raised");
            } catch (e) {
                QUnit.ok(true, "An Exception has been raised");
            }
        });
    });