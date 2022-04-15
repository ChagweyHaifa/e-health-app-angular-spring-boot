import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { City } from '../model/city';
import { Speciality } from '../model/speciality';
import { State } from '../model/state';
import { map } from 'rxjs/operators';
import { Country } from '../model/country';

@Injectable({
  providedIn: 'root',
})
export class FormService {
  host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public getSpecialities(): Observable<Speciality[]> {
    const searchUrl = `${this.host}/specialities?sort=name,asc`;
    return this.http
      .get<GetResponseSpecialities>(searchUrl)
      .pipe(map((response) => response._embedded.specialities));
  }

  public getCountries(): Observable<Country[]> {
    const searchUrl = `${this.host}/countries?sort=name,asc`;
    return this.http
      .get<GetResponseCountries>(searchUrl)
      .pipe(map((response) => response._embedded.countries));
  }

  public getStates(name: string): Observable<State[]> {
    const searchUrl = `${this.host}/states/search/findByCountryName?name=${name}&sort=name,asc`;
    return this.http
      .get<GetResponseStates>(searchUrl)
      .pipe(map((response) => response._embedded.states));
  }

  getCities(name: string): Observable<City[]> {
    const searchUrl = `${this.host}/cities/search/findByStateName?name=${name}&sort=name,asc`;
    return this.http
      .get<GetResponseCities>(searchUrl)
      .pipe(map((response) => response._embedded.cities));
  }
}

interface GetResponseSpecialities {
  _embedded: {
    specialities: Speciality[];
  };
}

interface GetResponseCountries {
  _embedded: {
    countries: Country[];
  };
}

interface GetResponseStates {
  _embedded: {
    states: State[];
  };
}
interface GetResponseCities {
  _embedded: {
    cities: City[];
  };
}
