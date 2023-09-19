import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BookFormService } from './book-form.service';
import { BookService } from '../service/book.service';
import { IBook } from '../book.model';
import { IShoppingCart } from 'app/entities/shopping-cart/shopping-cart.model';
import { ShoppingCartService } from 'app/entities/shopping-cart/service/shopping-cart.service';

import { BookUpdateComponent } from './book-update.component';

describe('Book Management Update Component', () => {
  let comp: BookUpdateComponent;
  let fixture: ComponentFixture<BookUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookFormService: BookFormService;
  let bookService: BookService;
  let shoppingCartService: ShoppingCartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BookUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookFormService = TestBed.inject(BookFormService);
    bookService = TestBed.inject(BookService);
    shoppingCartService = TestBed.inject(ShoppingCartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ShoppingCart query and add missing value', () => {
      const book: IBook = { id: 456 };
      const shoppingCarts: IShoppingCart = { id: 31178 };
      book.shoppingCarts = shoppingCarts;

      const shoppingCartCollection: IShoppingCart[] = [{ id: 20048 }];
      jest.spyOn(shoppingCartService, 'query').mockReturnValue(of(new HttpResponse({ body: shoppingCartCollection })));
      const additionalShoppingCarts = [shoppingCarts];
      const expectedCollection: IShoppingCart[] = [...additionalShoppingCarts, ...shoppingCartCollection];
      jest.spyOn(shoppingCartService, 'addShoppingCartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(shoppingCartService.query).toHaveBeenCalled();
      expect(shoppingCartService.addShoppingCartToCollectionIfMissing).toHaveBeenCalledWith(
        shoppingCartCollection,
        ...additionalShoppingCarts.map(expect.objectContaining)
      );
      expect(comp.shoppingCartsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const book: IBook = { id: 456 };
      const shoppingCarts: IShoppingCart = { id: 15499 };
      book.shoppingCarts = shoppingCarts;

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(comp.shoppingCartsSharedCollection).toContain(shoppingCarts);
      expect(comp.book).toEqual(book);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue(book);
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookService.update).toHaveBeenCalledWith(expect.objectContaining(book));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue({ id: null });
      jest.spyOn(bookService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(bookService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareShoppingCart', () => {
      it('Should forward to shoppingCartService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(shoppingCartService, 'compareShoppingCart');
        comp.compareShoppingCart(entity, entity2);
        expect(shoppingCartService.compareShoppingCart).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
