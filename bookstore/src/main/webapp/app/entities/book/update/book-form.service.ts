import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBook, NewBook } from '../book.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBook for edit and NewBookFormGroupInput for create.
 */
type BookFormGroupInput = IBook | PartialWithRequiredKeyOf<NewBook>;

type BookFormDefaults = Pick<NewBook, 'id'>;

type BookFormGroupContent = {
  id: FormControl<IBook['id'] | NewBook['id']>;
  title: FormControl<IBook['title']>;
  author: FormControl<IBook['author']>;
  publicationYear: FormControl<IBook['publicationYear']>;
  genre: FormControl<IBook['genre']>;
  price: FormControl<IBook['price']>;
  quantityInStock: FormControl<IBook['quantityInStock']>;
  shoppingCarts: FormControl<IBook['shoppingCarts']>;
};

export type BookFormGroup = FormGroup<BookFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookFormService {
  createBookFormGroup(book: BookFormGroupInput = { id: null }): BookFormGroup {
    const bookRawValue = {
      ...this.getFormDefaults(),
      ...book,
    };
    return new FormGroup<BookFormGroupContent>({
      id: new FormControl(
        { value: bookRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(bookRawValue.title, {
        validators: [Validators.required],
      }),
      author: new FormControl(bookRawValue.author),
      publicationYear: new FormControl(bookRawValue.publicationYear),
      genre: new FormControl(bookRawValue.genre),
      price: new FormControl(bookRawValue.price, {
        validators: [Validators.required],
      }),
      quantityInStock: new FormControl(bookRawValue.quantityInStock, {
        validators: [Validators.required],
      }),
      shoppingCarts: new FormControl(bookRawValue.shoppingCarts),
    });
  }

  getBook(form: BookFormGroup): IBook | NewBook {
    return form.getRawValue() as IBook | NewBook;
  }

  resetForm(form: BookFormGroup, book: BookFormGroupInput): void {
    const bookRawValue = { ...this.getFormDefaults(), ...book };
    form.reset(
      {
        ...bookRawValue,
        id: { value: bookRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BookFormDefaults {
    return {
      id: null,
    };
  }
}
