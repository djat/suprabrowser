/**
 * 
 */
package ss.client.ui.balloons;

/**
 * @author zobo
 *
 */
interface IBalloonListener {
	void closed();
	
	void created(BalloonWindow balloonWindow);
}
