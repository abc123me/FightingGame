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
	
	PLAYER_HEALTH,			//When a player is shot this gets sent to ALL clients
	//Server -> Client ONLY
	//Content: JSON Serialized HealthData
	
	PLAYER_POSITIONS,		
	//Client -> Server		//If the server get it as a request it sends back all player positions to the client
	//Content: PlayerPos Vector
	PLAYER_POSITION,
	//Server -> Client		//The client uses this to make sure all its positions are correct
	
	CLIENT_KICK,			//When a player is kicked this is sent to them telling them why
	//Server -> Client ONLY
	//Content: Reason they got kicked
	
	CHAT_MESSAGE,			//When a player or the server types in chat
	//Server -> Client
	//Content: Serialized ChatMessage
	//Client -> Server
	//Content: Raw chat message
	
	CLIENT_NAME,			//Updates the servers name for the client
	//Client -> Server
	//Content: Client's name
	
	NAME_UPDATE,			//Updates a client's name to all clients
	//Server -> Clients
	//Content: Client UUID and name 
	
	PLAYER_LIST;			//When a client request a list of the players
							
}
