import org.wofgang.contrail.connection.net.NetClient
import org.wofgang.contrail.connection.net.NetServer

import org.wolfgang.contrail.component.router.event.Event

import org.wolfgang.contrail.component.bound.gateway.ClientComponent
import org.wolfgang.contrail.component.bound.gateway.ServerComponent 

import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory
import org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent
import org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent

define Parallel = function(s){ ParallelSourceComponent <> s <> ParallelDestinationComponent }
define TCPEvent = function() { PayLoad <> Parallel Serialization <> Coercion Events }
define Client   = functionc(uri)(station) { ClientComponent uri <> TCPEvent <> station }
define Server   = functionc(uri)(station) { ServerComponent uri (function(b){ b <> TCPEvent <> station } }

define NetStation =  
    router id=A.A {     
	case A.B: Client "tcp://localhost:6667" ((OnLink A.A) <> self)
	case A.C: Client "tcp://localhost:6668" self
    default: switch { 
             case Service: ServiceAgent ()
			 case Transfer:TransferAgent () 
			 }  
	}

Server "tcp://localhost:6666" ( client -> client <> NetStation )
