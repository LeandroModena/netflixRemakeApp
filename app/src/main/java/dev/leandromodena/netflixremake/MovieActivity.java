package dev.leandromodena.netflixremake;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.leandromodena.netflixremake.model.Movie;
import dev.leandromodena.netflixremake.model.MovieDetail;
import dev.leandromodena.netflixremake.util.ImageDownloadTask;
import dev.leandromodena.netflixremake.util.MovieDetailTask;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoad {

    TextView textViewTitle;
    TextView textViewDesc;
    TextView textViewCast;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        textViewCast = findViewById(R.id.textViewCast);
        textViewDesc = findViewById(R.id.textViewDesc);
        textViewTitle = findViewById(R.id.textViewTitle);
        recyclerView = findViewById(R.id.recyclerViewSimilar);
        imgCover = findViewById(R.id.imageViewCover);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);

        }

        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.shadows);

        if (drawable != null){
            Drawable movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4);
            drawable.setDrawableByLayerId(R.id.cover_drawable, movieCover);
            //((ImageView) findViewById(R.id.imageViewCover)). setImageDrawable(drawable);
        }
        List<Movie> movies = new ArrayList<>();
        //Set para Texte


        /**
        textViewTitle.setText("Batmam Begins");
        textViewDesc.setText(R.string.mock_desck);
        textViewCast.setText(getString(R.string.cast, "Leandro Modena" + " Janaina Modena" + " André Modena" + " Felipe Modena"));


        for (int i = 0; i < 30; i++) {
            Movie movie = new Movie();
            movies.add(movie);
        }**/
        movieAdapter = new MovieAdapter(movies);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoad(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() ==  android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetail movieDetail) {
        textViewTitle.setText(movieDetail.getMovie().getTitle());
        textViewDesc.setText(movieDetail.getMovie().getDesc());
        textViewCast.setText(movieDetail.getMovie().getCast());

        ImageDownloadTask imageDownloadTask = new ImageDownloadTask(imgCover);
        imageDownloadTask.setShadowEnabled(true);
        imageDownloadTask.execute(movieDetail.getMovie().getCoverUrl());

        movieAdapter.setMovies(movieDetail.getMoviesSimilar());
        movieAdapter.notifyDataSetChanged();

    }

    private static class MovieHolder extends RecyclerView.ViewHolder{
        ImageView imageViewCover;
        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);

        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder>{
        // Todos os adpatdadores tem que ter uma lista para gerenciar os dados dinamicos

        private List<Movie> movies;

        public MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        public void setMovies(List<Movie> movies){
            this.movies.clear();
            this.movies.addAll(movies);

        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            //           holder.imageViewCover.setImageResource(movie.getCoverUrl());
            new ImageDownloadTask(holder.imageViewCover).execute(movie.getCoverUrl());

        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }

}