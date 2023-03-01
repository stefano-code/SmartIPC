package com.android.smartipc.rtrepo;

import java.io.IOException;

	public interface SharedMemory
	{
		int readInt(int offset) throws IOException;
		void writeInt(int offset, int value) throws IOException;

		double readDouble(int offset) throws IOException;
		void writeDouble(int offset, double value) throws IOException;


		void close();
	}


	/*
			int readBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException;
		void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException;

		byte readByte(int offset) throws IOException;
		void writeByte(int offset, byte value) throws IOException;

		short readShort(int offset) throws IOException;
		void writeShort(int offset, short value) throws IOException;

		float readFloat(int offset) throws IOException;
		void writeFloat(int offset, float value) throws IOException;
	 */

