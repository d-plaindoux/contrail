transducer PayLoad byte[] <-> Bytes {
	PayLoad
}

transducer Jaxb String <-> Object {
	JAXB
}

transducer Serialization Bytes <-> Object {
	Serialization
}

transducer NetworkEvent Object <-> NetworkEvent {
	Coercion NetworkEvent
}

flow NetFlow byte[] <-> NetworkEvent {
	PayLoad Serialization NetworkEvent
}

flow WebFlow String <-> NetworkEvent {
	Jaxb NetworkEvent
}

router NetworkRouter NetworkEvent <-> NetworkEvent {
	table { 
		case component01 localhost:6666 NetFlow
		default localhost:6667 NetFlow
	}
	
		
}