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

require([ "Core/test/jCC", "Core/object/jObj", "Network/jNetwork", "Contrail/jContrail", "Core/flow/jFlow" ],
    function (jCC, jObj, jNetwork, jContrail, jFlow) {
        "use strict";

        jCC.scenario("Checking client component with upstream package to new destination", function () {
            var clientComponent, terminal;

            jCC.
                Given(function () {
                    clientComponent = jNetwork.component.client("ws://localhost:8090/a");
                }).
                And(function () {
                    terminal = jContrail.component.terminal(jFlow.buffered());
                }).
                And(function () {
                    jContrail.component.compose([ clientComponent,terminal ]);
                }).
                When(function () {
                    clientComponent.getUpStreamDataFlow().handleData(jNetwork.packet("a", "b" , {}));
                }).
                Then(function () {
                    jCC.equal(clientComponent.acceptDestinationId("a"), true, "upstream packet source identifier is a destination");
                }).
                And(function () {
                    jCC.equal(clientComponent.acceptDestinationId("b"), false, "downstream packet destination identifier is a not destination");
                });
        });


        jCC.scenario("Checking client component with upstream package to new destination", function () {
            var initial, clientComponent;

            jCC.
                Given(function () {
                    clientComponent = jNetwork.component.client("ws://localhost:8090/a");
                }).
                And(function () {
                    initial = jContrail.component.initial(jFlow.buffered());
                }).
                And(function () {
                    jContrail.component.compose([ initial, clientComponent ]);
                }).
                When(function () {
                    clientComponent.getDownStreamDataFlow().handleData(jNetwork.packet("a", "b" , {}));
                }).
                Then(function () {
                    jCC.equal(clientComponent.acceptDestinationId("b"), true, "downstream packet destination identifier is a destination");
                }).
                And(function () {
                    jCC.equal(clientComponent.acceptDestinationId("a"), false, "upstream packet source identifier is a not destination");
                });
        });
    });