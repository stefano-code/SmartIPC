package com.android.smartipc.rtrepo;

public interface Field {
    String getField();

    int getOffset();

    int getType();

    void toLog();
}
