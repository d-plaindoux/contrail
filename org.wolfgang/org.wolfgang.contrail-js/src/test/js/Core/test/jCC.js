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

/*global define, setTimeout*/

define("test/jCC", [ "qunit" ],
    function (QUnit) {
        "use strict";

        var jCC = {};

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
            return {
                And:jCC.AndThen,
                When:jCC.When(jCC.Nothing)
            };
        };

        jCC.AndThen = function (previous) {
            previous();
            return jCC.ThenSomething();
        };

        jCC.ThenAfter = function (previous) {
            return function (timeout, aThen) {
                previous();
                QUnit.stop();
                setTimeout(function () {
                    try {
                        aThen();
                    } finally {
                        QUnit.start();
                    }
                }, timeout);
                return jCC.ThenSomething();
            };
        };

        jCC.Then = function (previous) {
            return function (aThen) {
                previous();
                aThen();
                return jCC.ThenSomething();
            };
        };

        jCC.ThenError = function (previous) {
            return function (aThen) {
                try {
                    previous();
                } catch (e) {
                    aThen(e);
                    return jCC.ThenSomething();
                }

                throw { message:"expecting an exception"};
            };
        };

        /*
         * When block definition
         */

        jCC.WhenNothing = function (previous) {
            return {
                Then:jCC.Then(previous),
                ThenError:jCC.ThenError(previous)
            };
        };

        jCC.WhenSomething = function (previous) {
            return {
                And:jCC.When(previous),
                Then:jCC.Then(previous),
                ThenAfter:jCC.ThenAfter(previous),
                ThenError:jCC.ThenError(previous)
            };
        };

        jCC.When = function (previousGiven) {
            return function (currentWhen) {
                return jCC.WhenSomething(function () {
                    previousGiven();
                    currentWhen();
                });
            };
        };

        /*
         * Given block definition
         */

        jCC.GivenNothing = {
            When:jCC.When(jCC.Nothing),
            WhenNothing:jCC.WhenNothing(jCC.Nothing)
        };

        jCC.GivenSomething = function () {
            return {
                And:jCC.Given,
                When:jCC.When(jCC.Nothing),
                WhenNothing:jCC.WhenNothing(jCC.Nothing)
            };
        };

        jCC.Given = function (given) {
            given();
            return jCC.GivenSomething();
        };

        return jCC;

    });
