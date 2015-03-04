Backend tutorial
1. Calling existing SteamAPI functions:
```Java
SteamDataExtractor steamDataExtractor = new SteamDataExtractor(new SteamApi(Initialization.CONFIG.getProperty("apikey")));
// As an example, we get the user profile
SteamProfile steamProfile = steamDataExtractor.getSteamProfile(76561198013815387);
```

2. Implementing SteamAPI functions like getSteamProfile()
    * First, figure out from the Steam Wiki what your API call is.  In this case, we'll use the GetPlayerSummaries. An example API call is http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=XXXXXXXXXX&steamids=76561197960435530
    * Run that command in your browser and see what it gives.  In our case, we get the following:
    ```JSON
    {
	"response": {
		"players": [
			{
				"steamid": "76561197960435530",
				"communityvisibilitystate": 3,
				"profilestate": 1,
				"personaname": "Robin",
				"lastlogoff": 1424949328,
				"profileurl": "http://steamcommunity.com/id/robinwalker/",
				"avatar": "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/f1/f1dd60a188883caf82d0cbfccfe6aba0af1732d4.jpg",
				"avatarmedium": "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/f1/f1dd60a188883caf82d0cbfccfe6aba0af1732d4_medium.jpg",
				"avatarfull": "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/f1/f1dd60a188883caf82d0cbfccfe6aba0af1732d4_full.jpg",
				"personastate": 0,
				"realname": "Robin Walker",
				"primaryclanid": "103582791429521412",
				"timecreated": 1063407589,
				"personastateflags": 0,
				"loccountrycode": "US",
				"locstatecode": "WA",
				"loccityid": 3961
			}
		]
		
	}
}
    ```
    * Start writing code.  Make the appropriate method headers:
    ```Java
    public SteamProfile getSteamProfile(long steamId) {};
    ```
    * Within that method, create a HashMap to store arguments Steam uses.  In our case, that's the ID64 of the user for which we're trying to get data.
    ```Java
    HashMap<String, String> parameters = new HashMap<String, String>();
    parameters.put(SteamApi.PARAMETER_STEAM_IDS, Long.toString(steamId));
    ```
    * Next, we need to figure out what interface, method and version our API call uses. In our case, from our example request we have the following:
        * Interface: ISteamUser
        * Method: GetPlayerSummaries
        * Version: 2
    The API key has to be handled when SteamApi is initialized in the code that calls your new method, so you don't worry about that here.  Moving on, we add the interface and method to the SteamApi code under INTERFACE_STEAM_USER and METHOD_GET_PLAYER_SUMMARIES in this example.  Now that that's done, we can tell the SteamApi to go get our data, feeding it the interface and method constants along with our new HashMap:
    
    ```Java
    String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_USER, SteamApi.METHOD_GET_PLAYER_SUMMARIES, SteamApi.VERSION_TWO, parameters);
    ```
    * Once that's done, we need to parse the response it gives us.  Looking at the JSON it gives us in the browser, we see that we have an array named "players" with one entry that is within an object named "response".  Luckily, we have some libraries that can help take care of that (note that if one of those objects doesn't exist, you will get an exception):
```Java
JSONObject json = new JSONObject(jsonString).getJSONObject("response").getJSONArray("players").getJSONObject(0);
```
    * The last thing to take care of is creating the object to return.  How you do this will vary, but for this example we need to create a SteamProfile object, which we fill using .getString() and .getLong() calls based on the format of the JSON we get.  We also do some .has() checks to put null in case the response doesn't have some data we're trying to get; doing .get() for something that doesn't exist is generally not a good idea.
```Java
    return new SteamProfile(Long.parseLong(json.getString("steamid")), getCommunityIdFromUrl(json.getString("profileurl")), 
    json.getString("personaname"), 
    json.has("realname") ? json.getString("realname") : null, 
    json.has("loccountrycode") ? json.getString("loccountrycode") : null,
    json.has("locstatecode") ? json.getString("locstatecode") : null,
    json.has("loccityid") ? Integer.toString(json.getInt("loccityid")) : null,
    json.has("avatarfull") ? json.getString("avatarfull") : null, 
    json.has("avatarmedium") ? json.getString("avatarmedium") : null, 
    json.has("avatar") ? json.getString("avatar") : null, json.has("lastlogoff") ? new DateTime(json.getLong("lastlogoff")) : new DateTime(0));
```
3. For the database stuff, what you need depends heavily on what you're doing but the [documentation page](http://javalite.io/documentation) is your (best) friend.  Our models are all defined in com.steamrankings.service.models; not that these are database models and not Java Objects; those are defined in the API. Each model maps directly to a database table, I recommend you get something to browse the database like HeidiSQL to see what the tables are.  Without it, you will be lost. It will probably take you a few hours to get the hang of what the library can do, but once you do it's very very helpful.  I will also point out that while it may not be obvious, One-To-Many relationships are auto detected by the library and so [this page](http://javalite.io/one_to_many_associations) applies.  As an example for what you can do, here's code I hacked together before we submitted that got the top player per game by achievement:
```Java
// Get list of games that match the game ID we are given, sorted in descending order by completion rate per game ( higher completion rate -> more achievements for that game)
LazyList<ProfilesGames> listOfGames = ProfilesGames.where("game_id = ?", param.get("game")[0]).orderBy("completion_rate desc");
// Get the first entry in that sorted list, go get the profile associated with it, get the person's name
String topPlayer = listOfGames.get(0).parent(Profile.class).getString("persona_name")
```

