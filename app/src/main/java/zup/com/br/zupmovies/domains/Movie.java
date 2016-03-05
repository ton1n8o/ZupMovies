package zup.com.br.zupmovies.domains;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
@Table(name = Movie.TABLE_NAME)
public class Movie extends Model {

    public static final String TABLE_NAME = "movie";

    @Column
    private String title;
    @Column
    private String year;
    @Column
    private String rated;
    @Column
    private String released;
    @Column
    private String runtime;
    @Column
    private String genre;
    @Column
    private String director;
    @Column
    private String writer;
    @Column
    private String actors;
    @Column
    private String plot;
    @Column
    private String language;
    @Column
    private String country;
    @Column
    private String awards;
    @Column
    private String poster;
    @Column
    private byte[] posterData;
    @Column
    private String metascore;
    @Column
    @SerializedName("imdbRating")
    private String imdbRating;
    @Column
    private String imdbVotes;
    @Column
    @SerializedName("imdbID")
    private String imdbID;
    @Column
    private String type;
    @Column
    private String response;

    /*Getters and Setters*/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public byte[] getPosterData() {
        return posterData;
    }

    public void setPosterData(byte[] posterData) {
        this.posterData = posterData;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void customSave(@NonNull BitmapDrawable bd) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgByte = baos.toByteArray();
        this.setPosterData(imgByte);
        this.save();
    }

    /**
     * Find movie by its IMDB id.
     * @param imdbID
     * @return Movie or null.
     */
    public static Movie findByImdbId(String imdbID) {
        return new Select().from(Movie.class).where(" imdbID = ? ", imdbID).executeSingle();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", rated='" + rated + '\'' +
                ", released='" + released + '\'' +
                ", runtime='" + runtime + '\'' +
                ", genre='" + genre + '\'' +
                ", director='" + director + '\'' +
                ", writer='" + writer + '\'' +
                ", actors='" + actors + '\'' +
                ", plot='" + plot + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", awards='" + awards + '\'' +
                ", poster='" + poster + '\'' +
                ", metascore='" + metascore + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                ", imdbVotes='" + imdbVotes + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", type='" + type + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
