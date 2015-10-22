package cn.edu.zzti.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Main {

    private static final int PORT = 80;

    private static final String HOST = "61.163.70.228";

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(PORT);
        System.out.println("Server is running!");
        while (true) {
            Socket client = socket.accept();
            System.out.println("Accepted!");
            new HandlerThread(client);
        }
    }

    private static void testGET(InputStream in, OutputStream out) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(HOST, 80));

        OutputStream outputStream = socket.getOutputStream();
        try {
            int c;
            while ((c = in.read()) != -1) {
                outputStream.write(c);
            }
        } catch (SocketTimeoutException e) {
        }
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();
        Header header = new Header();
        header.readHeader(inputStream);
        String len = header.getValue("Content-Length");
        if (len == null || len.length() == 0) return;

        byte[] content = new byte[Integer.valueOf(len)];
        inputStream.read(content);

        out.write(mergeBytes(header.getHeader(), content));
        out.flush();

        inputStream.close();
        outputStream.close();
        socket.close();
    }

    private static byte[] mergeBytes(byte[] bytes1, byte[] bytes2) {
        int i = 0;
        byte[] bytes = new byte[bytes1.length + bytes2.length];
        for (byte b : bytes1) bytes[i++] = b;
        for (byte b : bytes2) bytes[i++] = b;
        return bytes;
    }

    private static class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                socket.setSoTimeout(10000);

                testGET(in, out);

                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println(e);
                    }
                }
            }
        }
    }

}
