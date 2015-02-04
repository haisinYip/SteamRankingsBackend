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
    	Long steamid =  76561197965726621L;
		String commID = "communityvisibilitystate";
		String personaname =  "Viper the Vengeful Sniper";
        System.out.println( "Hello World!" );
       
        ProfileDataExtractor pde = new ProfileDataExtractor(steamid, commID, personaname);
        try {
			pde.profile(steamid);
        	//pde.totalPlayTime(steamid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
