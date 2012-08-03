import org.wofgang.contrail.connection.net.NetClient
import org.wofgang.contrail.connection.net.NetServer

import as PayLoad org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory
import as Coercion org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory
import as Serialization org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory
import as ParallelSource org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent
import as ParallelDestination org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent
import as Event org.wolfgang.contrail.component.router.event.Event
import as Client org.wolfgang.contrail.component.bound.gateway.ClientComponent
import as Server org.wolfgang.contrail.component.bound.gateway.ServerComponent

define Parallel  { ParallelSource + ParallelDestination }
define NetEvent  { Parallel <> Coercion Event }
define TCPEvent  { PayLoad <> Serialization <> NetEvent }
define TCPClient { uri | reverse TCPEvent <> Client[uri] }
define TCPServer { uri | factory | reverse TCPEvent <> Server[uri,factory] }

define NetStation router<StreamDataHandlerStation> [
	case 'A.A' {
		/* Nothing == Lambda */
	}
	case 'A.B' {
		TCPClient "tcp://localhost:6667"
	} 
	default {
		TCPClient "tcp://localhost:6668"
	}		
]

start { TCPServer "tcp://localhost:6666" { b | b <> TCPEvent <> NetStation } <> Logger }
