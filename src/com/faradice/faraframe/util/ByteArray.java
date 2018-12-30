/*
 * Copyright (c) 2009 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */
package com.faradice.faraframe.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.stream.FileImageInputStream;

/**
 * ByteArray provides methods to read various data types outof a byte array
 * 
 * @version $Id: ByteArray.java,v 1.14 2010/12/08 09:59:30 gudmfr Exp $
 */
public class ByteArray {
	/** The initial hash value for using with DJB hashing function */
	public static final int DJB_INITIAL_HASH = 5381;

	/** The length of the Byte Array */
	public final int length;
	private byte[] bytes;
	private ByteOrder order;
	private int position;

	/**
	 * ByteArrayOutputStream that can efficiently be used to hash byte values
	 * with DJBHash
	 */
	public static class HashByteArrayOutputStream extends ByteArrayOutputStream {
		/**
		 * Construct HashByteArrayOutputStream
		 * 
		 * @param size
		 *            The size of the stream
		 */
		public HashByteArrayOutputStream(int size) {
			super(size);
		}

		/**
		 * Query for the hash value of the stream
		 * 
		 * @param initial
		 *            The initial hash value
		 * @return The hash value
		 */
		public int getHash(int initial) {
			return ByteArray.DJBHash(initial, super.buf, super.count);
		}
	}

	/**
	 * Construct ByteArray
	 * 
	 * @param bytes
	 *            The bytes to read
	 */
	public ByteArray(byte[] bytes) {
		this(bytes, ByteOrder.nativeOrder());
	}

	/**
	 * Construct ByteArray
	 * 
	 * @param bytes
	 *            The bytes to read
	 * @param byteOrder
	 *            The ByteOrder to use
	 */
	public ByteArray(byte[] bytes, ByteOrder byteOrder) {
		this.bytes = bytes;
		this.length = bytes.length;
		this.order = byteOrder;
		this.position = 0;
	}

	/**
	 * Query for the current position in the Byte Array
	 * 
	 * @return The position
	 */
	public int position() {
		return position;
	}

	/**
	 * Query for the number of bytes unread from the buffer
	 * 
	 * @return The number of bytes unread from the buffer
	 */
	public int bytesUnreadFromBuffer() {
		return position < length ? length - position : 0;
	}

	/**
	 * Query if the buffer has unread data
	 * 
	 * @param count
	 *            The number of data bytes needed
	 * @return True if the buffer contains at least count bytes unread, else
	 *         false
	 */
	public boolean hasData(int count) {
		return position + count <= length;
	}

	/**
	 * Set the current position in the ByteArray
	 * 
	 * @param pos
	 *            The position
	 */
	public void seek(int pos) {
		assert pos >= 0;
		this.position = pos;
	}

	/**
	 * Skip the specified number of bytes
	 * 
	 * @param len
	 *            The number of bytes to skip
	 */
	public void skipBytes(int len) {
		this.position += len;
		assert position >= 0;
	}

	/**
	 * Read a byte block from the buffer
	 * 
	 * @param b
	 *            The destination byte array
	 * @param offset
	 *            Offset of first element in array
	 * @param len
	 *            The length to read
	 * @return The length read
	 */
	public int read(byte[] b, int offset, int len) {
		assert position + len <= length;
		assert offset + len <= b.length;
		if (position + len > length) {
			System.out.println("ATTENTION");
		}
		if (offset + len > b.length) {
			System.out.println("DANGER");
		}
		for (int i = 0; i < len; i++) {
			b[offset + i] = bytes[position++];
		}
		return len;
	}

	/**
	 * Read the next byte
	 * 
	 * @return The byte
	 */
	public byte readByte() {
		return bytes[position++];
	}

	/**
	 * Read the next unsigned byte
	 * 
	 * @return The unsigned byte as int
	 */
	public int readUnsignedByte() {
		return readUnsignedByte(bytes, position++);
	}

	/**
	 * Read the next short
	 * 
	 * @return The short
	 */
	public short readShort() {
		short s = readShort(bytes, position, order);
		position += 2;
		return s;
	}

	/**
	 * Read the next unsigned short
	 * 
	 * @return The unsigned short as an integer
	 */
	public int readUnsignedShort() {
		int us = readUnsignedShort(bytes, position, order);
		position += 2;
		return us;
	}

	/**
	 * Read the next int
	 * 
	 * @return The int value
	 */
	public int readInt() {
		int i = readInt(bytes, position, order);
		position += 4;
		return i;
	}

	/**
	 * Read the next unsigned int
	 * 
	 * @return The unsigned int as a long
	 */
	public long readUnsignedInt() {
		long l = readUnsignedInt(bytes, position, order);
		position += 4;
		return l;
	}

	/**
	 * Read the next float
	 * 
	 * @return The float value
	 */
	public float readFloat() {
		float f = readFloat(bytes, position, order);
		position += 4;
		return f;
	}

	/**
	 * Read a block from the specified file that is equal in length as the
	 * buffer
	 * 
	 * @param file
	 *            The file to read from
	 * @param offset
	 *            The starting position of the reading
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void fill(File file, int offset) throws FileNotFoundException, IOException {
		FileImageInputStream inputFile = new FileImageInputStream(file);
		try {
			fill(inputFile, offset, length);
		} finally {
			inputFile.close();
		}
	}

	/**
	 * Read a block from the specified input stream
	 * 
	 * @param data
	 *            The stream to read from
	 * @param offset
	 *            The starting position of the reading
	 * @param len
	 *            The number of bytes to read
	 * @throws IOException
	 */
	public void fill(FileImageInputStream data, int offset, int len) throws IOException {
		fillRange(0, data, offset, len);
	}

	/**
	 * Read a block from the specified input stream that is equal in length as
	 * the buffer
	 * 
	 * @param pos
	 *            The position to start writing the block into
	 * @param data
	 *            The stream to read from
	 * @param offset
	 *            The starting position of the reading
	 * @param len
	 *            The number of bytes to read
	 * @throws IOException
	 */
	public void fillRange(int pos, FileImageInputStream data, int offset, int len) throws IOException {
		data.seek(offset);
		data.readFully(bytes, pos, len);
		position = pos;
	}

	/**
	 * Write the next int
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(int value) {
		writeInt(bytes, position, order, value);
		position += 4;
	}

	/**
	 * Write the next byte
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(byte value) {
		bytes[position++] = value;
	}

	/**
	 * Write the next short
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(short value) {
		if (order == ByteOrder.BIG_ENDIAN) {
			bytes[position++] = (byte) (value >>> 8);
			bytes[position++] = (byte) (value >>> 0);
		} else {
			bytes[position++] = (byte) (value >>> 0);
			bytes[position++] = (byte) (value >>> 8);
		}
	}

	/**
	 * Write the next short
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(char value) {
		if (order == ByteOrder.BIG_ENDIAN) {
			bytes[position++] = (byte) (value >>> 8);
			bytes[position++] = (byte) (value >>> 0);
		} else {
			bytes[position++] = (byte) (value >>> 0);
			bytes[position++] = (byte) (value >>> 8);
		}
	}

	/**
	 * Write the specified long value into the byte buffer
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(long value) {
		writeLong(bytes, position, order, value);
		position += 8;
	}

	/**
	 * Read an long from the current position in the byte array
	 * 
	 * @return The long value
	 */
	public long readLong() {
		int i1 = readInt();
		int i2 = readInt();

		if (order == ByteOrder.BIG_ENDIAN) {
			return ((long) i1 << 32) + (i2 & 0xFFFFFFFFL);
		}
		return ((long) i2 << 32) + (i1 & 0xFFFFFFFFL);
	}
	
	/**
	 * Read an long from the current position in the byte array
	 * @param buf The buffer to read from
	 * @param offset The offset into the byte array to read from
	 * @param byteorder The order of the bytes
	 * 
	 * @return The long value
	 */
	public static long readLong(byte[] buf, int offset, ByteOrder byteorder) {
		int i1 = readInt(buf, offset, byteorder);
		int i2 = readInt(buf, offset+4, byteorder);

		if (byteorder == ByteOrder.BIG_ENDIAN) {
			return ((long) i1 << 32) + (i2 & 0xFFFFFFFFL);
		}
		return ((long) i2 << 32) + (i1 & 0xFFFFFFFFL);
	}
	

	/**
	 * Write the next string
	 * 
	 * @param value
	 *            The value to write
	 */
	public void write(String value) {
		position = writeString(bytes, position, value);
	}

	/**
	 * Reads a string from a byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            Start position of the string
	 * @param length
	 *            The number of bytes to read
	 * @return The String found
	 */
	public static String readString(byte[] byteBuf, int pos, int length) {
		return new String(byteBuf, pos, length);
	}

	/**
	 * Read the next short
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @param byteOrder
	 *            The byte order to use
	 * @return The short
	 */
	public static short readShort(byte[] byteBuf, int pos, ByteOrder byteOrder) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			return (short) (((byteBuf[pos] & 0xff) << 8) | ((byteBuf[pos + 1] & 0xff) << 0));
		}
		return (short) (((byteBuf[pos + 1] & 0xff) << 8) | ((byteBuf[pos] & 0xff) << 0));
	}

	/**
	 * Read an unsigned short from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @param byteOrder
	 *            The byte order to use
	 * @return The unsigned short as an integer
	 */
	public static int readUnsignedShort(byte[] byteBuf, int pos, ByteOrder byteOrder) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			return (short) (((byteBuf[pos] & 0xff) << 8) | ((byteBuf[pos + 1] & 0xff) << 0));
		}
		return (short) (((byteBuf[pos + 1] & 0xff) << 8) | ((byteBuf[pos] & 0xff) << 0));
	}

	/**
	 * Read an int from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @param byteOrder
	 *            The byte order to use
	 * @return The int value
	 */
	public static int readInt(byte[] byteBuf, int pos, ByteOrder byteOrder) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			return (((byteBuf[pos] & 0xff) << 24) | ((byteBuf[pos + 1] & 0xff) << 16) | ((byteBuf[pos + 2] & 0xff) << 8) | ((byteBuf[pos + 3] & 0xff) << 0));
		}
		return (((byteBuf[pos + 3] & 0xff) << 24) | ((byteBuf[pos + 2] & 0xff) << 16) | ((byteBuf[pos + 1] & 0xff) << 8) | ((byteBuf[pos + 0] & 0xff) << 0));
	}

	/**
	 * Read an unsigned int from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @param byteOrder
	 *            The byte order to use
	 * @return The unsigned int as a long
	 */
	public static long readUnsignedInt(byte[] byteBuf, int pos, ByteOrder byteOrder) {
		return (readInt(byteBuf, pos, byteOrder)) & 0xffffffffL;
	}

	/**
	 * Read an unsigned byte from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @return The unsigned byte
	 */
	public static int readUnsignedByte(byte[] byteBuf, int pos) {
		return byteBuf[pos] & 0xFF;
	}

	/**
	 * Read a byte from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @return The byte
	 */
	public static int readByte(byte[] byteBuf, int pos) {
		return byteBuf[pos];
	}

	/**
	 * Read a float from the specified position in the byte array
	 * 
	 * @param byteBuf
	 *            The byte array
	 * @param pos
	 *            The position to read from
	 * @param byteOrder
	 *            The byte order to use
	 * @return The float value
	 */
	public static float readFloat(byte[] byteBuf, int pos, ByteOrder byteOrder) {
		return Float.intBitsToFloat(readInt(byteBuf, pos, byteOrder));
	}

	/**
	 * Write a string into the byte array, using just one byte of the char value
	 * (i.e. support only ASCII). The string will be zero terminated.
	 * 
	 * @param byteBuf
	 *            The byte array, must contain enough space from index pos for
	 *            the string content and terminating byte.
	 * @param pos
	 *            The index into the byte array to begin write the string
	 * @param s
	 *            The string to write into the byte array
	 * @return The next position in the array to write data
	 */
	public static int writeString(byte[] byteBuf, int pos, String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			byteBuf[pos + i] = (byte) s.charAt(i);
		}
		byteBuf[pos + len] = 0;
		return pos + len + 1;
	}

	/**
	 * Write a string into the byte array, using just one byte of the char value
	 * (i.e. support only ASCII). Since no termination byte is written, it is
	 * assumed that strings written are of fixed length.
	 * 
	 * @param byteBuf
	 *            The byte array, must contain enough space from index pos for
	 *            the string content.
	 * @param pos
	 *            The index into the byte array to begin write the string
	 * @param s
	 *            The string to write into the byte array
	 * @return The next position in the array to write data
	 */
	public static int writeFixedString(byte[] byteBuf, int pos, String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			byteBuf[pos + i] = (byte) s.charAt(i);
		}
		return pos + len;
	}

	/**
	 * Write the specified integer value into the byte buffer
	 * 
	 * @param byteBuf
	 *            The byte buffer
	 * @param pos
	 *            The position to write to
	 * @param byteOrder
	 *            The ByteOrder to use
	 * @param v
	 *            The value to write
	 */
	public static void writeInt(byte[] byteBuf, int pos, ByteOrder byteOrder, int v) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			byteBuf[pos] = (byte) (v >>> 24);
			byteBuf[pos + 1] = (byte) (v >>> 16);
			byteBuf[pos + 2] = (byte) (v >>> 8);
			byteBuf[pos + 3] = (byte) (v >>> 0);
		} else {
			byteBuf[pos] = (byte) (v >>> 0);
			byteBuf[pos + 1] = (byte) (v >>> 8);
			byteBuf[pos + 2] = (byte) (v >>> 16);
			byteBuf[pos + 3] = (byte) (v >>> 24);
		}
	}
	
	/**
	 * Write the specified short value into the buffer
	 * @param byteBuf
	 * @param pos
	 * @param byteOrder
	 * @param v
	 */
	public static void writeShort(byte[] byteBuf, int pos, ByteOrder byteOrder, short v) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			byteBuf[pos] = (byte) (v >>> 8);
			byteBuf[pos + 1] = (byte) (v >>> 0);
		} else {
			byteBuf[pos] = (byte) (v >>> 0);
			byteBuf[pos + 1] = (byte) (v >>> 8);
		}
	}


	/**
	 * Write the specified long value into the byte buffer
	 * 
	 * @param byteBuf
	 *            The byte buffer
	 * @param pos
	 *            The position to write to
	 * @param byteOrder
	 *            The ByteOrder to use
	 * @param value
	 *            The value to write
	 */
	public static void writeLong(byte[] byteBuf, int pos, ByteOrder byteOrder, long value) {
		if (byteOrder == ByteOrder.BIG_ENDIAN) {
			byteBuf[pos++] = (byte) (value >>> 56);
			byteBuf[pos++] = (byte) (value >>> 48);
			byteBuf[pos++] = (byte) (value >>> 40);
			byteBuf[pos++] = (byte) (value >>> 32);
			byteBuf[pos++] = (byte) (value >>> 24);
			byteBuf[pos++] = (byte) (value >>> 16);
			byteBuf[pos++] = (byte) (value >>> 8);
			byteBuf[pos++] = (byte) (value >>> 0);
		} else {
			byteBuf[pos++] = (byte) (value >>> 0);
			byteBuf[pos++] = (byte) (value >>> 8);
			byteBuf[pos++] = (byte) (value >>> 16);
			byteBuf[pos++] = (byte) (value >>> 24);
			byteBuf[pos++] = (byte) (value >>> 32);
			byteBuf[pos++] = (byte) (value >>> 40);
			byteBuf[pos++] = (byte) (value >>> 48);
			byteBuf[pos++] = (byte) (value >>> 56);
		}
	}

	/**
	 * Write the specified float value into the byte buffer
	 * 
	 * @param byteBuf
	 *            The byte buffer
	 * @param pos
	 *            The position to write to
	 * @param byteOrder
	 *            The ByteOrder to use
	 * @param v
	 *            The value to write
	 */
	public static void writeFloat(byte[] byteBuf, int pos, ByteOrder byteOrder, float v) {
		writeInt(byteBuf, pos, byteOrder, Float.floatToIntBits(v));
	}

	/**
	 * Write the specified float value into the byte buffer
	 * 
	 * @param byteBuf
	 *            The byte buffer
	 * @param pos
	 *            The position to write to
	 * @param byteOrder
	 *            The ByteOrder to use
	 * @param v
	 *            The value to write
	 */
	public static void writeDouble(byte[] byteBuf, int pos, ByteOrder byteOrder, double v) {
		writeLong(byteBuf, pos, byteOrder, Double.doubleToLongBits(v));
	}

	/**
	 * Write the specified byte value into the byte buffer
	 * 
	 * @param byteBuf
	 *            The byte buffer
	 * @param pos
	 *            The position to write to
	 * @param v
	 *            The value to write
	 */
	public static void writeByte(byte[] byteBuf, int pos, byte v) {
		byteBuf[pos] = v;
	}

	/**
	 * Create a 32 bit hash from the specified byte array
	 * 
	 * @param bytes
	 *            The bytes to hash
	 * @return The hexadecimal string representation of the hash
	 */
	public static String DJBHash(byte[] bytes) {
		return Integer.toHexString(DJBHash(DJB_INITIAL_HASH, bytes, bytes.length));
	}

	/**
	 * Create a 32 bit hash from the specified byte array starting with the
	 * specified hash
	 * 
	 * @param hash
	 *            The initial hash to start from
	 * @param bytes
	 *            The bytes to add to the hash
	 * @param len
	 *            The length to read from the byte array
	 * @return The new hash value
	 */
	public static int DJBHash(int hash, byte[] bytes, int len) {
		for (int i = 0; i < len; i++) {
			hash = ((hash << 5) + hash) + bytes[i];
		}
		return hash;
	}
}