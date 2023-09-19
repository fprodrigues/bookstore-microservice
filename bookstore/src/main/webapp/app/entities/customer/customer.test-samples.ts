import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 24379,
  name: 'Operations',
  email: 'Vaughn.Mosciski8@yahoo.com',
};

export const sampleWithPartialData: ICustomer = {
  id: 75018,
  name: 'Namibia Cambridgeshire',
  email: 'Amira_Block@gmail.com',
  phone: '1-414-967-5237 x76635',
};

export const sampleWithFullData: ICustomer = {
  id: 50737,
  name: 'Maine',
  email: 'Lenna_Gleason@hotmail.com',
  address: 'Awesome',
  phone: '434.398.7821 x030',
};

export const sampleWithNewData: NewCustomer = {
  name: 'Games Analyst',
  email: 'Luciano.Runolfsson82@hotmail.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
