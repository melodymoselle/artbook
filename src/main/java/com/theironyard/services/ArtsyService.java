package com.theironyard.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.models.Token;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArtsyService {
    private static final String BASE_URL = "https://api.artsy.net/api";
    private static final String HEAD_AUTH = "X-Xapp-Token";

    @Value("${artsy.client_id}")
    private String id;

    @Value("${artsy.client_secret}")
    private String secret;

    private Token token;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    ArtistRepository artistRepo;

    @PostConstruct
    public void init(){
        token = getAccessToken();
    }

    public Artist getSaveArtistById(String artist_id){
        String url = BASE_URL + "/artists/" + artist_id;
        HttpEntity request = getRequest();
        Artist artist = restTemplate.exchange(url, HttpMethod.GET, request, Artist.class).getBody();
        artist = parseArtistImgThumb(artist);
        artist = parseArtistImgLarge(artist);
        artistRepo.save(artist);
        return artist;
    }

    public Artwork getSaveArtworkById(String artwork_id){
        String url = BASE_URL + "/artworks/" + artwork_id;
        HttpEntity request = getRequest();
        Artwork artwork = restTemplate.exchange(url, HttpMethod.GET, request, Artwork.class).getBody();
        artwork = parseArtworkImgThumb(artwork);
        artwork = parseArtworkImgLarge(artwork);
        artwork = parseArtworkImgZoom(artwork);
        artworkRepo.save(artwork);
        return artwork;
    }

    public List<Artwork> getArtworksByArtist(Artist artist){
        String url = BASE_URL + "/artworks?size=1000&artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        String json = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
        JsonNode artworksNode = null;
        try {
            artworksNode = new ObjectMapper().readTree(json).get("_embedded").get("artworks");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(artworksNode);
        List<Artwork> artworks = new ArrayList<>();
        if (artworksNode != null && artworksNode.isArray()) {
            for(JsonNode artworkNode : artworksNode){
                Artwork artwork = new Artwork();
                artwork.setArtsyArtworkId(artworkNode.findValue("id").toString());
                artwork.setTitle(artworkNode.findValue("title").toString());
                artwork.setMedium(artworkNode.findValue("medium").toString());
                artwork.setCategory(artworkNode.findValue("category").toString());
                artwork.setDate(artworkNode.findValue("date").toString());
                artwork.setSize(artworkNode.findValue("in").findValue("text").toString());
                artwork.setImgThumb(getImgThumb(artworkNode));
                artwork.setImgLarge(getImgLarge(artworkNode));
                artwork.setImgZoom(getImgZoom(artworkNode));
                artwork.setArtist(artist);
                artworks.add(artwork);
            }
        }
        return artworks;
    }

    public String getImgThumb(JsonNode artsyNode){
        String url = "";
        JsonNode imgVersions = artsyNode.findValue("image_versions");
        String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").toString();
        if (imgVersions.has("medium")) {
            url = imgBaseUrl.replace("{image_version}", "medium");
        } else if (imgVersions.has("tall")) {
            url = imgBaseUrl.replace("{image_version}", "tall");
        } else if (imgVersions.has("square")) {
            url = imgBaseUrl.replace("{image_version}", "square");
        } else if (imgVersions.has("large")) {
            url = imgBaseUrl.replace("{image_version}", "large");
        } else if (imgVersions.has("larger")) {
            url = imgBaseUrl.replace("{image_version}", "larger");
        } else {
            url = ""; // img not found thumbnail ???
        }
        return url;
    }

    public String getImgLarge(JsonNode artsyNode){
        String url = "";
        JsonNode imgVersions = artsyNode.findValue("image_versions");
        String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").toString();
        if (imgVersions.has("large")) {
            url = imgBaseUrl.replace("{image_version}", "large");
        } else if (imgVersions.has("larger")) {
            url = imgBaseUrl.replace("{image_version}", "larger");
        } else if (imgVersions.has("medium")) {
            url = imgBaseUrl.replace("{image_version}", "medium");
        } else if (imgVersions.has("tall")) {
            url = imgBaseUrl.replace("{image_version}", "tall");
        } else if (imgVersions.has("square")) {
            url = imgBaseUrl.replace("{image_version}", "square");
        } else {
            url = ""; // img not found thumbnail ???
        }
        return url;
    }

    public String getImgZoom(JsonNode artsyNode){
        String url = "";
        JsonNode imgVersions = artsyNode.findValue("image_versions");
        String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").toString();
        if (imgVersions.has("normalized")) {
            url = imgBaseUrl.replace("{image_version}", "normalized");
        } else {
            url = ""; // img not found thumbnail ???
        }
        return url;
    }

//    public Artist getSaveArtworksByArtist(Artist artist){
//        //Gets total artwork count
//        String url = BASE_URL + "/artworks?total_count=1&size=1&artist_id=" + artist.getArtsyArtistId();
//        HttpEntity request = getRequest();
//        HttpEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class);
//        int count = ((int) response.getBody().get("total_count"));
//        //Uses 'count' so that response is not paginated
//        url = BASE_URL + "/artworks?size="+count+"&artist_id=" + artist.getArtsyArtistId();
//        HashMap embedded = (HashMap)restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class).getBody().get("_embedded");
//        List<HashMap> rawArtworks = (List)embedded.get("artworks");
//        ObjectMapper mapper = new ObjectMapper();
//        for (HashMap art : rawArtworks) {
//            Artwork artwork = artworkRepo.findByArtsyArtworkId(art.get("id").toString());
//            if (artwork == null) {
//                artwork = mapper.convertValue(art, Artwork.class);
//                artwork = parseArtworkImgThumb(artwork);
//                artwork = parseArtworkImgLarge(artwork);
//                artwork = parseArtworkImgZoom(artwork);
//                artwork.setArtist(artist);
//                artworkRepo.save(artwork);
//            }
//            artist.getItems().add(artwork);
//        }
//        artist.setLoaded(true);
//        if (rawArtworks.size()>0){
//            artist.setPopulated(true);
//        }
//        artistRepo.save(artist);
//        return artist;
//    }

//    public Artist getSaveSimilarToByArtist(Artist artist){
//        String url = BASE_URL + "/artists?similar_to_artist_id=" + artist.getArtsyArtistId();
//        HttpEntity request = getRequest();
//        HashMap<String, HashMap<String, List<HashMap>>> response = restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class).getBody();
//        List<HashMap> rawArtists = response.get("_embedded").get("artists");
//        ObjectMapper mapper = new ObjectMapper();
//        for (HashMap rawArtist : rawArtists){
//            Artist similarArtist = artistRepo.findByArtsyArtistId(rawArtist.get("id").toString());
//            if (similarArtist == null){
//                similarArtist = mapper.convertValue(rawArtist, Artist.class);
//                similarArtist = parseArtistImgThumb(similarArtist);
//                similarArtist = parseArtistImgLarge(similarArtist);
//                artistRepo.save(similarArtist);
//            }
//            artist.addSimilarArtist(similarArtist);
//        }
//        artistRepo.save(artist);
//        return artist;
//    }

    public HttpEntity getRequest(){
        if (!token.isValid()){
            token = getAccessToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEAD_AUTH, token.getToken());
        return new HttpEntity(headers);
    }

    public Token getAccessToken(){
        Map<String, String> request = new HashMap<>();
        request.put("client_id", id);
        request.put("client_secret", secret);

        return restTemplate.postForObject(BASE_URL+"/tokens/xapp_token", request, Token.class);
    }

}
