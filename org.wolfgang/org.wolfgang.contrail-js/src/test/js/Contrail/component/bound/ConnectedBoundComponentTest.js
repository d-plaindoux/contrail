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

require([ "qunit", "Core/jObj", "Contrail" , "test/jCC"],
    function (QUnit, jObj, Factory, jCC) {
        "use strict";

        jCC.scenario("Linking an initial with a terminal component and up message", function () {
            var initialStream, initialComponent, terminalComponent, terminalStream;

            jCC.
                Given(function () {
                    initialStream = Factory.flow.core();
                    initialStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    initialComponent = Factory.component.initial(initialStream);
                }).
                And(function () {
                    terminalStream = Factory.flow.core();
                    terminalStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        terminalComponent.getSource().getDownStreamDataFlow().handleData(data);
                    });
                }).
                And(function () {
                    terminalComponent = Factory.component.terminal(terminalStream);
                }).
                And(function () {
                    Factory.link.connect(initialComponent, terminalComponent);
                }).
                When(function () {
                    initialComponent.getDestination().getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(initialStream.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
                });
        });


        jCC.scenario("Linking an initial with a terminal component and multiple up messages", function () {
            var initialStream, initialComponent, terminalComponent, terminalStream;

            jCC.
                Given(function () {
                    initialStream = Factory.flow.core();
                    initialStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    initialComponent = Factory.component.initial(initialStream);
                }).
                And(function () {
                    terminalStream = Factory.flow.core();
                    terminalStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        terminalComponent.getSource().getDownStreamDataFlow().handleData(data);
                    });
                }).
                And(function () {
                    terminalComponent = Factory.component.terminal(terminalStream);
                }).
                And(function () {
                    Factory.link.connect(initialComponent, terminalComponent);
                }).
                When(function () {
                    initialComponent.getDestination().getUpStreamDataFlow().handleData("Hello,");
                }).
                And(function () {
                    initialComponent.getDestination().getUpStreamDataFlow().handleData(" World!");
                }).
                Then(function () {
                    QUnit.equal(initialStream.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
                });
        });

        jCC.scenario("Linking an initial with a terminal component and down message", function () {
            var initialStream, initialComponent, terminalComponent, terminalStream;

            jCC.
                Given(function () {
                    initialStream = Factory.flow.core();
                    initialStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        initialComponent.getDestination().getUpStreamDataFlow().handleData(data);
                    });
                }).
                And(function () {
                    initialComponent = Factory.component.initial(initialStream);
                }).
                And(function () {
                    terminalStream = Factory.flow.core();
                    terminalStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    terminalComponent = Factory.component.terminal(terminalStream);
                }).
                And(function () {
                    Factory.link.connect(initialComponent, terminalComponent);
                }).
                When(function () {
                    terminalComponent.getSource().getDownStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(terminalStream.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
                });
        });


        jCC.scenario("Linking an initial with a terminal component and multiple down messages", function () {
            var initialStream, initialComponent, terminalComponent, terminalStream;

            jCC.
                Given(function () {
                    initialStream = Factory.flow.core();
                    initialStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        initialComponent.getDestination().getUpStreamDataFlow().handleData(data);
                    });
                }).
                And(function () {
                    initialComponent = Factory.component.initial(initialStream);
                }).
                And(function () {
                    terminalStream = Factory.flow.core();
                    terminalStream.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    terminalComponent = Factory.component.terminal(terminalStream);
                }).
                And(function () {
                    Factory.link.connect(initialComponent, terminalComponent);
                }).
                When(function () {
                    terminalComponent.getSource().getDownStreamDataFlow().handleData("Hello,");
                }).
                And(function () {
                    terminalComponent.getSource().getDownStreamDataFlow().handleData(" World!");
                }).
                Then(function () {
                    QUnit.equal(terminalStream.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
                });
        });
    });