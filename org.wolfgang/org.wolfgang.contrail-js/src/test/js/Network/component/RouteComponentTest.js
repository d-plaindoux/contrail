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

        jCC.scenario("Checking route component", function () {
            var table, component, initial, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    component = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    initial = jContrail.component.initial(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ initial, component ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "Hello, World!");
                }).
                When(function () {
                    initial.getUpStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.types.checkType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!'", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/b", "End point is 'ws://localhost/b'");
                });
        });
    });