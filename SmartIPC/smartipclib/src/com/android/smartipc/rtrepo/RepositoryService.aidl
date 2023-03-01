    package com.android.smartipc.rtrepo;

    import android.os.ParcelFileDescriptor;

    interface RepositoryService
    {
       void setFD(String repositoryName, in ParcelFileDescriptor p);
       ParcelFileDescriptor getFD(String repositoryName);
    }

