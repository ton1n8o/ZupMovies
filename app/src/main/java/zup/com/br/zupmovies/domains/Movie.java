package zup.com.br.zupmovies.domains;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/3/16.
 */
@Table(name = Movie.TABLE_NAME)
public class Movie extends Model implements Parcelable {

    /*Constants*/

    public static final String TABLE_NAME = "movie";

    public static final int SORT_BY_NAME_DESC = 0;
    public static final int SORT_BY_NAME_ASC = 1;
    public static final int SORT_BY_YEAR_DESC = 2;
    public static final int SORT_BY_YEAR_ASC = 3;
    public static final int SORT_BY_DATE_DESC = 4;
    public static final int SORT_BY_DATE_ASC = 5;

    public Movie() {
    }

    @Column
    private String title;
    @Column
    private String year;
    @Column
    private String director;
    @Column
    private String actors;
    @Column
    private String plot;
    @Column
    private String genre;
    @Column
    private String poster;
    @Column
    private byte[] posterData;
    @Column
    @SerializedName("imdbRating")
    private String imdbRating;
    @Column
    @SerializedName("imdbID")
    private String imdbID;
    @Column
    private String response;
    @Column
    private Date created;

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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void customSave(@NonNull BitmapDrawable bd) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgByte = baos.toByteArray();
        this.setPosterData(imgByte);
        this.save();
    }

    /* Parcelable */

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(year);
        out.writeString(director);
        out.writeString(actors);
        out.writeString(plot);
        out.writeString(genre);
        out.writeString(poster);
        out.writeByteArray(posterData);
        out.writeString(imdbRating);
        out.writeString(imdbID);
        out.writeString(response);
        out.writeSerializable(created);
    }

    private Movie(Parcel in) {
        title = in.readString();
        year = in.readString();
        director = in.readString();
        actors = in.readString();
        plot = in.readString();
        genre = in.readString();
        poster = in.readString();
        posterData = new byte[in.readInt()];
        in.readByteArray(posterData);
        imdbRating = in.readString();
        response = in.readString();
        created = (Date) in.readSerializable();
    }

    /**
     * Find movie by its IMDB id.
     *
     * @param imdbID
     * @return Movie or null.
     */
    public static Movie findByImdbId(String imdbID) {
        return new Select().from(Movie.class).where(" imdbID = ? ", imdbID).executeSingle();
    }

    /**
     * Find all stored movies.
     *
     * @param sort Movie.SORT_BY_NAME_DESC, Movie.SORT_BY_YEAR, Movie.SORT_BY_DATE,
     *             Movie.SORT_BY_SCORE_ASC, Movie.SORT_BY_SCORE_DESC
     * @return List<Movies>
     */
    public static List<Movie> findAll(int sort) {

        String sortBy = " title DESC ";
        switch (sort) {
            case SORT_BY_NAME_DESC:
                sortBy = " title DESC ";
                break;
            case SORT_BY_NAME_ASC:
                sortBy = " title ASC ";
                break;
            case SORT_BY_YEAR_DESC:
                sortBy = " year DESC ";
                break;
            case SORT_BY_YEAR_ASC:
                sortBy = " year ASC ";
                break;
            case SORT_BY_DATE_DESC:
                sortBy = " created DESC ";
                break;
            case SORT_BY_DATE_ASC:
                sortBy = " created ASC ";
                break;
        }

        List<Movie> l = new Select().from(Movie.class).orderBy(sortBy).execute();
        if (l == null)
            l = new ArrayList<>();
        return l;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", plot='" + plot + '\'' +
                ", poster='" + poster + '\'' +
                ", posterData=" + Arrays.toString(posterData) +
                ", imdbRating='" + imdbRating + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", response='" + response + '\'' +
                ", created=" + created +
                '}';
    }
}
