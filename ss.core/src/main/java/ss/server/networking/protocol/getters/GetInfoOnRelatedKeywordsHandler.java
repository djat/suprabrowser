/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.event.tagging.InfoOnRelatedKeywordsData;
import ss.client.networking.protocol.getters.GetInfoOnRelatedKeywordsCommand;
import ss.common.StringUtils;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetInfoOnRelatedKeywordsHandler extends
		AbstractGetterCommandHandler<GetInfoOnRelatedKeywordsCommand, InfoOnRelatedKeywordsData> {

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetInfoOnRelatedKeywordsHandler( final DialogsMainPeer peer ) {
		super(GetInfoOnRelatedKeywordsCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected InfoOnRelatedKeywordsData evaluate(GetInfoOnRelatedKeywordsCommand command) throws CommandHandleException {
		final Hashtable<String, String> hash = command.getKeywordsHash();
		if ( hash == null ) {
			return null;
		}
		final InfoOnRelatedKeywordsData data = new InfoOnRelatedKeywordsData();
		for (String uniqueId : hash.keySet()) {
			Document doc = this.peer.getXmldb().getKeywordsWithUnique(hash.get(uniqueId), uniqueId);
			Hashtable<String, String> related = new Hashtable<String, String>();
			final List elements = (doc == null) ? null : doc.getRootElement().elements("keywords");
			if (elements != null) {
				for (Object o : elements) {
					Element elem = (Element) o;
					String id = elem.attributeValue("unique_id");
					String name = elem.attributeValue("value");
					if ( StringUtils.isNotBlank(name) && StringUtils.isNotBlank(id) ) {
						related.put( id , name );
					}
				}
			}
			data.setKeywordsCountForTag( uniqueId, related.size() );
		}
		return data;
	}

}
