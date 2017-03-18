/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created on 13/12/2016, 9:00AM
 *
 * @author ctvan
 */
public class MBufferWrapper {

    private ByteBuffer buf;
    private int _sizeRemain;
    public static byte ERROR_READ_BYTE = Byte.MAX_VALUE;
    public static short ERROR_READ_SHORT = Short.MAX_VALUE;
    public static int ERROR_READ_INT = Integer.MAX_VALUE;
    public static long ERROR_READ_LONG = Long.MAX_VALUE;
    public static String ERROR_READ_STRING_S2 = "ERROR_READ_STRING_S2";

    // java.lang.Object shell size in bytes:
    public static final int LONG_FIELD_SIZE = 8;
    public static final int INT_FIELD_SIZE = 4;
    public static final int SHORT_FIELD_SIZE = 2;
    public static final int CHAR_FIELD_SIZE = 2;
    public static final int BYTE_FIELD_SIZE = 1;
    public static final int BOOLEAN_FIELD_SIZE = 1;
    public static final int DOUBLE_FIELD_SIZE = 8;
    public static final int FLOAT_FIELD_SIZE = 4;

    public MBufferWrapper(int sizeRemain) {
        this.buf = ByteBuffer.allocate(sizeRemain);
        this._sizeRemain = sizeRemain;
        // set byte order
        this.buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    public MBufferWrapper(byte[] data, int sizeRemain) {
        this.buf = ByteBuffer.wrap(data);
        this._sizeRemain = sizeRemain;
        // set byte order
        this.buf.order(ByteOrder.LITTLE_ENDIAN);
    }

    public int sizeRemain() {
        return _sizeRemain;
    }

    public byte[] toArrayByte() {
        return buf.array();
    }

//    public void consume(int len) {
//        if (len > _sizeRemain) {
//            len = (int) _sizeRemain;
//        }
//        if (len == 0) {
//            return;
//        }
//        _sizeRemain -= len;
//    }
    // write
    public boolean writeI8(byte val) {
        if (BYTE_FIELD_SIZE > _sizeRemain) {
            return false;
        }
        _sizeRemain -= BYTE_FIELD_SIZE;
        buf.put((byte) val);
        return true;
    }

    public boolean writeI16(short val) {
        if (SHORT_FIELD_SIZE > _sizeRemain) {
            return false;
        }
        _sizeRemain -= SHORT_FIELD_SIZE;
        buf.putShort((short) val);
        return true;
    }

    public boolean writeI32(int val) {
        if (INT_FIELD_SIZE > _sizeRemain) {
            return false;
        }
        _sizeRemain -= INT_FIELD_SIZE;
        buf.putInt((int) val);
        return true;
    }

    public boolean writeI64(long val) {
        if (LONG_FIELD_SIZE > _sizeRemain) {
            return false;
        }
        _sizeRemain -= LONG_FIELD_SIZE;
        buf.putLong((long) val);
        return true;
    }

    public boolean writeStringS2(String val) {
        if (val.length() + SHORT_FIELD_SIZE > _sizeRemain) {
            return false;
        }
        // write 2 byte of size
        writeI16((short)val.length());
        _sizeRemain -= val.length();
        buf.put(val.getBytes());
        return true;
    }

    public boolean writeRawBuf(byte[] rawBuf, int len) {
        if (len > _sizeRemain) {
            return false;
        }
        if (len > 0) {
            buf.put(rawBuf);
            _sizeRemain -= len;
        }
        return true;
    }

    //read
    public byte readI8() {
        if (BYTE_FIELD_SIZE > _sizeRemain) {
            return ERROR_READ_BYTE;
        }
        _sizeRemain -= BYTE_FIELD_SIZE;
        return buf.get();
    }

    public short readI16() {
        if (SHORT_FIELD_SIZE > _sizeRemain) {
            return ERROR_READ_SHORT;
        }
        _sizeRemain -= SHORT_FIELD_SIZE;
        return buf.getShort();
    }

    public int readI32() {
        if (INT_FIELD_SIZE > _sizeRemain) {
            return ERROR_READ_INT;
        }
        _sizeRemain -= INT_FIELD_SIZE;
        return buf.getInt();
    }

    public long readI64() {
        if (LONG_FIELD_SIZE > _sizeRemain) {
            return ERROR_READ_LONG;
        }
        _sizeRemain -= LONG_FIELD_SIZE;
        return buf.getLong();
    }
    
    public String readStringS2() {
        if (SHORT_FIELD_SIZE > _sizeRemain) {
            return ERROR_READ_STRING_S2;
        }
        short size = readI16();
        if(size > _sizeRemain)
            return ERROR_READ_STRING_S2;
        
        if(size > 0){
            byte []data = new byte[size];
            buf.get(data,0,size);
            _sizeRemain -=size;
            return new String(data);
        }
        return ERROR_READ_STRING_S2;       
    }

    public boolean readRawBuf(byte[] data, int len) {
        if (len > _sizeRemain) {
            return false;
        }
        if (len > 0) {
            buf.get(data, 0, len);
            _sizeRemain -= len;
        }
        return true;
    }

}
