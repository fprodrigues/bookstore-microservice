import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 37098,
  title: 'Jewelery',
  price: 26912,
  quantityInStock: 60289,
};

export const sampleWithPartialData: IBook = {
  id: 58635,
  title: 'object-oriented Wooden Pizza',
  publicationYear: 81264,
  genre: 'Steel bandwidth',
  price: 32668,
  quantityInStock: 65305,
};

export const sampleWithFullData: IBook = {
  id: 98221,
  title: 'Egypt Stand-alone',
  author: 'compelling Music',
  publicationYear: 57563,
  genre: 'Naira application',
  price: 33998,
  quantityInStock: 27044,
};

export const sampleWithNewData: NewBook = {
  title: 'invoice Salad Senior',
  price: 66464,
  quantityInStock: 43257,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
