package com.bookstore.app.web.rest;

import com.bookstore.app.domain.ShoppingCart;
import com.bookstore.app.repository.ShoppingCartRepository;
import com.bookstore.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.bookstore.app.domain.ShoppingCart}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ShoppingCartResource {

    private final Logger log = LoggerFactory.getLogger(ShoppingCartResource.class);

    private static final String ENTITY_NAME = "shoppingCart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartResource(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    /**
     * {@code POST  /shopping-carts} : Create a new shoppingCart.
     *
     * @param shoppingCart the shoppingCart to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shoppingCart, or with status {@code 400 (Bad Request)} if the shoppingCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shopping-carts")
    public Mono<ResponseEntity<ShoppingCart>> createShoppingCart(@Valid @RequestBody ShoppingCart shoppingCart) throws URISyntaxException {
        log.debug("REST request to save ShoppingCart : {}", shoppingCart);
        if (shoppingCart.getId() != null) {
            throw new BadRequestAlertException("A new shoppingCart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return shoppingCartRepository
            .save(shoppingCart)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/shopping-carts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /shopping-carts/:id} : Updates an existing shoppingCart.
     *
     * @param id the id of the shoppingCart to save.
     * @param shoppingCart the shoppingCart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCart,
     * or with status {@code 400 (Bad Request)} if the shoppingCart is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shopping-carts/{id}")
    public Mono<ResponseEntity<ShoppingCart>> updateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ShoppingCart shoppingCart
    ) throws URISyntaxException {
        log.debug("REST request to update ShoppingCart : {}, {}", id, shoppingCart);
        if (shoppingCart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return shoppingCartRepository
                    .save(shoppingCart)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /shopping-carts/:id} : Partial updates given fields of an existing shoppingCart, field will ignore if it is null
     *
     * @param id the id of the shoppingCart to save.
     * @param shoppingCart the shoppingCart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shoppingCart,
     * or with status {@code 400 (Bad Request)} if the shoppingCart is not valid,
     * or with status {@code 404 (Not Found)} if the shoppingCart is not found,
     * or with status {@code 500 (Internal Server Error)} if the shoppingCart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shopping-carts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ShoppingCart>> partialUpdateShoppingCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ShoppingCart shoppingCart
    ) throws URISyntaxException {
        log.debug("REST request to partial update ShoppingCart partially : {}, {}", id, shoppingCart);
        if (shoppingCart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shoppingCart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return shoppingCartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ShoppingCart> result = shoppingCartRepository
                    .findById(shoppingCart.getId())
                    .map(existingShoppingCart -> {
                        if (shoppingCart.getPurchaseDate() != null) {
                            existingShoppingCart.setPurchaseDate(shoppingCart.getPurchaseDate());
                        }
                        if (shoppingCart.getStatus() != null) {
                            existingShoppingCart.setStatus(shoppingCart.getStatus());
                        }

                        return existingShoppingCart;
                    })
                    .flatMap(shoppingCartRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /shopping-carts} : get all the shoppingCarts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shoppingCarts in body.
     */
    @GetMapping("/shopping-carts")
    public Mono<List<ShoppingCart>> getAllShoppingCarts() {
        log.debug("REST request to get all ShoppingCarts");
        return shoppingCartRepository.findAll().collectList();
    }

    /**
     * {@code GET  /shopping-carts} : get all the shoppingCarts as a stream.
     * @return the {@link Flux} of shoppingCarts.
     */
    @GetMapping(value = "/shopping-carts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ShoppingCart> getAllShoppingCartsAsStream() {
        log.debug("REST request to get all ShoppingCarts as a stream");
        return shoppingCartRepository.findAll();
    }

    /**
     * {@code GET  /shopping-carts/:id} : get the "id" shoppingCart.
     *
     * @param id the id of the shoppingCart to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shoppingCart, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shopping-carts/{id}")
    public Mono<ResponseEntity<ShoppingCart>> getShoppingCart(@PathVariable Long id) {
        log.debug("REST request to get ShoppingCart : {}", id);
        Mono<ShoppingCart> shoppingCart = shoppingCartRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(shoppingCart);
    }

    /**
     * {@code DELETE  /shopping-carts/:id} : delete the "id" shoppingCart.
     *
     * @param id the id of the shoppingCart to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shopping-carts/{id}")
    public Mono<ResponseEntity<Void>> deleteShoppingCart(@PathVariable Long id) {
        log.debug("REST request to delete ShoppingCart : {}", id);
        return shoppingCartRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
