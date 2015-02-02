package com.steamrankings.service.core;

import com.steamrankings.service.core.dataextractors.ProfileDataExtractor;

/**
 * Hello world!
 *
 */
public class Application 
{
    public static void main( String[] args )
    {
    	String steamid =  "76561197965726621";
		String commID = "communityvisibilitystate";
		String personaname =  "Viper the Vengeful Sniper";
        System.out.println( "Hello World!" );
       
        ProfileDataExtractor pde = new ProfileDataExtractor(steamid, commID, personaname);
        try {
			pde.profile(steamid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
