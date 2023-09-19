import dayjs from 'dayjs/esm';

import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 47761,
  orderDate: dayjs('2023-09-18T15:36'),
  status: 'New Parks Handcrafted',
};

export const sampleWithPartialData: IOrder = {
  id: 61522,
  orderDate: dayjs('2023-09-18T22:58'),
  status: 'Ville Account Cambridgeshire',
};

export const sampleWithFullData: IOrder = {
  id: 80079,
  orderDate: dayjs('2023-09-18T20:48'),
  status: 'violet reinvent International',
};

export const sampleWithNewData: NewOrder = {
  orderDate: dayjs('2023-09-18T17:10'),
  status: 'Unbranded 24/7 Intranet',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
