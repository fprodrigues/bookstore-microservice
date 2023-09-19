package com.bookstore.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Book.
 */
@Table("book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Column("author")
    private String author;

    @Column("publication_year")
    private Integer publicationYear;

    @Column("genre")
    private String genre;

    @NotNull(message = "must not be null")
    @Column("price")
    private Double price;

    @NotNull(message = "must not be null")
    @Column("quantity_in_stock")
    private Integer quantityInStock;

    @Transient
    @JsonIgnoreProperties(value = { "books", "order" }, allowSetters = true)
    private ShoppingCart shoppingCarts;

    @Column("shopping_carts_id")
    private Long shoppingCartsId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public Book author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return this.publicationYear;
    }

    public Book publicationYear(Integer publicationYear) {
        this.setPublicationYear(publicationYear);
        return this;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getGenre() {
        return this.genre;
    }

    public Book genre(String genre) {
        this.setGenre(genre);
        return this;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getPrice() {
        return this.price;
    }

    public Book price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantityInStock() {
        return this.quantityInStock;
    }

    public Book quantityInStock(Integer quantityInStock) {
        this.setQuantityInStock(quantityInStock);
        return this;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public ShoppingCart getShoppingCarts() {
        return this.shoppingCarts;
    }

    public void setShoppingCarts(ShoppingCart shoppingCart) {
        this.shoppingCarts = shoppingCart;
        this.shoppingCartsId = shoppingCart != null ? shoppingCart.getId() : null;
    }

    public Book shoppingCarts(ShoppingCart shoppingCart) {
        this.setShoppingCarts(shoppingCart);
        return this;
    }

    public Long getShoppingCartsId() {
        return this.shoppingCartsId;
    }

    public void setShoppingCartsId(Long shoppingCart) {
        this.shoppingCartsId = shoppingCart;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", author='" + getAuthor() + "'" +
            ", publicationYear=" + getPublicationYear() +
            ", genre='" + getGenre() + "'" +
            ", price=" + getPrice() +
            ", quantityInStock=" + getQuantityInStock() +
            "}";
    }
}
