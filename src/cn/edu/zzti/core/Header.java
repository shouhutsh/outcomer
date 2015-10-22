package cn.edu.zzti.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ae-mp02 on 2015/10/22.
 */
public class Header {

    private byte[] header;

    public byte[] getHeader() {
        return header;
    }

    private Map<String, String> map;

    public String getValue(String key){
        if(map == null) map = parse();
        return map.get(key);
    }

    private Map<String, String> parse(){
        Map<String, String> map = new HashMap<>();
        if(header != null) {
            int count = 1;
            for (String line : new String(header).split("\r\n")) {
                if (count++ == 1){
                    if(line.contains("200")) continue;
                    else break;
                }
                int split = line.indexOf(':');
                map.put(line.substring(0, split).trim(), line.substring(split + 1, line.length()).trim());
            }
        }
        return map;
    }

    public void readHeader(InputStream input) throws IOException {
        Vector<Byte> bytes = new Vector<>();
        byte[] line;
        do {
            line = turnBytes(readLine(input));
            for(byte b : line) bytes.add(b);
        }while (line.length != 2);      // line is '\r\n', end of header;
        header = turnBytes(bytes);
    }

    private static Vector<Byte> readLine(InputStream input) throws IOException {
        Vector<Byte> bytes = new Vector<>();
        int c;
        do{
            c = input.read();
            bytes.add((byte) c);
        }while(c != '\n');
        return bytes;
    }

    private static byte[] turnBytes(Vector<? extends Byte> bytes) {
        byte[] temp = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); ++i){
            temp[i] = bytes.get(i);
        }
        return temp;
    }
}
