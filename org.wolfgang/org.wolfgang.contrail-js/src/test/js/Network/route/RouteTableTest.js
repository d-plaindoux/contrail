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

require([ "Core/test/jCC", "Network/jNetwork", "External/jSonLib" ],
    function (jCC, jNetwork, JSON) {
        "use strict";

        jCC.scenario("Checking route table add/retrieve capabilities", function () {
            var table;

            jCC.
                Given(function () {
                    table = jNetwork.table(jNetwork.builder.standard);
                }).
                When(function () {
                    table.addEntry("a", "ws://localhost/a");
                }).
                Then(function () {
                    jCC.equal(table.getEntry("a").getEndPoint(), "ws://localhost/a");
                });
        });

        jCC.scenario("Checking route table failed retrieve capabilities", function () {
            var table;

            jCC.
                Given(function () {
                    table = jNetwork.table(jNetwork.builder.standard);
                }).
                When(function () {
                    table.getEntry("a");
                }).
                ThenError(function (e) {
                    jCC.ok("Exception catch");
                });
        });

        jCC.scenario("Checking route table failed add/add capabilities", function () {
            var table;

            jCC.
                Given(function () {
                    table = jNetwork.table(jNetwork.builder.standard);
                }).
                When(function () {
                    table.addEntry("a", "ws://localhost/a");
                }).
                And(function () {
                    table.addEntry("a", "ws://localhost/a");
                }).
                ThenError(function (e) {
                    jCC.ok("Exception catch");
                });
        });

        jCC.scenario("Loading and Checking route table capabilities", function () {
            var table, configuration;

            jCC.
                Given(function () {
                    table = jNetwork.table(jNetwork.builder.standard);
                }).
                And(function () {
                    configuration = '{"a":"ws://localhost/a", "b":"ws://localhost/b"}';
                }).
                When(function () {
                    table.populate(JSON.parse(configuration));
                }).
                Then(function () {
                    jCC.equal(table.getEntry("a").getEndPoint(), "ws://localhost/a");
                }).
                And(function () {
                    jCC.equal(table.getEntry("b").getEndPoint(), "ws://localhost/b");
                });
        });
    });