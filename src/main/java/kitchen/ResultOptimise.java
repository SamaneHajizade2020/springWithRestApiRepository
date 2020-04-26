package kitchen;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class ResultOptimise {

    @JsonIgnore()
    private String id;
    List<Result> recipes;
    private String recipeCount;
    private String unusedInventoryCount;

    public ResultOptimise(String id, List<Result> results, String recipeCount, String unusedInventoryCount) {
        this.id = id;
        this.recipes = results;
        this.recipeCount = recipeCount;
        this.unusedInventoryCount = unusedInventoryCount;
    }

    public ResultOptimise(List<Result> results, String recipeCount, String unusedInventoryCount) {
        this.recipes = results;
        this.recipeCount = recipeCount;
        this.unusedInventoryCount = unusedInventoryCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Result> getResults() {
        return recipes;
    }

    public void setResults(List<Result> results) {
        this.recipes = results;
    }

    public String getRecipeCount() {
        return recipeCount;
    }

    public void setRecipeCount(String recipeCount) {
        this.recipeCount = recipeCount;
    }

    public String getUnusedInventoryCount() {
        return unusedInventoryCount;
    }

    public void setUnusedInventoryCount(String unusedInventoryCount) {
        this.unusedInventoryCount = unusedInventoryCount;
    }
}
