import org.wofgang.contrail.connection.net.NetClient
import org.wofgang.contrail.connection.net.NetServer

import as Event org.wolfgang.contrail.component.router.event.Event

import as PayLoad org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory
import as Coercion org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory
import as Serialization org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory
import as ParallelSource org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent
import as ParallelDestination org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent
import as Client(uri) org.wolfgang.contrail.component.bound.gateway.ClientComponent
import as Server(uri factory) org.wolfgang.contrail.component.bound.gateway.ServerComponent 

define Parallel  { ParallelSource + ParallelDestination }
define NetEvent  { Parallel <> Coercion Event }
define TCPEvent  { PayLoad <> Serialization <> NetEvent }
define TCPClient { uri | reverse TCPEvent <> Client uri;uri }
define TCPServer { uri | Server uri:uri factory:{ TCPEvent <> NetStation as $mainStation } <> Manager }

define NetStation {
    switch [ 
    | A.A => switch [ 
             | Service  => { ServiceAgent  } 
			 | Transfer => { TransferAgent } 
			 | _        => { /* lambda */  } 
			 ]  
	| A.B => { TCPClient "tcp://localhost:6667" }
	| _   => { TCPClient "tcp://localhost:6668" }
	] 
}

start { TCPServer "tcp://localhost:6666"  }
