require {
	org.wofgang.contrail.connection.net.NetClient
	org.wofgang.contrail.connection.net.NetServer
	org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory as PayLoad
	org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory as Coercion
	org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory as Serialization
	org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent as ParallelSource
	org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent as ParallelDestination
	org.wolfgang.contrail.component.router.SourceAcceptanceComponent as ClientConnector
	org.wolfgang.contrail.bound.LoggerComponent as Logger
	org.wolfgang.contrail.component.router.event.Event as NetEvent
}

flow Coercion.Event {
	 Coercion(NetEvent) 
}

flow Parallel
	ParallelSource ParallelDestination
}
	
flow Event
	Parallel Coercion.Event ClientConnector NS
}
	
flow TCPEvent {
	PayLoad Serialization Event
}
	
router NETStation A.A {
	client A.B {
		endpoint {
			tcp://localhost:6666
		}
		flow {
			TCPEvent
		}
	}
}

binder NetHook byte[]*byte[] { 
	<binder name='NETHook' typein="byte[]" typeout="byte[]">
		TCPEvent
	</binder>

	<server endpoint='tcp://localhost:6667'> TCPEvent </server>

	<main> NS=NETStation Logger </main>
</ecosystem>
