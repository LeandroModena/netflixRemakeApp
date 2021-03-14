package dev.leandromodena.netflixremake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.leandromodena.netflixremake.model.Category;
import dev.leandromodena.netflixremake.model.Movie;
import dev.leandromodena.netflixremake.util.CategoryTask;
import dev.leandromodena.netflixremake.util.ImageDownloadTask;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoad {
    private  MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);

        List<Category> categories = new ArrayList<>();


        //Criação de dados FAKES para teste do aplicativo
        /**for (int j = 0; j < 10; j++) {
            Category category = new Category();
            category.setName("cat" + j);

            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                Movie movie = new Movie();
//                movie.setCoverUrl(R.drawable.movie);
                movies.add(movie);
        }
            category.setMovies(movies);
            categories.add(category);

        }**/

        mainAdapter = new MainAdapter(categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mainAdapter);

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoad(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");



    }

    @Override
    public void onResult(List<Category> categories) {
        mainAdapter.setCategories(categories);
        mainAdapter.notifyDataSetChanged();
    }

    //Para criação de RecyclerView onde apenas uma Activity irá usar
    // Holder = Suporte
    private static class MovieHolder extends RecyclerView.ViewHolder{
        ImageView imageViewCover;
        public MovieHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });

        }
    }

    private static class CategoryHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        RecyclerView recyclerViewMovie;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            recyclerViewMovie = itemView.findViewById(R.id.recyclerViewMovie);
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<CategoryHolder>{
        // Todos os adpatdadores tem que ter uma lista para gerenciar os dados dinamicos

        private List<Category> categories;

        public MainAdapter(List<Category> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            Category category = categories.get(position);
            holder.textViewTitle.setText(category.getName());
            holder.recyclerViewMovie.setAdapter(new MovieAdapter(category.getMovies()));
            holder.recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.HORIZONTAL, false ));

        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public void setCategories(List<Category> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
        }
    }


    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder>implements OnItemClickListener{

        private List<Movie> movies;

        public MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @Override
        public void onClick(int position) {
            if (movies.get(position).getId() <= 3){
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("id", movies.get(position).getId());
            startActivity(intent);
            }
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent, false);

            return new MovieHolder(view, this::onClick);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
          new ImageDownloadTask(holder.imageViewCover).execute(movie.getCoverUrl());

        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }

    interface OnItemClickListener{
        void onClick (int position);
    }
}