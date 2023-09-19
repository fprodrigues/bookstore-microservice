package com.bookstore.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.bookstore.app.IntegrationTest;
import com.bookstore.app.domain.Book;
import com.bookstore.app.repository.BookRepository;
import com.bookstore.app.repository.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final Integer DEFAULT_PUBLICATION_YEAR = 1;
    private static final Integer UPDATED_PUBLICATION_YEAR = 2;

    private static final String DEFAULT_GENRE = "AAAAAAAAAA";
    private static final String UPDATED_GENRE = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Integer DEFAULT_QUANTITY_IN_STOCK = 1;
    private static final Integer UPDATED_QUANTITY_IN_STOCK = 2;

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .title(DEFAULT_TITLE)
            .author(DEFAULT_AUTHOR)
            .publicationYear(DEFAULT_PUBLICATION_YEAR)
            .genre(DEFAULT_GENRE)
            .price(DEFAULT_PRICE)
            .quantityInStock(DEFAULT_QUANTITY_IN_STOCK);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .publicationYear(UPDATED_PUBLICATION_YEAR)
            .genre(UPDATED_GENRE)
            .price(UPDATED_PRICE)
            .quantityInStock(UPDATED_QUANTITY_IN_STOCK);
        return book;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Book.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        book = createEntity(em);
    }

    @Test
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().collectList().block().size();
        // Create the Book
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBook.getPublicationYear()).isEqualTo(DEFAULT_PUBLICATION_YEAR);
        assertThat(testBook.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testBook.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testBook.getQuantityInStock()).isEqualTo(DEFAULT_QUANTITY_IN_STOCK);
    }

    @Test
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);

        int databaseSizeBeforeCreate = bookRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().collectList().block().size();
        // set the field null
        book.setTitle(null);

        // Create the Book, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().collectList().block().size();
        // set the field null
        book.setPrice(null);

        // Create the Book, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkQuantityInStockIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().collectList().block().size();
        // set the field null
        book.setQuantityInStock(null);

        // Create the Book, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBooksAsStream() {
        // Initialize the database
        bookRepository.save(book).block();

        List<Book> bookList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Book.class)
            .getResponseBody()
            .filter(book::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(bookList).isNotNull();
        assertThat(bookList).hasSize(1);
        Book testBook = bookList.get(0);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBook.getPublicationYear()).isEqualTo(DEFAULT_PUBLICATION_YEAR);
        assertThat(testBook.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testBook.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testBook.getQuantityInStock()).isEqualTo(DEFAULT_QUANTITY_IN_STOCK);
    }

    @Test
    void getAllBooks() {
        // Initialize the database
        bookRepository.save(book).block();

        // Get all the bookList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(book.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].author")
            .value(hasItem(DEFAULT_AUTHOR))
            .jsonPath("$.[*].publicationYear")
            .value(hasItem(DEFAULT_PUBLICATION_YEAR))
            .jsonPath("$.[*].genre")
            .value(hasItem(DEFAULT_GENRE))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.[*].quantityInStock")
            .value(hasItem(DEFAULT_QUANTITY_IN_STOCK));
    }

    @Test
    void getBook() {
        // Initialize the database
        bookRepository.save(book).block();

        // Get the book
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, book.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(book.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.author")
            .value(is(DEFAULT_AUTHOR))
            .jsonPath("$.publicationYear")
            .value(is(DEFAULT_PUBLICATION_YEAR))
            .jsonPath("$.genre")
            .value(is(DEFAULT_GENRE))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.quantityInStock")
            .value(is(DEFAULT_QUANTITY_IN_STOCK));
    }

    @Test
    void getNonExistingBook() {
        // Get the book
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBook() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).block();
        updatedBook
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .publicationYear(UPDATED_PUBLICATION_YEAR)
            .genre(UPDATED_GENRE)
            .price(UPDATED_PRICE)
            .quantityInStock(UPDATED_QUANTITY_IN_STOCK);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBook.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBook))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBook.getPublicationYear()).isEqualTo(UPDATED_PUBLICATION_YEAR);
        assertThat(testBook.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testBook.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBook.getQuantityInStock()).isEqualTo(UPDATED_QUANTITY_IN_STOCK);
    }

    @Test
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.price(UPDATED_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBook.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBook.getPublicationYear()).isEqualTo(DEFAULT_PUBLICATION_YEAR);
        assertThat(testBook.getGenre()).isEqualTo(DEFAULT_GENRE);
        assertThat(testBook.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBook.getQuantityInStock()).isEqualTo(DEFAULT_QUANTITY_IN_STOCK);
    }

    @Test
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .title(UPDATED_TITLE)
            .author(UPDATED_AUTHOR)
            .publicationYear(UPDATED_PUBLICATION_YEAR)
            .genre(UPDATED_GENRE)
            .price(UPDATED_PRICE)
            .quantityInStock(UPDATED_QUANTITY_IN_STOCK);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBook.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBook.getPublicationYear()).isEqualTo(UPDATED_PUBLICATION_YEAR);
        assertThat(testBook.getGenre()).isEqualTo(UPDATED_GENRE);
        assertThat(testBook.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBook.getQuantityInStock()).isEqualTo(UPDATED_QUANTITY_IN_STOCK);
    }

    @Test
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, book.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(book))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBook() {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeDelete = bookRepository.findAll().collectList().block().size();

        // Delete the book
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, book.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
