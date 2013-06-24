/**
 * 
 */
package ss.smtp.reciever;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zobo
 *
 */
public class RecieveList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4777451819021919811L;
	
	private List<Reciever> recievers = new ArrayList<Reciever>();

	/**
	 * @param reciever
	 */
	public void addReciever(Reciever reciever) {
		if (reciever == null){
			return;
		}
		this.recievers.add(reciever);
	}
	
	public List<Reciever> getRecievers(){
		return this.recievers;
	}

	/**
	 * @return
	 */
	public List<String> getSphereIdsList() {
		List<String> spheres = new ArrayList<String>(); 
		for (Reciever rec : this.recievers){
			String sphereId = rec.getRecipientsSphere();
			if (!spheres.contains(sphereId)){
				spheres.add(sphereId);
			}
		}
		return null;
	}
}
