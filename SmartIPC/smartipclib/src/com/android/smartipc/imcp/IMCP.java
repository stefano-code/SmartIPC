package com.android.smartipc.imcp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

	public class IMCP extends ContentProvider
	{
		protected final HashMap<String, Object> db = new HashMap<>();

		private  String[] fullProjection;
		protected void invalidateFullProjection()
		{
			fullProjection = null;
		}


		@Override
		public boolean onCreate()
		{
			return true;
		}

		@Override
		public String getType(Uri uri)
		{
			return null;
		}

		@Override
		public Uri insert(Uri uri, ContentValues values)
		{
			Set<Entry<String, Object>> s = values.valueSet();
			for (Entry<String, Object> e : s)
			{
				if( ! db.containsKey( e.getKey() ))
					invalidateFullProjection();
				db.put(e.getKey(), e.getValue());
			}
			getContext().getContentResolver().notifyChange(uri, null, false);
			return null;
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
							String[] selectionArgs, String sortOrder)
		{
			if (projection == null)
			{
				if( fullProjection ==  null )
				{
					Set<String> keys = db.keySet();
					String[] keyArray = new String[keys.size()];
					fullProjection = keys.toArray(keyArray);
				}
				projection = fullProjection;
			}

			final MatrixCursor c = new MatrixCursor(projection);
			int n = projection.length;
			Object[] values = new Object[n];
			int i = 0;
			for (String s : projection)
			{
				values[i++] = db.get(s);
			}
			c.addRow(values);
			return c;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
		{
			insert(uri, values);
			return 1;
		}

		@Override
		public synchronized int delete(Uri uri, String selection, String[] selectionArgs)
		{
			return 0;
		}
	}

