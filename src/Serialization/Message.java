package Serialization;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ctVan
 */
public final class Message {
    public int lengthMsg;
    public int encryptType;
    public int msgType;
    public String fileName;
    public byte[] data;
    private byte[] result;

    public Message() {      
    }

    public int messageSize() {
        if (msgType == MessageType.FILE) {
            return 12 + data.length + fileName.length() + 2; // 2 byte of size file name
        } else {
            return 12 + data.length;
        }
    }

    public byte[] serialize(int len) {
        lengthMsg = len - 4; // remove size of lennth msg in Message
        if (!_serialize(len)) {
            return null;
        }
        return result;
    }

    public boolean deserialize(byte[] data, int len) {
        if (data == null) {
            return false;
        }
        if (!_deserialize(data, len)) {
            return false;
        }
        return true;
    }

    public boolean _serialize(int len) {
        MBufferWrapper buf = new MBufferWrapper(len);
        // size of message
        if (!buf.writeI32(lengthMsg)) {
            return false;
        }
        // encrypt type
        if (!buf.writeI32(encryptType)) {
            return false;
        }
        // msg type
        if (!buf.writeI32(msgType)) {
            return false;
        }
        if (msgType == MessageType.FILE) {
            // callSession: 2 + ...
            if (!buf.writeStringS2(fileName)) {
                return false;
            }
        }
        // data
        if (!buf.writeRawBuf(data, data.length)) {
            return false;
        }
        // get output of serialization
        result = buf.toArrayByte();
        return buf.sizeRemain() == 0;
    }

    public boolean _deserialize(byte[] data1, int len) {
        MBufferWrapper buf = new MBufferWrapper(data1, len);
        // encrypt type
        if ((encryptType = buf.readI32()) == MBufferWrapper.ERROR_READ_INT) {
            return false;
        }
        // msg type
        if ((msgType = buf.readI32()) == MBufferWrapper.ERROR_READ_INT) {
            return false;
        }
        if (msgType == MessageType.FILE) {
            // callSession: 2 + ...
            if ((fileName = buf.readStringS2()).equals(MBufferWrapper.ERROR_READ_STRING_S2)) {
                return false;
            }
        }
        // data
        int dataLen = buf.sizeRemain();
        data = new byte[dataLen];
        if (!buf.readRawBuf(data, dataLen)) {
            return false;
        }
        return buf.sizeRemain() == 0;
    }
    public int byte2int(byte[] _size){     
        int size = 0;
        MBufferWrapper buf = new MBufferWrapper(_size, _size.length);
        if ((size = buf.readI32()) == MBufferWrapper.ERROR_READ_INT) {
            return -1;
        }
        return size;
    }
}
