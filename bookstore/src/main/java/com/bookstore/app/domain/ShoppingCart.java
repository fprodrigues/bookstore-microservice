package com.bookstore.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ShoppingCart.
 */
@Table("shopping_cart")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("purchase_date")
    private Instant purchaseDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Transient
    @JsonIgnoreProperties(value = { "shoppingCarts" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "shoppingCarts", "customers" }, allowSetters = true)
    private Order order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShoppingCart id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPurchaseDate() {
        return this.purchaseDate;
    }

    public ShoppingCart purchaseDate(Instant purchaseDate) {
        this.setPurchaseDate(purchaseDate);
        return this;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return this.status;
    }

    public ShoppingCart status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.setShoppingCarts(null));
        }
        if (books != null) {
            books.forEach(i -> i.setShoppingCarts(this));
        }
        this.books = books;
    }

    public ShoppingCart books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public ShoppingCart addBooks(Book book) {
        this.books.add(book);
        book.setShoppingCarts(this);
        return this;
    }

    public ShoppingCart removeBooks(Book book) {
        this.books.remove(book);
        book.setShoppingCarts(null);
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public ShoppingCart order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingCart)) {
            return false;
        }
        return id != null && id.equals(((ShoppingCart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShoppingCart{" +
            "id=" + getId() +
            ", purchaseDate='" + getPurchaseDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
