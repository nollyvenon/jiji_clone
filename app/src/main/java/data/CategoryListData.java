package data;

public class CategoryListData {

    int categoryImage;
    String categoryName;
    String categoryCount;

    public CategoryListData(int categoryImage, String categoryName, String categoryCount) {
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
        this.categoryCount = categoryCount;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryCount() {
        return categoryCount;
    }

}
