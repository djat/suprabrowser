/**
 * 
 */
package ss.server.debug;

import org.dom4j.DocumentException;

import ss.common.XmlDocumentUtils;
import ss.common.debug.DumpSupraSphereCommand;
import ss.framework.networking2.CommandHandleException;
import ss.framework.networking2.RespondentCommandHandler;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;

/**
 *
 */
public class DumpSupraSphereHandler extends RespondentCommandHandler<DumpSupraSphereCommand,String>{

	/**
	 * @param acceptableCommandClass
	 */
	public DumpSupraSphereHandler() {
		super(DumpSupraSphereCommand.class);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected String evaluate(DumpSupraSphereCommand command) throws CommandHandleException {
		try {
			return XmlDocumentUtils.toPrettyString( Utils.getUtils( new XMLDB() ).getSupraSphereDocument() );
		} catch (NullPointerException ex) {
			throw new CommandHandleException( ex );
		}
	}

}
