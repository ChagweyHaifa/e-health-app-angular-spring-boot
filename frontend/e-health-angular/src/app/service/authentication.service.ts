  import { Injectable } from '@angular/core';
  import { HttpClient, HttpResponse, HttpErrorResponse } from '@angular/common/http';
  import { environment } from '../../environments/environment';
  import { Observable } from 'rxjs';
  import { User } from '../model/user';
  import { JwtHelperService } from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private jwtHelper = new JwtHelperService();
  public host = environment.apiUrl;

  private loggedInUsername: string = null;
  

  constructor(private http: HttpClient) {}
 

  public login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.host}/login`, user, { observe: 'response' });
  }

   // { observe: 'response' }
  // takes the whole response including the header to get the JSON Web Token
  // by default it just get the body 

  public register(user: User): Observable<User> {
    return this.http.post<User>(`${this.host}/register`, user);
  }

  public logOut(): void {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('users');
  }

  public saveToken(token: string ): void {
    // localStorage.setItem('token', null);
    localStorage.setItem('token', token);
   
  }
  public loadToken(): string {
// this.token = localStorage.getItem('token')!
// '!' non-null assertion operator to tell typescript that getItem function will never return null, i'm
//  sure that token item exists in local storage

// if token item does not exist in local storage, getItem function will return null else will return a string
// since i have telling typescript that token value can be null, this statement is considered true
    return localStorage.getItem('token');
  }

  public getToken(): string{
    return this.loadToken();
  }

  public addUserToLocalCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    const user = localStorage.getItem('user');
    // if (user != null){
    return JSON.parse(user);
    // }
    // return user;
  }

  // public getUserFromLocalCache(): User {
  //   // ! i'm telling typescript that user can't be null
  //   // i'm sure that user item exists in local storage
  //   return JSON.parse(localStorage.getItem('user')!);
  // }

  public isUserLoggedIn(): boolean {
    const token: string = this.loadToken();
    if (token != null && token !== ''){
      if (this.jwtHelper.decodeToken(token).sub != null || '') {
        // subject == username
        if (!this.jwtHelper.isTokenExpired(token)) {
          const loggedInUsername: string = this.jwtHelper.decodeToken(token).sub;
          console.log(loggedInUsername);
          return true;
        } 
      } 
    } 
    this.logOut();
    return false;
    
  }

  
}
