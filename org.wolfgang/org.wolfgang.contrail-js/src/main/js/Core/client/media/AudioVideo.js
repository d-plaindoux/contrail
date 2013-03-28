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

/*global define, navigator, window*/

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function AudioVideo(requiredStreams) {
            jObj.bless(this);

            var self = this, makeDevice, successCallback, failureCallback;

            this.requireStreams = requiredStreams;
            this.stream = null;
            this.callbacks = [];

            makeDevice =
                navigator.getUserMedia ||
                    navigator.webkitGetUserMedia ||
                    navigator.mozGetUserMedia ||
                    navigator.msGetUserMedia ||
                    jObj.throwError(jObj.exception("L.native.camera.streaming.not.supported"));

            successCallback = function (stream) {
                self.stream = stream;
                self.callbacks.forEach(function (callback) {
                    callback(stream);
                });
                self.callbacks = [];
            };

            failureCallback = function (error) {
                jObj.throwError(error);
            };

            makeDevice(requiredStreams, successCallback, failureCallback);
        }

        AudioVideo.init = jObj.constructor([ jObj.types.ObjectOf({video:jObj.types.Nullable(jObj.types.Boolean), audio:jObj.types.Nullable(jObj.types.Boolean)}) ],
            function (requiredStreams) {
                return new AudioVideo(requiredStreams);
            });

        AudioVideo.prototype.isRequired = jObj.method([ jObj.types.String ], jObj.types.Boolean,
            function (media) {
                return this.requireStreams.hasOwnProperty(media) && this.requireStreams[media];
            });

        AudioVideo.prototype.isVideoRequired = jObj.method([], jObj.types.Boolean,
            function () {
                return this.isRequired("video");
            });

        AudioVideo.prototype.isAudioRequired = jObj.method([], jObj.types.Boolean,
            function () {
                return this.isRequired("audio");
            });

        AudioVideo.prototype.whenReady = jObj.method([], jObj.types.Function,
            function (callback) {
                if (this.stream) {
                    callback(this.stream);
                } else {
                    this.callbacks.push(callback);
                }
            });

        return AudioVideo.init;
    });
