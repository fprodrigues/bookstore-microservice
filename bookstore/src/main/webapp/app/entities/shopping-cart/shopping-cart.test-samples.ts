import dayjs from 'dayjs/esm';

import { IShoppingCart, NewShoppingCart } from './shopping-cart.model';

export const sampleWithRequiredData: IShoppingCart = {
  id: 126,
  purchaseDate: dayjs('2023-09-18T21:31'),
  status: 'Steel B2C',
};

export const sampleWithPartialData: IShoppingCart = {
  id: 96105,
  purchaseDate: dayjs('2023-09-18T18:37'),
  status: 'invoice Web',
};

export const sampleWithFullData: IShoppingCart = {
  id: 91317,
  purchaseDate: dayjs('2023-09-18T20:24'),
  status: 'synthesizing Mouse',
};

export const sampleWithNewData: NewShoppingCart = {
  purchaseDate: dayjs('2023-09-18T05:37'),
  status: 'Fall Plastic',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
