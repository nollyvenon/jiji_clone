package data;

public class CategoryListData {

    String image;
    String id;
    String name;
    String count;

    public CategoryListData(String id, String image, String name, String count) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

}
