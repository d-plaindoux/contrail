import org.wofgang.contrail.connection.net.NetClient
import org.wofgang.contrail.connection.net.NetServer

import as Event org.wolfgang.contrail.component.router.event.Event

import as ClientHandler org.wolfgang.contrail.component.bound.gateway.ClientComponent
import as ServerHandler org.wolfgang.contrail.component.bound.gateway.ServerComponent 

import as PayLoad org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory
import as Coercion org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory
import as Serialization org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory
import as ParallelSource org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent
import as ParallelDestination org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent

define Parallel { s | ParallelSource <> s <> ParallelDestination }
define TCPEvent { PayLoad <> Parallel Serialization <> Coercion Event }
define Client   { uri station | ClientHandler uri { b | b <> TCPEvent <> station } }
define Server   { uri station | ServerHandler uri { b | b <> TCPEvent <> station } }

define NetStation { 
    router id=A.A {     
	case A.B { Client tcp://localhost:6667 self }
	case A.C { Client tcp://localhost:6668 self }
    default switch { 
             case Service  { ServiceAgent  } 
			 case Transfer { TransferAgent } 
			 default       { /* lambda */  } 
			 }  
	}
}

start { Server tcp://localhost:6666 NetStation }
