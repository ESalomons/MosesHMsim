package nl.saxion.ami.moses.common.datamodel;

import java.io.Serializable;

/**
 * Just a decorator for data that can be serialized and sent over the wire. Depending on the
 * distribution component in use a different wire format can be chosen. The distribution service
 * component should take care of converting these objects to/from the wire format, so that
 * application code only deals with subclasses of DistributableData. These are the only data
 * objects that should be published on the EventBus.
 */
public abstract class DistributableData implements Serializable {
}
