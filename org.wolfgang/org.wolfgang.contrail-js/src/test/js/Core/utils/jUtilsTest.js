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

require([ "Core/utils/jUUID", "qunit", "test/jCC" ],
    function (jUUID, QUnit, jCC) {
        "use strict";

        /**
         * Test UUID generation
         */
        jCC.scenario("Check UUID generation", function () {
            var uuid1, uuid2;

            jCC.
                Given(function () {
                    uuid1 = jUUID.generate();
                }).
                And(function () {
                    uuid2 = jUUID.generate();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.notEqual(uuid1, uuid2, "Two fresh UUID must be different");
                });
        });
    });