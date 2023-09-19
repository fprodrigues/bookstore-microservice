import { IShoppingCart } from 'app/entities/shopping-cart/shopping-cart.model';

export interface IBook {
  id: number;
  title?: string | null;
  author?: string | null;
  publicationYear?: number | null;
  genre?: string | null;
  price?: number | null;
  quantityInStock?: number | null;
  shoppingCarts?: Pick<IShoppingCart, 'id'> | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
