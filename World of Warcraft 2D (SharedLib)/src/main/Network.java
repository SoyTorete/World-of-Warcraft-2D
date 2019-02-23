package main;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;

import logon.CS_CharacterCreate;
import logon.CS_CharacterDelete;
import logon.CS_CharacterList;
import logon.CS_Login;
import logon.CS_Realmlist;
import logon.SC_Character;
import logon.SC_CharacterCreate;
import logon.SC_CharacterDelete;
import logon.SC_CharacterList;
import logon.SC_Login;
import logon.SC_Realmlist;
import world.CS_Movement;
import world.CS_WorldConnection;
import world.SC_MovementUpdate;
import world.SC_World;
import world.SC_WorldConnection;
import world.SC_WorldPosition;

public class Network {

	public static void RegisterLib(Kryo kryo) {
		kryo.register(CS_Login.class);
		kryo.register(SC_Login.class);
		kryo.register(CS_Realmlist.class);
		kryo.register(SC_Realmlist.class);
		kryo.register(CS_CharacterCreate.class);
		kryo.register(SC_CharacterCreate.class);
		kryo.register(CS_CharacterList.class);
		kryo.register(SC_CharacterList.class);
		kryo.register(SC_Character.class);
		kryo.register(ArrayList.class);
		kryo.register(CS_CharacterDelete.class);
		kryo.register(SC_CharacterDelete.class);
		kryo.register(CS_WorldConnection.class);
		kryo.register(SC_WorldConnection.class);
		kryo.register(SC_WorldPosition.class);
		kryo.register(SC_World.class);
		kryo.register(CS_Movement.class);
		kryo.register(SC_MovementUpdate.class);
	}
}
