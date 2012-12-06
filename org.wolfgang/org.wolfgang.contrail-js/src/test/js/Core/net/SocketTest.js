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

require([ "qunit", "Contrail/Factory", "Core/Socket", "test/jCC" ],
    function (QUnit, Factory, socket, jCC) {
        "use strict";

        jCC.scenario("Trying code", function () {
            var dataFlow, client;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.accumulated();
                }).
                And(function () {
                    client = socket("ws:ws://127.0.0.1:1337", dataFlow);
                }).
                When(function () {
                    client.send("Ping");
                }).
                Then(function () {
                    // review -- TODO
                });
        });
    });