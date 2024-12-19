//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.traders.exchange.vendor.dhan;

import lombok.Getter;
public class Routes {

    public static String _wsuri = "wss://api-feed.dhan.co?version=2"
        + "&token=:token"
        + "&clientId=:clientId"
        + "&authType=2";
    public static String restUrl ="https://api.dhan.co/v2/marketfeed/quote";

}
