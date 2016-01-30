package com.mingJiang.as;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteArray {

	protected ByteBuffer bb;
	public int pos;
	public int size;
	public boolean createFromArray;

	public ByteArray(int size) {
		this(size, ByteOrder.LITTLE_ENDIAN);
	}

	public ByteArray(int size, ByteOrder order) {
		this.size = size;
		this.pos = 0;
		bb = ByteBuffer.allocate(size);
		bb.order(order);
		createFromArray = false;
	}

	public ByteArray(byte[] wrap) {
		this(wrap,ByteOrder.LITTLE_ENDIAN);
	}

	public ByteArray(byte[] wrap, ByteOrder order) {
		this.size = wrap.length;
		this.pos = size;
		bb = ByteBuffer.wrap(wrap);
		createFromArray = true;
		pos = wrap.length;
		bb.order(order);
	}

	public byte[] data() {
		// System.out.println("pos: "+bb.position()+" "+pos+"  limit: "+bb.capacity());
		byte[] data = new byte[createFromArray ? pos : bb.position()];
		System.arraycopy(bb.array(), 0, data, 0,
				createFromArray ? pos : bb.position());
		return data;
	}

	public byte[] getData() {
		System.out.println("length: " + bb.array().length);

		byte[] data = new byte[bb.array().length];
		System.arraycopy(bb.array(), 0, data, 0, bb.array().length);
		return data;
	}

	public boolean readBoolean() {
		return readByte() == 1;
	}

	public byte readByte() {
		return bb.get();
	}

	public void readBytes(byte[] tmp, int i, int _loc_3) {
		bb.get(tmp, i, _loc_3);
	}

	public void readBytes(byte[] tmp) {
		bb.get(tmp);
	}

	public double readDouble() {
		return bb.getDouble();
	}

	public float readFloat() {
		return bb.getFloat();
	}

	public int readInt() {
		return bb.getInt();
	}

	// readMultiByte(length:uint, charSet:String):String

	// readObject():*

	public short readShort() {
		return bb.getShort();
	}

	public int readUnsignedByte() {
		return Byte.toUnsignedInt(readByte());
	}

	public long readUnsignedInt() {
		return Integer.toUnsignedLong(readInt());
	}

	public int readUnsignedShort() {
		return Short.toUnsignedInt(readShort());
	}

	public String readUTF(){
		int utflen = readUnsignedShort();
		byte[] bytearr = new byte[utflen];
		readBytes(bytearr, 0, utflen);
		return new String(bytearr);
	}

	public long readLong() {
		return bb.getLong();
	}

	public void writeBoolean(boolean val) {
		bb.put((byte) (val ? 1 : 0));
	}

	public void writeByte(int val) {
		bb.put((byte) val);
	}

	public void writeBytes(byte[] val, int offset, int length) {
		bb.put(val, offset, length);
	}

	// writeMultiByte(value:String, charSet:String):void

	// writeObject(object:*):void

	// writeUTFBytes(value:String):void
	public void writeDouble(double val) {
		bb.putDouble(val);
	}

	public void writeInt(int val) {
		bb.putInt(val);
	}

	public void writeShort(int val) {
		bb.putShort((short) val);
	}

	public void writeUnsignedInt(long val) {
		writeInt((int) val);
	}

	public void writeUTF(String str){
        byte[] data = str.getBytes();
        writeShort(data.length);
        writeBytes(data, 0, data.length);
	}

	public void writeUTFBytes(String val) {
		bb.put(val.getBytes());
	}

	public void writeFloat(float val) {
		bb.putFloat(val);
	}

	public int remaining() {
		return bb.remaining();
	}

	public int position() {
		return bb.position();
	}

	public int setPosition(int pos) {
		int old = bb.position();
		bb.position(pos);
		return old;
	}

}
