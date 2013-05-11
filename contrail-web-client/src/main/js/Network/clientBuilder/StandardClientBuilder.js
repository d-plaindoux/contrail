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

define([ "require", "Core/object/jObj", "Core/net/jSocket", "Contrail/jContrail", "./ClientBuilder" ],
    function (require, jObj, jSocket, jContrail, clientBuilder) {
        "use strict";

        function StandardClientBuilder(endPoint) {
            jObj.bless(this, clientBuilder(endPoint));
        }

        StandardClientBuilder.init = jObj.constructor([ jObj.types.String ],
            function (endPoint) {
                return new StandardClientBuilder(endPoint);
            });

        StandardClientBuilder.prototype.getIntermediateComponent = jObj.method([ ], jObj.types.Named("SourceComponent"),
            function () {
                var jSonifiers = [ require("Network/jNetwork").packet ];

                return jContrail.component.compose([
                    jContrail.component.transducer(jContrail.codec.stringify.encoder(), jContrail.codec.stringify.decoder()),
                    jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                    jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers))
                ]);
            });

        return StandardClientBuilder.init;
    });
