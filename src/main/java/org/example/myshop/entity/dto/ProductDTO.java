package org.example.myshop.entity.dto;

public class ProductDTO {
    private String name;
    private Double price;
    private int count;
    private String description;
    private String image;
    private String category;

    public ProductDTO() {
    }

    public ProductDTO(String name, Double price, int count, String description, String image, String category) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.description = description;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
