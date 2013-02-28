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

        function MyBuilder(endPoint) {
            jObj.bless(this, jNetwork.builder.core(endPoint));
        }

        MyBuilder.init = function(endPoint) {
            return new MyBuilder(endPoint);
        };

        MyBuilder.prototype.activate = function() {
            return jContrail.component.initial(jFlow.buffered());
        };

        jCC.scenario("Checking active route for packet with new destination", function () {
            var table, routerComponent;

            jCC.
                Given(function () {
                    table = jNetwork.table(MyBuilder.init);
                    table.addRoute("b","Route.To.B");
                }).
                And(function () {
                    routerComponent = jNetwork.component.router(table);
                }).
                When(function () {
                   routerComponent.getDownStreamDataFlow().handleData(jNetwork.packet("a", "b" , {}));
                }).
                Then(function () {
                    jCC.equal(routerComponent.getActiveRoute("b",null) !== null, true, "Active route must be created for destination 'b'");
                });
        });
    });