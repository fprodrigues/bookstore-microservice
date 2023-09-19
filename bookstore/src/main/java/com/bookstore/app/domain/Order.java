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
 * A Order.
 */
@Table("jhi_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("order_date")
    private Instant orderDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Transient
    @JsonIgnoreProperties(value = { "books", "order" }, allowSetters = true)
    private Set<ShoppingCart> shoppingCarts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "orders" }, allowSetters = true)
    private Set<Customer> customers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrderDate() {
        return this.orderDate;
    }

    public Order orderDate(Instant orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return this.status;
    }

    public Order status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<ShoppingCart> getShoppingCarts() {
        return this.shoppingCarts;
    }

    public void setShoppingCarts(Set<ShoppingCart> shoppingCarts) {
        if (this.shoppingCarts != null) {
            this.shoppingCarts.forEach(i -> i.setOrder(null));
        }
        if (shoppingCarts != null) {
            shoppingCarts.forEach(i -> i.setOrder(this));
        }
        this.shoppingCarts = shoppingCarts;
    }

    public Order shoppingCarts(Set<ShoppingCart> shoppingCarts) {
        this.setShoppingCarts(shoppingCarts);
        return this;
    }

    public Order addShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCarts.add(shoppingCart);
        shoppingCart.setOrder(this);
        return this;
    }

    public Order removeShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCarts.remove(shoppingCart);
        shoppingCart.setOrder(null);
        return this;
    }

    public Set<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        if (this.customers != null) {
            this.customers.forEach(i -> i.setOrders(null));
        }
        if (customers != null) {
            customers.forEach(i -> i.setOrders(this));
        }
        this.customers = customers;
    }

    public Order customers(Set<Customer> customers) {
        this.setCustomers(customers);
        return this;
    }

    public Order addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.setOrders(this);
        return this;
    }

    public Order removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.setOrders(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", orderDate='" + getOrderDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
