package zup.com.br.zupmovies.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import zup.com.br.zupmovies.R;
import zup.com.br.zupmovies.domains.Movie;
import zup.com.br.zupmovies.services.Services;

public class ActSearch extends AppCompatActivity implements Services.OnServiceResponse {

    // View Elements
    @Bind(R.id.edt_search) EditText edtSearch;

    /* Activity Lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        ButterKnife.bind(this);
        //TODO: Home Button enabled:
    }

    /* Activity Controls */

    @OnEditorAction(R.id.edt_search)
    public boolean actionSearchMovie(int actionId) {
        if (EditorInfo.IME_ACTION_SEARCH == actionId ||
                EditorInfo.IME_FLAG_NO_ENTER_ACTION == actionId) {
            doSearch();
            return true;
        }
        return false;
    }

    /* Private Methods */

    private void doSearch() {
        String searchTerm = this.edtSearch.getText().toString();
        if (!TextUtils.isEmpty(searchTerm)) {
            Services.getInstance(this).searchMovie(searchTerm);
        } else {
            Toast.makeText(this, R.string.msg_informar_termo_pesquisa, Toast.LENGTH_LONG).show();
        }
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.edtSearch.getWindowToken(), 0);
    }

    // Services.OnServiceResponse

    @Override
    public void onResponse(Movie movie) {
        System.out.println("movie = [" + movie + "]");
    }

    @Override
    public void onError(String msg) {

    }
}
