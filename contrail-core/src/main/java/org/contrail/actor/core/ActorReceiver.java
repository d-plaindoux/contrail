package org.contrail.actor.core;

import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;

public interface ActorReceiver {

	void receiveRequest(Request request, Response response);
	
}
