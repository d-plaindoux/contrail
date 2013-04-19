package org.contrail.web.actor;

import org.contrail.actor.core.ActorException;
import org.contrail.actor.event.Response;
import org.contrail.common.concurrent.Promise;

public class PromiseResponse extends Promise<Object, ActorException> implements Response {
	// Nothing to be done
}