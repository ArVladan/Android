package promo.kit.androidlamrak;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import promo.kit.androidlamrak.model.Movie;
import promo.kit.androidlamrak.network.MovieFetcherAsync;
import promo.kit.androidlamrak.network.MovieNetworkParser;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private RecyclerView rv;
    private List<Movie> movies;
    private MovieAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        movies = new ArrayList<>();
        rv = (RecyclerView) root.findViewById(R.id.rv_movies);
        adapter = new MovieAdapter(getContext(), movies);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE ? 3 : 2));

        MovieFetcherAsync task = new MovieFetcherAsync(new MovieFetcherAsync.IResultListener() {
            @Override
            public void onResult(String result) {
                if (TextUtils.isEmpty(result)) //TODO handle error
                    return;

                try {
                    movies.addAll(MovieNetworkParser.getMoviesFromJson(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //TODO handle error
                    return;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                // error
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        //BuildConfig.OPEN_WEATHER_MAP_API_KEY
        task.execute("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + BuildConfig.OPEN_MOVIE_DB_API_KEY);

        return root;
    }
}