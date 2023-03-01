	package com.android.smartipc.rtrepo;

	import android.app.Service;
	import android.content.Intent;
	import android.os.IBinder;
	import android.os.ParcelFileDescriptor;
	import android.os.RemoteException;

	import java.io.IOException;
	import java.util.HashMap;

	public class RepositoryServiceImpl extends Service
	{
		private static final HashMap<String,ParcelFileDescriptor>
							fileCatalog	= new HashMap<String,ParcelFileDescriptor>();

		private final RepositoryService.Stub mBinder = new RepositoryService.Stub()
		{
			@Override
			public void setFD(String repositoryName,
							  ParcelFileDescriptor fd) throws RemoteException
			{
				try
				{
					ParcelFileDescriptor fdd = fd.dup();
					fileCatalog.put(repositoryName, fdd);
				} catch (IOException e)
				{
					throw new RemoteException();
				}
			}

			@Override
			public ParcelFileDescriptor getFD(String repositoryName)
												throws RemoteException
			{
				try
				{
					ParcelFileDescriptor fd = fileCatalog.get(repositoryName);
					assert fd != null;
					return fd.dup();
				} catch (Exception e)
				{
					throw new RemoteException();
				}
			}
		};

		@Override
		public IBinder onBind(Intent arg0)
		{
			return mBinder;
		}
	}


