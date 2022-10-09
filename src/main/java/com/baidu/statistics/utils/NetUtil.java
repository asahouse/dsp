package com.baidu.statistics.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by benjaminkc on 16/12/13.
 */
public class NetUtil {
    /**
     * @param args
     * @throws UnknownHostException
     * @throws SocketException
     */
    public static void main(String[] args) throws UnknownHostException, SocketException {
        InetAddress ia = InetAddress.getLocalHost();
        System.out.println(ia);
        System.out.println(getLocalMac());
    }

    public static String getLocalMac() {
        byte[] mac = new byte[0];
        try {
            InetAddress ia = InetAddress.getLocalHost();

            //获取网卡，获取地址
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer("");
        for(int i=0; i<mac.length; i++) {
            if(i!=0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i]&0xff;
            String str = Integer.toHexString(temp);

            if(str.length()==1) {
                sb.append("0"+str);
            }else {
                sb.append(str);
            }
        }

        return sb.toString().toUpperCase();
    }
}
