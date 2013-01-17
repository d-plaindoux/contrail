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

require([ "Core/object/jObj", "Contrail/jContrail", "test/jCC" ],
    function (jObj, Factory, jCC) {
        "use strict";

        jCC.scenario("Checking data flow", function () {
            var dataFlow;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.core();
                    dataFlow.handleData = jObj.procedure([jObj.types.Any],
                        function (data) {
                            this.content = jObj.value(this.content, "") + data;
                        });
                }).
                When(function () {
                    dataFlow.handleData("Hello,");
                }).
                Then(function () {
                    jCC.equal(dataFlow.content, "Hello,", "Checking content after handling 'Hello,'");
                }).
                When(function () {
                    dataFlow.handleData(" World!");
                }).
                Then(function () {
                    jCC.equal(dataFlow.content, "Hello, World!", "Checking content after handling 'Hello,' and ' World!'");
                });
        });

        jCC.test("Checking closed data flow", function () {
            var dataFlow, closeableDataFlow;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.core();
                    dataFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    closeableDataFlow = Factory.flow.closeable(dataFlow);
                }).
                When(function () {
                    closeableDataFlow.handleData("Hello,");
                }).
                Then(function () {
                    jCC.equal(dataFlow.content, "Hello,", "Checking content after handling 'Hello,'");
                }).
                When(function () {
                    closeableDataFlow.handleClose();
                }).
                And(function () {
                    closeableDataFlow.handleData(" World!");
                }).
                ThenError(function () {
                    jCC.ok(true, "An Exception has been raised");
                });
        });

        jCC.test("Checking filtered data flow", function () {
            var dataFlow, filteredDataFlow;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.core();
                    dataFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    filteredDataFlow = Factory.flow.filtered(dataFlow, function (data) {
                        return (data === "Hello,") ? null : data;
                    });
                }).
                When(function () {
                    filteredDataFlow.handleData("Hello,");
                }).
                And(function () {
                    filteredDataFlow.handleData(" World!");
                }).
                Then(function () {
                    jCC.equal(dataFlow.content, " World!", "Checking content after handling 'removed[Hello,] World!'");
                });
        });
    });