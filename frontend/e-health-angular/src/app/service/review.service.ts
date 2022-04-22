import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpRespone } from '../model/custom-http-response';
import { Review } from '../model/review';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public getDoctorReviews(username: string): Observable<Review[]> {
    return this.http.get<Review[]>(
      `${this.host}/reviews/search/findByDoctorUsername/${username}`
    );
  }
  public addReview(review: Review): Observable<number> {
    return this.http.post<number>(`${this.host}/reviews`, review);
  }
  public deleteReview(reviewId: bigint): Observable<number> {
    return this.http.delete<number>(`${this.host}/reviews/${reviewId}`);
  }
}
