package com.bookstore.app.repository;

import com.bookstore.app.domain.ShoppingCart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ShoppingCart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShoppingCartRepository extends ReactiveCrudRepository<ShoppingCart, Long>, ShoppingCartRepositoryInternal {
    @Query("SELECT * FROM shopping_cart entity WHERE entity.order_id = :id")
    Flux<ShoppingCart> findByOrder(Long id);

    @Query("SELECT * FROM shopping_cart entity WHERE entity.order_id IS NULL")
    Flux<ShoppingCart> findAllWhereOrderIsNull();

    @Override
    <S extends ShoppingCart> Mono<S> save(S entity);

    @Override
    Flux<ShoppingCart> findAll();

    @Override
    Mono<ShoppingCart> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ShoppingCartRepositoryInternal {
    <S extends ShoppingCart> Mono<S> save(S entity);

    Flux<ShoppingCart> findAllBy(Pageable pageable);

    Flux<ShoppingCart> findAll();

    Mono<ShoppingCart> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ShoppingCart> findAllBy(Pageable pageable, Criteria criteria);

}
