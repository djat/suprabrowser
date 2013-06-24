/**
 * Jul 3, 2006 : 1:02:53 PM
 * 
 * Author : dankosedin
 */
package ss.client.ui.controllers;

import java.util.Vector;

/**
 * @author dankosedin
 *
 */
public class PendingSpheresController {

	
	private Vector<String> pendingSpheres = new Vector<String>();
	/*
	 * Unlike the new collection implementations, Vector is synchronized.
	 * 
	 * Note that this implementation of List is not synchronized.
	 * f multiple threads access an ArrayList instance concurrently, 
	 * and at least one of the threads modifies the list structurally, 
	 * it must be synchronized externally. (A structural modification 
	 * is any operation that adds or deletes one or more elements, or 
	 * explicitly resizes the backing array; merely setting the value 
	 * of an element is not a structural modification.) This is typically 
	 * accomplished by synchronizing on some object that naturally encapsulates  
	 * the list. If no such object exists, the list should be "wrapped" using 
	 * the Collections.synchronizedList  method. This is best done at creation  
	 * time, to prevent accidental unsynchronized access to the list: 
	 * List list = Collections.synchronizedList(new ArrayList(...));
	 * 
	 * So maybe it better to remove synchronization?
	 */
	
	
	public boolean containsPending(String sphere_id) {
		synchronized (this.pendingSpheres) {
			return this.pendingSpheres.contains(sphere_id);
		}
	}

	public void addPending(String sphere_id) {
		synchronized (this.pendingSpheres) {
			this.pendingSpheres.add(sphere_id);
		}
	}

	public void removePending(String sphere_id) {
		synchronized (this.pendingSpheres) {
			this.pendingSpheres.remove(sphere_id);
		}
	}	

}
