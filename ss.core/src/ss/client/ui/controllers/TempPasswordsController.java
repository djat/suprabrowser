/**
 * 
 * Author : dankosedin
 */
package ss.client.ui.controllers;

import java.util.Hashtable;

public class TempPasswordsController {

	private Hashtable<String, String> passphrases = new Hashtable<String, String>();

	public void setTempPW(String supraSphereName, String tempPW) {

		this.passphrases.put(supraSphereName, tempPW);
		// this.tempPW = tempPW;

	}

	public String getTempPW(String supraSphereName) {

		return this.passphrases.get(supraSphereName);

	}

}