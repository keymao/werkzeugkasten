package org.handwerkszeug.dns.record;

import org.handwerkszeug.dns.Name;
import org.handwerkszeug.dns.NameCompressor;
import org.handwerkszeug.dns.Type;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <ul>
 * <li>3.3.1. CNAME RDATA format</li>
 * <li>3.3.11. NS RDATA format</li>
 * <li>3.3.12. PTR RDATA format</li>
 * </ul>
 * 
 * 
 * @author taichi
 */
public class SingleNameRecord extends AbstractRecord {

	protected Name oneName;

	public SingleNameRecord(Type type) {
		super(type);
	}

	@Override
	protected void parseRDATA(ChannelBuffer buffer) {
		this.oneName = new Name(buffer);
	}

	@Override
	protected void writeRDATA(ChannelBuffer buffer, NameCompressor compressor) {
		this.oneName.write(buffer, compressor);
	}

	public Name oneName() {
		return this.oneName;
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		stb.append(super.toString());
		stb.append(' ');
		stb.append(this.oneName());
		return stb.toString();
	}
}
