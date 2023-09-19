import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BookFormService, BookFormGroup } from './book-form.service';
import { IBook } from '../book.model';
import { BookService } from '../service/book.service';
import { IShoppingCart } from 'app/entities/shopping-cart/shopping-cart.model';
import { ShoppingCartService } from 'app/entities/shopping-cart/service/shopping-cart.service';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  book: IBook | null = null;

  shoppingCartsSharedCollection: IShoppingCart[] = [];

  editForm: BookFormGroup = this.bookFormService.createBookFormGroup();

  constructor(
    protected bookService: BookService,
    protected bookFormService: BookFormService,
    protected shoppingCartService: ShoppingCartService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareShoppingCart = (o1: IShoppingCart | null, o2: IShoppingCart | null): boolean =>
    this.shoppingCartService.compareShoppingCart(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.book = book;
      if (book) {
        this.updateForm(book);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.bookFormService.getBook(this.editForm);
    if (book.id !== null) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(book: IBook): void {
    this.book = book;
    this.bookFormService.resetForm(this.editForm, book);

    this.shoppingCartsSharedCollection = this.shoppingCartService.addShoppingCartToCollectionIfMissing<IShoppingCart>(
      this.shoppingCartsSharedCollection,
      book.shoppingCarts
    );
  }

  protected loadRelationshipsOptions(): void {
    this.shoppingCartService
      .query()
      .pipe(map((res: HttpResponse<IShoppingCart[]>) => res.body ?? []))
      .pipe(
        map((shoppingCarts: IShoppingCart[]) =>
          this.shoppingCartService.addShoppingCartToCollectionIfMissing<IShoppingCart>(shoppingCarts, this.book?.shoppingCarts)
        )
      )
      .subscribe((shoppingCarts: IShoppingCart[]) => (this.shoppingCartsSharedCollection = shoppingCarts));
  }
}
