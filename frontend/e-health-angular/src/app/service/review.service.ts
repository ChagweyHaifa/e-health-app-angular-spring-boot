import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Review } from '../model/review';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  private host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public getDoctorReviews(username: string): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.host}/reviews/${username}`);
  }
  public addReview(review: Review): Observable<Review[]> {
    return this.http.post<Review[]>(`${this.host}/reviews`, review);
  }
}
