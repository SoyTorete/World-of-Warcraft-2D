package main;

import com.esotericsoftware.kryo.Kryo;

import logon.CS_Login;
import logon.CS_Realmlist;
import logon.SC_Login;
import logon.SC_Realmlist;

public class Network {

	public static void RegisterLib(Kryo kryo) {
		kryo.register(CS_Login.class);
		kryo.register(SC_Login.class);
		kryo.register(CS_Realmlist.class);
		kryo.register(SC_Realmlist.class);
	}
}
