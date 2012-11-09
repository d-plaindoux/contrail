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

/*global define*/

define("test/jCC", [ "qunit" ],
    function (QUnit) {
        "use strict";

        var jCC;

        jCC = {};

        jCC.scenario = function (name, scenario) {
            QUnit.test(name, scenario);
        };

        jCC.Nothing = function () {
            // Nothing to do
        };

        /*
         * Then block definition
         */

        jCC.ThenSomething = function () {
            return { And:jCC.AndThen, When:jCC.When };
        };

        jCC.Then = function (theThen) {
            theThen();
            return jCC.ThenSomething();
        };

        jCC.AndThen = function (theThen) {
            theThen();
            return jCC.ThenSomething();
        };

        /*
         * When block definition
         */

        jCC.WhenNothing = function () {
            return { Then:jCC.Then };
        };

        jCC.WhenSomething = function () {
            return {
                And:jCC.AndWhen,
                Then:jCC.Then
            };
        };

        jCC.When = function (theWhen) {
            theWhen();
            return jCC.WhenSomething();
        };

        jCC.AndWhen = function (theWhen) {
            theWhen();
            return jCC.WhenSomething();
        };

        /*
         * Given block definition
         */

        jCC.GivenNothing = {
            When:jCC.When,
            WhenNothing:jCC.WhenNothing()
        };

        jCC.GivenSomething = function () {
            return {
                And:jCC.AndGiven,
                When:jCC.When,
                WhenNothing:jCC.WhenNothing()
            };
        };

        jCC.Given = function (theGiven) {
            theGiven();
            return jCC.GivenSomething();
        };

        jCC.AndGiven = function (theGiven) {
            theGiven();
            return jCC.GivenSomething();
        };

        return jCC;

    });
