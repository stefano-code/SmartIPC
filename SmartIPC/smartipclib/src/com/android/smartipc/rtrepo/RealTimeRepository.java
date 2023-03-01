	package com.android.smartipc.rtrepo;

	import android.content.ComponentName;
	import android.content.Context;
	import android.content.Intent;
	import android.content.ServiceConnection;
	import android.content.pm.PackageManager;
	import android.content.pm.ResolveInfo;
	import android.os.IBinder;
	import android.os.ParcelFileDescriptor;
	import android.os.RemoteException;
	import android.util.Log;

	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.List;

	public class RealTimeRepository implements SharedMemory {
		static {
			try	{
				System.loadLibrary("SystemLib");
			} catch (Exception e) {
				Log.e("MemoryFile", "error in static initializer");
			}
		}

		private final static String TAG = "SharedMemory";
		private static RepositoryService sharedMemService;
		private static boolean bound;
		private static final ArrayList<Callback> callback = new  ArrayList<Callback>();

		public interface Callback {
			void onBound(boolean success);
		}

		public static void init(Context ctx, Callback cb) {
			synchronized (callback)	{
				if (!bound)	{
					boolean firstCall = callback.isEmpty();
					callback.add(cb);
					if (firstCall) {
						Intent i = new Intent(RepositoryServiceImpl.class.getName());
						Log.e("TEST_SYSTEMLIB","" +RepositoryServiceImpl.class.getName());

						boolean willBind = ctx.bindService(createExplicitFromImplicitIntent(ctx, i),
												serviceConnection, Context.BIND_AUTO_CREATE);
						if (!willBind) {
							callback.clear();
							cb.onBound(false);
						}
					}
				} else
					cb.onBound(true);
			}
		}

		public static void tearDown(Context ctx) {
			try	{
				ctx.unbindService(serviceConnection);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		public static SharedMemory createInstance(String name, int length) throws Exception {
			return new RealTimeRepository(name, length);
		}

		public static SharedMemory openInstance(String name) throws Exception {
			return new RealTimeRepository(name);
		}

		private final static ServiceConnection serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				synchronized (callback) {
					sharedMemService = RepositoryService.Stub.asInterface(service);
					bound = true;
					for (Callback cb : callback) {
						cb.onBound(true);
					}
					callback.clear();
					Log.e(TAG, "onServiceConnected");
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {Log.e(TAG, "onServiceDisconnected");}
		};

		// mmap(2) protection flags from <sys/mman.h>
		private static final int PROT_READ = 0x1;
		private static final int PROT_WRITE = 0x2;

		private native int open(String name, int length);

		// returns memory address for ashmem region
		private native long mmap(int fd, int length, int mode);

		private native void close(int fd);

		private native void write(int fd, long address, byte[] buffer, int srcOffset, int destOffset,
																		int count, boolean isUnpinned);

		private native int readInt(long address, int offset);
		private native void writeInt(long address, int offset, int value);

		private native double readDouble(long address, int offset);
		private native int writeDouble(long address, int offset, double value);

		private native int getSize(int fd);

		private int mFD; // ashmem file descriptor
		private long mAddress; // address of ashmem memory
		private int mLength; // total length of our ashmem region

		RealTimeRepository(String repositoryName) throws RemoteException {
			ParcelFileDescriptor pfd = sharedMemService.getFD(repositoryName);
			mFD = pfd.getFd();
			mLength = getSize(mFD);
			mAddress = mmap(mFD, mLength, PROT_READ | PROT_WRITE);
		}

		RealTimeRepository(String repositoryName, int length) throws Exception {
			mLength = length;
			if (length > 0)	{
				mFD = open(repositoryName, length);
			} else {
				throw new IOException("Invalid length: " + length);
			}

			mAddress = mmap(mFD, length, PROT_READ | PROT_WRITE);
			byte[] zeroes = new byte[length];
			writeBytes(zeroes, 0, 0, length);
			sharedMemService.setFD(repositoryName, ParcelFileDescriptor.fromFd(mFD));
		}

		@Override
		public void close()	{
			close(mFD);
		}

		@Override
		protected void finalize()
		{
			close();
		}

		ParcelFileDescriptor getFD() throws IOException	{
			return ParcelFileDescriptor.fromFd(mFD);
		}

		@Override
		public int readInt(int offset) throws IOException {
			if (offset < 0 || offset > mLength - 4) throw new IndexOutOfBoundsException();
			return readInt(mAddress, offset);
		}

		@Override
		public void writeInt(int offset, int value) throws IOException {
			if (offset < 0 || offset > mLength - 4)	throw new IndexOutOfBoundsException();
			writeInt(mAddress, offset, value);
		}

		@Override
		public double readDouble(int offset) throws IOException {
			if (offset < 0 || offset > mLength - 8)	throw new IndexOutOfBoundsException();
			return readDouble(mAddress, offset);
		}

		@Override
		public void writeDouble(int offset, double value) throws IOException {
			if (offset < 0 || offset > mLength - 8)	throw new IndexOutOfBoundsException();
			writeDouble(mAddress, offset, value);
		}

		private void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count)
																	throws IOException {
			if (srcOffset<0 || srcOffset>buffer.length || count<0 || count > buffer.length-srcOffset
					|| destOffset < 0 || destOffset > mLength || count > mLength - destOffset)
				throw new IndexOutOfBoundsException();
			write(mFD, mAddress, buffer, srcOffset, destOffset, count, false);
		}

		private static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
			//Retrieve all services that can match the given intent
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

			//Make sure only one match was found
			if (resolveInfo == null || resolveInfo.size() != 1)
				return null;

			//Get component info and create ComponentName
			ResolveInfo serviceInfo = resolveInfo.get(0);
			String packageName = serviceInfo.serviceInfo.packageName;
			String className = serviceInfo.serviceInfo.name;
			ComponentName component = new ComponentName(packageName, className);

			//Create a new intent. Use the old one for extras and such reuse
			Intent explicitIntent = new Intent(implicitIntent);

			//Set the component to be explicit
			explicitIntent.setComponent(component);

			return explicitIntent;
		}
	}

	/*

		private native int read(int fd, long address, byte[] buffer, int srcOffset, int destOffset,
																		int count, boolean isUnpinned);
		private native byte readByte(long address, int offset);
		private native void writeByte(long address, int offset, byte value);

		private native short readShort(long address, int offset);
		private native void writeShort(long address, int offset, short value);

		private native float readFloat(long address, int offset);
		private native void writeFloat(long address, int offset, float value);

		@Override
		public int readBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't read from deactivated memory file.");
			}
			if (destOffset < 0 || destOffset > buffer.length || count < 0 || count > buffer.length - destOffset
										|| srcOffset < 0 || srcOffset > mLength || count > mLength - srcOffset)
			{
				throw new IndexOutOfBoundsException();
			}
			return read(mFD, mAddress, buffer, srcOffset, destOffset, count, false);
		}

		@Override
		public byte readByte(int offset) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't read from deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 1)
			{
				throw new IndexOutOfBoundsException();
			}
			return readByte(mAddress, offset);
		}

		@Override
		public void writeByte(int offset, byte value) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't write to deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 1)
			{
				throw new IndexOutOfBoundsException();
			}
			writeByte(mAddress, offset, value);
		}

		@Override
		public short readShort(int offset) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't read from deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 2)
			{
				throw new IndexOutOfBoundsException();
			}
			return readShort(mAddress, offset);
		}

		@Override
		public void writeShort(int offset, short value) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't write to deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 2)
			{
				throw new IndexOutOfBoundsException();
			}
			writeShort(mAddress, offset, value);
		}

		@Override
		public float readFloat(int offset) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't read from deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 2)
			{
				throw new IndexOutOfBoundsException();
			}
			return readFloat(mAddress, offset);
		}

		@Override
		public void writeFloat(int offset, float value) throws IOException
		{
			if (isDeactivated())
			{
				throw new IOException("Can't write to deactivated memory file.");
			}
			if (offset < 0 || offset > mLength - 2)
			{
				throw new IndexOutOfBoundsException();
			}
			writeFloat(mAddress, offset, value);
		}


		private native void munmap(long addr, int length);

		private void deactivate() {
			if (!isDeactivated()) {
				munmap(mAddress, mLength);
				mAddress = 0;
			}
		}

		private boolean isDeactivated()	{
			return mAddress == 0;
		}
	 */
