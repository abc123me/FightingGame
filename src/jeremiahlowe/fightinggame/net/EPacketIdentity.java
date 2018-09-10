package jeremiahlowe.fightinggame.net;

public enum EPacketIdentity {
	CLIENT_PLAYER_DATA, 	//When a client moves, turns around, or shoots this is sent to the server
	//Client -> Server ONLY
	//Content: JSON Serialized PlayerMovementData
	
	VERSION_DATA, 			//When a client requests the version
	//Interchangeable
	//Content: Version
	
	PLAYER_REMOVE, 			//When a player is kicked or leaves, this is the FINAL say of the server
	//Server -> Client ONLY
	//Content: UUID
	
	PLAYER_ADD, 			//When a player joins, this is the FINAL say of the server 
	//Server -> Client ONLY
	//Content: JSON Serialized Player
	
	PLAYER_MOVEMENT, 		//When a player moves, this is sent by the server to the client
	//Server -> Client ONLY
	//Content: JSON Serialized PlayerMovementData
	
	PLAYER_LIST;			//When a client request a list of the players
							
}
