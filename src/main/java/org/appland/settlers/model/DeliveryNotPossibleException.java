package org.appland.settlers.model;


public class DeliveryNotPossibleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6581138146376890558L;

	
	public DeliveryNotPossibleException() {
		super("This building does not accept deliveries.");
	}
}
