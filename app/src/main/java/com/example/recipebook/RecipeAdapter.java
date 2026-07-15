package com.example.recipebook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final Context context;
    private final List<Recipe> fullRecipeList;
    private List<Recipe> filteredRecipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.fullRecipeList = recipeList;
        this.filteredRecipeList = new ArrayList<>(recipeList);
    }


    public void filterByCategory(String category) {
        filteredRecipeList.clear();
        if (category.equals("All")) {
            filteredRecipeList.addAll(fullRecipeList);
        } else {
            for (Recipe recipe : fullRecipeList) {
                if (recipe.getClassification() != null && recipe.getClassification().equalsIgnoreCase(category)) {
                    filteredRecipeList.add(recipe);
                }
            }
        }
        notifyDataSetChanged();
    }


    public void filterBySearch(String query) {
        filteredRecipeList.clear();
        if (query.isEmpty()) {
            filteredRecipeList.addAll(fullRecipeList);
        } else {
            for (Recipe recipe : fullRecipeList) {
                if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredRecipeList.add(recipe);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = filteredRecipeList.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvCategory.setText(recipe.getClassification());
        holder.tvIngredients.setText(recipe.getIngredients());

        if (recipe.getImageUri() != null && !recipe.getImageUri().isEmpty()) {
            Glide.with(context).load(recipe.getImageUri()).error(R.drawable.img_6).into(holder.imgRecipe);
        } else {
            holder.imgRecipe.setImageResource(R.drawable.img_6);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra("recipeId", recipe.getId());
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("steps", recipe.getPreparationSteps());
            intent.putExtra("category", recipe.getClassification());
            intent.putExtra("imageUrl", recipe.getImageUri());
            intent.putExtra("youtubeUrl", recipe.getYoutubeVideoURL());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return filteredRecipeList.size(); }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView tvTitle, tvCategory, tvIngredients;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgItemRecipe);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvIngredients = itemView.findViewById(R.id.tvItemIngredients);
        }
    }
}