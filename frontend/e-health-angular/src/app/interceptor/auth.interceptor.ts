import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';
import { FormService } from '../service/form.service';
import { UserService } from '../service/user.service';
import { RatingService } from '../service/rating.service';
import { QuestionService } from '../service/question.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  // intercept the request and change it
  // it will intercept some particular routes to add the JWT

  constructor(
    private authenticationService: AuthenticationService,
    private formService: FormService,
    private userService: UserService,
    private ratingService: RatingService,
    private questionService: QuestionService
  ) {}

  intercept(
    httpRequest: HttpRequest<any>,
    httpHandler: HttpHandler
  ): Observable<HttpEvent<any>> {
    // eliminating some routes
    if (httpRequest.url.includes(`${this.authenticationService.host}/login`)) {
      // passing the httpRequest to the handler to continu his route
      return httpHandler.handle(httpRequest);
    }
    if (
      httpRequest.url.includes(
        `${this.authenticationService.host}/users/doctors/register`
      )
    ) {
      return httpHandler.handle(httpRequest);
    }

    if (
      httpRequest.url.includes(`${this.authenticationService.host}/register`)
    ) {
      return httpHandler.handle(httpRequest);
    }

    if (httpRequest.url.includes(`${this.formService.host}/specialities`)) {
      return httpHandler.handle(httpRequest);
    }

    if (httpRequest.url.includes(`${this.formService.host}/countries`)) {
      return httpHandler.handle(httpRequest);
    }

    if (httpRequest.url.includes(`${this.formService.host}/states`)) {
      return httpHandler.handle(httpRequest);
    }
    if (
      httpRequest.url.includes(
        `${this.formService.host}/cities/search/findByStateName`
      )
    ) {
      return httpHandler.handle(httpRequest);
    }

    if (
      httpRequest.url.includes(`${this.userService.host}/users/doctors/search`)
    ) {
      return httpHandler.handle(httpRequest);
    }

    if (httpRequest.url.includes(`${this.ratingService.host}/ratings/search`)) {
      return httpHandler.handle(httpRequest);
    }

    if (httpRequest.url.includes(`${this.questionService.host}/questions/`)) {
      return httpHandler.handle(httpRequest);
    }

    this.authenticationService.getToken();
    const token = this.authenticationService.getToken();
    // the httpRequest (original request) is mutable we can't modify it, we have to make a clone of it
    const request = httpRequest.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
    return httpHandler.handle(request);
  }
}
