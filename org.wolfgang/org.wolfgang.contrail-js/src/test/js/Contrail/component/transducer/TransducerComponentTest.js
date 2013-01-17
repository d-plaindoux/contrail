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

require([ "Contrail/jContrail", "Core/object/jObj", "Core/test/jCC" ],
    function (Factory, jObj, jCC) {
        "use strict";

        var JSon = Factory.codec.json,
            Identity = Factory.codec.identity,
            PayLoad = Factory.codec.payload,
            Serialize = Factory.codec.serialize,
            Component = Factory.component;

        jCC.scenario("Check Component generation", function () {
            var c1, c2;

            c1 = Component.transducer(JSon.encoder(), JSon.decoder());
            c2 = Component.transducer(JSon.encoder(), JSon.decoder());

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                And(function () {
                    c2 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
                });
        });

        jCC.scenario("Check Component type to be a TransducerComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("TransducerComponent")), true, "Checking c1 instance of TransducerComponent");
                });
        });

        jCC.scenario("Check Component type to be a PipelineComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("PipelineComponent")), true, "Checking c1 instance of PipelineComponent");
                });
        });

        jCC.scenario("Check Component type to be a SourceComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("SourceComponent")), true, "Checking c1 instance of SourceComponent");
                });
        });

        jCC.scenario("Check Component type to be a DestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("DestinationComponent")), true, "Checking c1 instance of DestinationComponent");
                });
        });

        jCC.scenario("Check Component type to be Component", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Component.transducer(JSon.encoder(), JSon.decoder());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });

        jCC.scenario("Check JSon transducer", function () {
            var terminalFlow, composition, testInit, loopTest, index, object;

            testInit = jCC.
                Given(function () {
                    terminalFlow = Factory.flow.core();
                    terminalFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = data;
                    });
                }).
                And(function () {
                    composition = Component.compose([
                        Component.initial(Factory.flow.core()),
                        Component.transducer(JSon.decoder(), JSon.encoder()),
                        Component.transducer(JSon.encoder(), JSon.decoder()),
                        Component.terminal(terminalFlow)]);
                });

            loopTest = function (index) {
                testInit.
                    When(function () {
                        composition.getUpStreamDataFlow().handleData({ a:index });
                    }).
                    Then(function () {
                        jCC.equal(terminalFlow.content.a, index, "De-Serialise JSON object");
                    });
            };

            for (index = 0; index < 10; index += 1) {
                loopTest(index);
            }
        });


        jCC.scenario("Check JSon/Serialize transducer", function () {
            var terminalFlow, composition, testInit, loopTest, index, object;

            testInit = jCC.
                Given(function () {
                    terminalFlow = Factory.flow.core();
                    terminalFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = data;
                    });
                }).
                And(function () {
                    composition = Component.compose([
                        Component.initial(Factory.flow.core()),
                        Component.transducer(JSon.decoder(), JSon.encoder()),
                        Component.transducer(Serialize.decoder(), Serialize.encoder()),
                        Component.transducer(Serialize.encoder(), Serialize.decoder()),
                        Component.transducer(JSon.encoder(), JSon.decoder()),
                        Component.terminal(terminalFlow)]);
                });

            loopTest = function (index) {
                testInit.
                    When(function () {
                        composition.getUpStreamDataFlow().handleData({ a:index });
                    }).
                    Then(function () {
                        jCC.equal(terminalFlow.content.a, index, "De-Serialise JSON object");
                    });
            };

            for (index = 0; index < 10; index += 1) {
                loopTest(index);
            }
        });


        jCC.scenario("Check JSon/Serialize/Payload transducer", function () {
            var terminalFlow, composition, testInit, loopTest, index, object;

            testInit = jCC.
                Given(function () {
                    terminalFlow = Factory.flow.core();
                    terminalFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = data;
                    });
                }).
                And(function () {
                    composition = Component.compose([
                        Component.initial(Factory.flow.core()),
                        Component.transducer(JSon.decoder(), JSon.encoder()),
                        Component.transducer(Serialize.decoder(), Serialize.encoder()),
                        Component.transducer(PayLoad.decoder(), PayLoad.encoder()),
                        Component.transducer(PayLoad.encoder(), PayLoad.decoder()),
                        Component.transducer(Serialize.encoder(), Serialize.decoder()),
                        Component.transducer(JSon.encoder(), JSon.decoder()),
                        Component.terminal(terminalFlow)]);
                });

            loopTest = function (index) {
                testInit.
                    When(function () {
                        composition.getUpStreamDataFlow().handleData({ a:index });
                    }).
                    Then(function () {
                        jCC.equal(terminalFlow.content.a, index, "De-Serialise JSON object");
                    });
            };

            for (index = 0; index < 10; index += 1) {
                loopTest(index);
            }
        });

        jCC.scenario("Check JSon/Serialize/Payload/Identity transducer", function () {
            var terminalFlow, composition, testInit, loopTest, index, object;

            testInit = jCC.
                Given(function () {
                    terminalFlow = Factory.flow.core();
                    terminalFlow.handleData = jObj.procedure([jObj.types.Any], function (data) {
                        this.content = data;
                    });
                }).
                And(function () {
                    composition = Component.compose([
                        Component.initial(Factory.flow.core()),
                        Component.transducer(JSon.decoder(), JSon.encoder()),
                        Component.transducer(Serialize.decoder(), Serialize.encoder()),
                        Component.transducer(PayLoad.decoder(), PayLoad.encoder()),
                        Component.transducer(Identity.decoder(), Identity.encoder()),
                        Component.transducer(Identity.encoder(), Identity.decoder()),
                        Component.transducer(PayLoad.encoder(), PayLoad.decoder()),
                        Component.transducer(Serialize.encoder(), Serialize.decoder()),
                        Component.transducer(JSon.encoder(), JSon.decoder()),
                        Component.terminal(terminalFlow)]);
                });

            loopTest = function (index) {
                testInit.
                    When(function () {
                        composition.getUpStreamDataFlow().handleData({ a:index });
                    }).
                    Then(function () {
                        jCC.equal(terminalFlow.content.a, index, "De-Serialise JSON object");
                    });
            };

            for (index = 0; index < 10; index += 1) {
                loopTest(index);
            }
        });
    });