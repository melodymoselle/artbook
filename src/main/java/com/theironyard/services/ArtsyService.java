package com.theironyard.services;

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

    public Artist getSaveArtworksByArtist(Artist artist){
        //Gets total artwork count
        String url = BASE_URL + "/artworks?total_count=1&size=1&artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        HttpEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class);
        int count = ((int) response.getBody().get("total_count"));
        //Uses 'count' so that response is not paginated
        url = BASE_URL + "/artworks?size="+count+"&artist_id=" + artist.getArtsyArtistId();
        HashMap embedded = (HashMap)restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class).getBody().get("_embedded");
        List<HashMap> rawArtworks = (List)embedded.get("artworks");
        ObjectMapper mapper = new ObjectMapper();
        for (HashMap art : rawArtworks) {
            Artwork artwork = artworkRepo.findByArtsyArtworkId(art.get("id").toString());
            if (artwork == null) {
                artwork = mapper.convertValue(art, Artwork.class);
                artwork = parseArtworkImgThumb(artwork);
                artwork = parseArtworkImgLarge(artwork);
                artwork = parseArtworkImgZoom(artwork);
                artwork.setArtist(artist);
                artworkRepo.save(artwork);
            }
            artist.addArtwork(artwork);
        }
        artist.setLoaded(true);
        if (rawArtworks.size()>0){
            artist.setPopulated(true);
        }
        artistRepo.save(artist);
        return artist;
    }

    public Artist getSaveSimilarToByArtist(Artist artist){
        String url = BASE_URL + "/artists?similar_to_artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        HashMap<String, HashMap<String, List<HashMap>>> response = restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class).getBody();
        List<HashMap> rawArtists = response.get("_embedded").get("artists");
        ObjectMapper mapper = new ObjectMapper();
        for (HashMap rawArtist : rawArtists){
            Artist similarArtist = artistRepo.findByArtsyArtistId(rawArtist.get("id").toString());
            if (similarArtist == null){
                similarArtist = mapper.convertValue(rawArtist, Artist.class);
                similarArtist = parseArtistImgThumb(similarArtist);
                similarArtist = parseArtistImgLarge(similarArtist);
                artistRepo.save(similarArtist);
            }
            artist.addSimilarArtist(similarArtist);
        }
        artistRepo.save(artist);
        return artist;
    }

    public Artist parseArtistImgThumb(Artist artist){
        if (artist.getImagesMap().get("image").get("href") != null) {
            String href = artist.getImagesMap().get("image").get("href").toString();
            List<String> versions = artist.getImageVersions();
            String url;
            if (versions.contains("four_thirds")) {
                url = href.replace("{image_version}", "four_thirds");
            } else if (versions.contains("square")) {
                url = href.replace("{image_version}", "square");
            } else if (versions.contains("tall")) {
                url = href.replace("{image_version}", "tall");
            } else if (versions.contains("large")) {
                url = href.replace("{image_version}", "large");
            } else {
                url = ""; // img not found thumbnail ???
            }
            artist.setImgThumb(url);
            artistRepo.save(artist);
        }
        return artist;
    }

    public Artist parseArtistImgLarge(Artist artist){
        if (artist.getImagesMap().get("image").get("href") != null) {
            String href = artist.getImagesMap().get("image").get("href").toString();
            List<String> versions = artist.getImageVersions();
            String url;
            if (versions.contains("large")) {
                url = href.replace("{image_version}", "large");
            } else if (versions.contains("four_thirds")) {
                url = href.replace("{image_version}", "four_thirds");
            } else if (versions.contains("square")) {
                url = href.replace("{image_version}", "square");
            } else if (versions.contains("tall")) {
                url = href.replace("{image_version}", "tall");
            } else {
                url = ""; // img not found thumbnail ???
            }
            artist.setImgLarge(url);
            artistRepo.save(artist);
        }
        return artist;
    }

    public Artwork parseArtworkImgThumb(Artwork artwork){
        if (artwork.getImagesMap().get("image").get("href") != null) {
            String href = artwork.getImagesMap().get("image").get("href").toString();
            List<String> versions = artwork.getImageVersions();
            String url;
            if (versions.contains("medium")) {
                url = href.replace("{image_version}", "medium");
            } else if (versions.contains("tall")) {
                url = href.replace("{image_version}", "tall");
            } else if (versions.contains("medium")) {
                url = href.replace("{image_version}", "medium");
            } else if (versions.contains("small")) {
                url = href.replace("{image_version}", "small");
            } else {
                url = ""; // img not found thumbnail ???
            }
            artwork.setImgThumb(url);
            artworkRepo.save(artwork);
        }
        return artwork;
    }

    public Artwork parseArtworkImgLarge(Artwork artwork){
        if (artwork.getImagesMap().get("image").get("href") != null) {
            String href = artwork.getImagesMap().get("image").get("href").toString();
            List<String> versions = artwork.getImageVersions();
            String url;
            if (versions.contains("larger")) {
                url = href.replace("{image_version}", "larger");
            } else if (versions.contains("large")) {
                url = href.replace("{image_version}", "large");
            } else if (versions.contains("large_rectangle")) {
                url = href.replace("{image_version}", "large_rectangle");
            } else if (versions.contains("square")) {
                url = href.replace("{image_version}", "square");
            } else {
                url = ""; // img not found thumbnail ???
            }
            artwork.setImgLarge(url);
            artworkRepo.save(artwork);
        }
        return artwork;
    }

    public Artwork parseArtworkImgZoom(Artwork artwork){
        if (artwork.getImagesMap().get("image").get("href") != null) {
            String href = artwork.getImagesMap().get("image").get("href").toString();
            List<String> versions = artwork.getImageVersions();
            String url;
            if (versions.contains("normalized")) {
                url = href.replace("{image_version}", "normalized");
            } else {
                url = null;
            }
            artwork.setImgZoom(url);
            artworkRepo.save(artwork);
        }
        return artwork;
    }

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
