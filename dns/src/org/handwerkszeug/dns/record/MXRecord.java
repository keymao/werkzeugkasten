package org.handwerkszeug.dns.record;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.NullNameCompressor;
import org.handwerkszeug.dns.Type;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 3.3.9. MX RDATA format
 * 
 * <pre>
 *                                   1  1  1  1  1  1
 *     0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *    |                  PREFERENCE                   |
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 *    /                   EXCHANGE                    /
 *    /                                               /
 *    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * </pre>
 * 
 * @author taichi
 */
public class MXRecord extends AbstractRecord {

	/**
	 * A 16 bit integer which specifies the preference given to this RR among
	 * others at the same owner. Lower values are preferred.
	 */
	protected int preference;

	/**
	 * RFC 974 the name of a host.
	 */
	protected Name name;

	public MXRecord() {
		super(Type.MX);
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.preference = buffer.readUnsignedShort();
		this.name = new Name(buffer);

	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		buffer.writeShort(this.preference);
		this.name.write(buffer, NullNameCompressor.INSTANCE);
	}

}