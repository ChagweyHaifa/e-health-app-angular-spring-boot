import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Doctor } from '../model/doctor';
import { Rating } from '../model/rating';

@Injectable({
  providedIn: 'root',
})
export class RatingService {
  host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public getDoctorRatings(username: string): Observable<Rating[]> {
    return this.http.get<Rating[]>(
      `${this.host}/ratings/search/findByDoctorUsername/${username}`
    );
  }

  public addRating(rating: Rating): Observable<Doctor> {
    return this.http.post<Doctor>(`${this.host}/ratings`, rating);
  }

  public editRating(rating: Rating): Observable<Doctor> {
    return this.http.put<Doctor>(`${this.host}/ratings`, rating);
  }

  public deleteRating(doctorUsername: String): Observable<Doctor> {
    return this.http.delete<Doctor>(
      `${this.host}/ratings?doctorUsername=${doctorUsername}`
    );
  }
}
