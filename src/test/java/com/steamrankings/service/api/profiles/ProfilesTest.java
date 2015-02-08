package com.steamrankings.service.api.profiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.steamrankings.service.client.SteamIdException;
import com.steamrankings.service.client.SteamRankingsClient;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.eq;

public class ProfilesTest extends TestCase {
	

	
    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.Profiles#getSteamUser(String, String)}
     * .
     * @throws IOException 
     * @throws ParseException 
     * @throws SteamIdException 
     */
    @Test
    public void testGetSteamUser() throws ParseException, IOException, SteamIdException {
   
    	
//        WireMockRule wireMockRule = new WireMockRule(1234); // No-args constructor defaults to port 8080
//        stubFor(get(urlEqualTo("/profile?id=\"76561197965726621\""))
//                .willReturn(aResponse()
//                    .withStatus(200)
//                    .withHeader("Content-Type", "text/xml")
//                    .withBody("<response>Some content</response>")));
//        SteamRankingsClient client=new SteamRankingsClient("Test");
//        try
//        {
    	BasicHttpEntity entity=new BasicHttpEntity();
    	InputStream stream = new ByteArrayInputStream("gfnbvvvvvcvbvnbnbgnggfgf".getBytes(StandardCharsets.UTF_8));
    	entity.setContent(stream);
    	entity.setContentLength(12);
    	String data = EntityUtils.toString(entity);
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = createStrictMock(SteamRankingsClient.class);
    	expect(client.excecuteRequest("profile?id=76561197965726621"))
        .andReturn(entity);
        replay(client);

    	HttpEntity x=Profiles.getSteamUser("76561197965726621", client);
    	//System.out.println( EntityUtils.toString(x));


//        assertTrue(result.wasSuccessFul());
//
//        SteamProfile profile = new SteamProfile();
//        assertNotNull(profile);
    }

}
