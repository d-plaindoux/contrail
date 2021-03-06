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

/*global define, require, document*/

define("Core/client/jLoader", [ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function Loader() {
            jObj.bless(this);

            this.loadCallback = jObj.procedure([ jObj.types.Function ],
                function (callback) {
                    callback();
                });
        }

        Loader.init = function () {
            return new Loader();
        };

        Loader.init.load = jObj.procedure([ jObj.types.String, jObj.types.Function ],
            function (source, callback) {
                var script;

                script = document.createElement("script");
                script.type = "text/javascript";
                script.src = source;
                script.onload = callback;

                //for IE only:
                script.onreadystatechange = function () {
                    if (this.readyState === "complete") {
                        script.onload();
                    }
                };

                document.getElementsByTagName("head")[0].appendChild(script);
            });

        Loader.prototype.source = jObj.method([ jObj.types.String ], jObj.types.Named("Loader"),
            function (source) {
                var onLoad = this.loadCallback;

                this.loadCallback = jObj.procedure([ jObj.types.Function ],
                    function (callback) {
                        onLoad(function () {
                            Loader.init.load(source, callback);
                        });
                    });

                return this;
            });

        Loader.prototype.onLoad = jObj.procedure([ jObj.types.Function ],
            function (callback) {
                this.loadCallback(callback);
            });

        return Loader.init;
    });
