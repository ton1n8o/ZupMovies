package zup.com.br.zupmovies.domains;

import java.util.List;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/6/16.
 */
public class SearchResponse {

    private List<Movie> search;

    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }

}
