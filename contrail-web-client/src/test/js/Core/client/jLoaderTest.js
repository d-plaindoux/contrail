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


require([ "Core/object/jObj", "Core/client/jLoader", "Core/test/jCC" ],
    function (jObj, jLoader, jCC) {
        "use strict";

        jCC.scenario("Checking jLoader with script name only", function () {
            var response;

            jCC.
                Given(jCC.Nothing).
                When(function () {
                    jLoader.load("./SimpleTest.js", function () {
                        response = require("test.loader");
                    });
                }).
                ThenAfter(500, function () {
                    jCC.equal(response, "Hello, World!");
                });
        });

        jCC.scenario("Checking jLoader with multiple script name only", function () {
            var response;

            jCC.
                Given(jCC.Nothing).
                When(function () {
                    jLoader().
                        source("./SimpleTest.js").
                        source("./SimpleTest2.js").
                        onLoad(function () {
                            response = require("test.loader2");
                        });
                }).
                ThenAfter(500, function () {
                    jCC.equal(response, "Hello, World!");
                });
        });
    });
