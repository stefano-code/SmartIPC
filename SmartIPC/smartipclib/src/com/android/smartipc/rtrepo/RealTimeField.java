package com.android.smartipc.rtrepo;

    public class RealTimeField {
        public final int ofs;
        public int type;

        public static final int Type_Long = 1;
        public static final int Type_Double = 2;
        public static final int Type_Int = 3;
        public static final int Type_Float = 4;

        public RealTimeField(int ofs, int type) {
            this.ofs = ofs;
            this.type = type;
        }
    }


/*
    public RealTimeField(Field tf) {
        this.ofs = tf.getOffset();
        this.type = tf.getType();
    }
 */
