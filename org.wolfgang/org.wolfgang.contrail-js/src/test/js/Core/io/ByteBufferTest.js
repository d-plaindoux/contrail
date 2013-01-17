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

require([ "test/jCC", "Core/utils/jUUID", "Core/io/jMarshaller", "Core/io/ByteBuffer" ],
    function (jCC, jUUID, jMarshaller, byteBuffer) {
        "use strict";

        jCC.scenario("Checking Byte buffer creation", function () {
            var buffer;

            jCC.
                Given(function () {
                    buffer = byteBuffer();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(buffer.isClosed(), false, "New buffer is not closed");
                }).
                And(function () {
                    jCC.equal(buffer.size(), 0, "New buffer is empty");
                });
        });

        jCC.scenario("Checking Byte buffer write", function () {
            var buffer, message;

            jCC.
                Given(function () {
                    buffer = byteBuffer();
                }).
                And(function () {
                    message = "Hello, World!";
                }).
                When(function () {
                    buffer.write(jMarshaller.stringToBytes(message));
                }).
                Then(function () {
                    jCC.equal(buffer.size(), message.length * 2, "Buffer must contain the written array");
                });
        });

        jCC.scenario("Checking Byte buffer read", function () {
            var buffer, message, bytes;

            jCC.
                Given(function () {
                    buffer = byteBuffer();
                }).
                And(function () {
                    message = "Hello, World!";
                }).
                And(function () {
                    bytes = jUUID.array(message.length * 2);
                }).
                When(function () {
                    buffer.write(jMarshaller.stringToBytes(message));
                }).
                Then(function () {
                    jCC.equal(buffer.read(bytes), message.length * 2, "Buffer must provide the written array with the same length");
                }).
                And(function () {
                    jCC.equal(jMarshaller.bytesToString(bytes), message, "Buffer must provide the same written array");
                });
        });

        jCC.scenario("Checking Empty Byte buffer read ", function () {
            var buffer, message, bytes;

            jCC.
                Given(function () {
                    buffer = byteBuffer();
                }).
                And(function () {
                    bytes = jUUID.array(10);
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(buffer.read(bytes), 0, "Buffer must provide nothing");
                });
        });


        jCC.scenario("Checking Closed Empty Byte buffer read ", function () {
            var buffer, message, bytes;

            jCC.
                Given(function () {
                    buffer = byteBuffer();
                }).
                And(function () {
                    bytes = jUUID.array(10);
                }).
                When(function () {
                    buffer.close();
                }).
                Then(function () {
                    jCC.equal(buffer.read(bytes), -1, "Buffer must provide nothing");
                });
        });
    });