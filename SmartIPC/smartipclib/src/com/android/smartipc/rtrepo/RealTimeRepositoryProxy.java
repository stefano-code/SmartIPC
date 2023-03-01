    package com.android.smartipc.rtrepo;
    import android.content.Context;

    public class RealTimeRepositoryProxy
    {
        private SharedMemory shm;

        public RealTimeRepositoryProxy(SharedMemory shm)
        {
            this.shm = shm;
        }

        public RealTimeRepositoryProxy(Context ctx, String name,
                                       final RealTimeRepository.Callback callback) {
            initRealTime(ctx, name, callback);
        }
        private void initRealTime(Context ctx, String name, RealTimeRepository.Callback callback) {
            RealTimeRepository.init(ctx, new RealTimeRepository.Callback() {
                @Override
                public void onBound(boolean success) {
                    if (success)
                        try {
                            shm = RealTimeRepository.openInstance(name);
                        } catch (Exception e) {
                        }
                    if (callback != null)
                        callback.onBound(success);
                }
            });
        }
        public synchronized void set(RealTimeField field, double value)
        {
            try
            {
                if(field.type == RealTimeField.Type_Long) {
                    shm.writeInt(field.ofs, (int) value);
                }
                else {
                    shm.writeDouble(field.ofs, value);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        public synchronized double getDouble(RealTimeField field) {
            try {
                return  (field.type == RealTimeField.Type_Long) ? (double) shm.readInt(field.ofs)
                                                                    : shm.readDouble(field.ofs);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return Double.NaN;
        }
        public synchronized void set(RealTimeField field, long value) {
            try {
                if(field.type == RealTimeField.Type_Long)
                    shm.writeInt(field.ofs, (int) value);
                else
                    shm.writeDouble(field.ofs, (double) value);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        public synchronized long getLong(RealTimeField field) {
            try {
                return  (field.type == RealTimeField.Type_Long) ? shm.readInt(field.ofs) :
                                                                (long) shm.readDouble(field.ofs);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return Long.MAX_VALUE;
        }
    }

