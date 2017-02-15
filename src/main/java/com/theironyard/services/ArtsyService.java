package com.theironyard.services;

import com.fasterxml.jackson.core.type.TypeReference;
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

    @Value("${artsy_client_id}")
    private String id;

    @Value("${artsy_client_secret}")
    private String secret;

    private Token token;

    private ObjectMapper mapper = new ObjectMapper();

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

    /**
     * Gets the artist info with 'artsyArtistId' from Artsy API.
     * Converts JSON to Artist object and returns. Unsaved.
     *
     * @param artsyArtistId Id used to query API
     * @return Artist
     */
    public Artist getArtistByArtsyId(String artsyArtistId){
        String url = BASE_URL + "/artists/" + artsyArtistId;
        HttpEntity request = getRequest();
        JsonNode jsonNode = restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class).getBody();
        Artist artist = mapper.convertValue(jsonNode, Artist.class);
        artist.setImgThumb(getImgThumb(jsonNode));
        artist.setImgLarge(getImgLarge(jsonNode));
        return artist;
    }

    /**
     * Gets a list of artworks data related to 'artist.artsyArtistId' from Artsy API.
     * Converts JSON to Artwork objects and returns. Unsaved.
     *
     * @param artist object with 'artsyArtistId' used to query API
     * @return List of Artwork objects related to 'artist'
     */
    public List<Artwork> getArtworksByArtist(Artist artist){
        ObjectMapper mapper = new ObjectMapper();
        String url = BASE_URL + "/artworks?size=1000&artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        String json = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
        JsonNode artworksNode = null;
        try {
            artworksNode = mapper.readTree(json).get("_embedded").get("artworks");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Artwork> artworks = new ArrayList<>();
        if (artworksNode != null && artworksNode.size()>0) {
            artist.setPopulated(true);
            for(JsonNode artworkNode : artworksNode){
                Artwork artwork = artworkRepo.findByArtsyArtworkId(artworkNode.findValue("id").asText());
                if (artwork == null) {
                    artwork = mapper.convertValue(artworkNode, Artwork.class);
                    artwork.setSize(artworkNode.findValue("in").findValue("text").asText());
                    artwork.setImgThumb(getImgThumb(artworkNode));
                    artwork.setImgLarge(getImgLarge(artworkNode));
                    artwork.setImgZoom(getImgZoom(artworkNode));
                    artwork.setArtist(artist);
                    artworks.add(artwork);
                }
            }
        }
        return artworks;
    }

    /**
     * Gets a list of similar artists data related to 'artist.artsyArtistId' from API.
     * Converts JSON to list of Artist objects and returns. Unsaved.
     *
     * @param artist object with 'artsyArtistId' used to query API
     * @return List of Artist objects related to 'artist'
     */
    public List<Artist> getSimilarToByArtist(Artist artist){
        String url = BASE_URL + "/artists?similar_to_artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        String json = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
        JsonNode artistsNode = null;
        try {
            artistsNode = mapper.readTree(json).get("_embedded").get("artists");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Artist> similarArtists = new ArrayList<>();
        if (artistsNode != null && artistsNode.isArray()) {
            for (JsonNode artistNode : artistsNode) {
                String id = artistNode.get("id").asText();
                Artist similarArtist = artistRepo.findByArtsyArtistId(id);
                if (similarArtist == null) {
                    similarArtist = mapper.convertValue(artistNode, Artist.class);
                    similarArtist.setImgThumb(getImgThumb(artistNode));
                    similarArtist.setImgLarge(getImgLarge(artistNode));
                }
                similarArtist.getSimilarFrom().add(artist);
                similarArtists.add(similarArtist);
            }
        }
        return similarArtists;
    }

    /**
     * Takes a JsonNode from Artsy API data. Converts and returns valid url for image.
     *
     * @param artsyNode JsonNode from API data
     * @return String of image url
     */
    public String getImgThumb(JsonNode artsyNode){
        String url = "";
        if (artsyNode.findValue("image_versions") != null){
            List<String> imgVersions = mapper.convertValue(artsyNode.findValue("image_versions"), new TypeReference<List<String>>() {});
            String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").asText();
                if (imgVersions.contains("medium")) {
                    url = imgBaseUrl.replace("{image_version}", "medium");
                } else if (imgVersions.contains("tall")) {
                    url = imgBaseUrl.replace("{image_version}", "tall");
                } else if (imgVersions.contains("square")) {
                    url = imgBaseUrl.replace("{image_version}", "square");
                } else if (imgVersions.contains("large")) {
                    url = imgBaseUrl.replace("{image_version}", "large");
                } else if (imgVersions.contains("larger")) {
                    url = imgBaseUrl.replace("{image_version}", "larger");
                } else {
                    url = ""; // img not found thumbnail ???
                }
        }
        return url;
    }

    /**
     * Takes a JsonNode from Artsy API data. Converts and returns valid url for image.
     *
     * @param artsyNode JsonNode from API data
     * @return String of image url
     */
    public String getImgLarge(JsonNode artsyNode){
        String url = "";
        if (artsyNode.findValue("image_versions") != null){
            List<String> imgVersions = mapper.convertValue(artsyNode.findValue("image_versions"), new TypeReference<List<String>>() {});
            String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").asText();
                if (imgVersions.contains("large")) {
                    url = imgBaseUrl.replace("{image_version}", "large");
                } else if (imgVersions.contains("larger")) {
                    url = imgBaseUrl.replace("{image_version}", "larger");
                } else if (imgVersions.contains("medium")) {
                    url = imgBaseUrl.replace("{image_version}", "medium");
                } else if (imgVersions.contains("tall")) {
                    url = imgBaseUrl.replace("{image_version}", "tall");
                } else if (imgVersions.contains("square")) {
                    url = imgBaseUrl.replace("{image_version}", "square");
                } else {
                    url = ""; // img not found thumbnail ???
                }
        }
        return url;
    }

    /**
     * Takes a JsonNode from Artsy API data. Converts and returns valid url for image.
     *
     * @param artsyNode JsonNode from API data
     * @return String of image url
     */
    public String getImgZoom(JsonNode artsyNode){
        String url = null;
        if (artsyNode.findValue("image_versions") != null){
            List<String> imgVersions = mapper.convertValue(artsyNode.findValue("image_versions"), new TypeReference<List<String>>() {});
            String imgBaseUrl = artsyNode.findValue("_links").findValue("image").findValue("href").asText();
                if (imgVersions.contains("normalized")) {
                    url = imgBaseUrl.replace("{image_version}", "normalized");
                }
        }
        return url;
    }

    /**
     * Sets request headers with API authorization token
     *
     * @return HttpEntity to use in API call
     */
    public HttpEntity getRequest(){
        if (!token.isValid()){
            token = getAccessToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEAD_AUTH, token.getToken());
        return new HttpEntity(headers);
    }

    /**
     * Calls API with client_id and client_secret to get access token
     *
     * @return Token object
     */
    public Token getAccessToken(){
        Map<String, String> request = new HashMap<>();
        request.put("client_id", id);
        request.put("client_secret", secret);

        return restTemplate.postForObject(BASE_URL+"/tokens/xapp_token", request, Token.class);
    }

}
