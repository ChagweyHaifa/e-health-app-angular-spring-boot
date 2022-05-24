import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { HeaderType } from 'src/app/enum/header-type.enum';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { City } from 'src/app/model/city';
import { Country } from 'src/app/model/country';
import { State } from 'src/app/model/state';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css'],
})
export class SettingsComponent implements OnInit {
  editUserProfileForm: FormGroup;

  submitted: boolean = false;
  showLoading: boolean = false;
  countries: Country[];
  states: State[];
  subscriptions: Subscription[] = [];
  cities: City[];
  loggedInUser: User;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private userService: UserService
  ) {
    this.editUserProfileForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required]],
      gender: [''],
      active: [[Validators.required]],
      notLocked: [[Validators.required]],
      phoneNumber: '',
      address: this.formBuilder.group({
        id: '',
        country: '',
        state: '',
        city: '',
        street: '',
      }),
    });
  }

  ngOnInit(): void {
    this.loggedInUser = this.authenticationService.getUserFromLocalCache();
    console.log(this.loggedInUser);
    this.getCountries();
    if (this.loggedInUser.address != null) {
      if (this.loggedInUser.address.country != null) {
        this.getStates(this.loggedInUser.address.country);
      }
      if (this.loggedInUser.address.state != null) {
        this.getCities(this.loggedInUser.address.state);
      }
    }
    this.editUserProfileForm.patchValue(this.loggedInUser);
  }

  resetAddress(address: string) {
    switch (address) {
      case 'country': {
        this.f.address.patchValue({
          state: '',
          city: '',
          street: '',
        });
        break;
      }
      case 'state': {
        this.f.address.patchValue({
          city: '',
          street: '',
        });
        break;
      }
      case 'city': {
        this.f.address.patchValue({
          street: '',
        });
        break;
      }
    }
  }

  getCountries() {
    this.subscriptions.push(
      this.formService.getCountries().subscribe(
        (response: Country[]) => {
          this.countries = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getStates(countryName: string) {
    this.subscriptions.push(
      this.formService.getStates(countryName).subscribe(
        (response: State[]) => {
          this.states = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getCities(stateName: string) {
    this.subscriptions.push(
      this.formService.getCities(stateName).subscribe(
        (response: City[]) => {
          this.cities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  get f() {
    return this.editUserProfileForm.controls;
  }

  editUserProfile() {
    this.submitted = true;
    if (this.editUserProfileForm.invalid) {
      return;
    }
    this.showLoading = true;
    console.log(this.editUserProfileForm.value);
    this.subscriptions.push(
      this.userService
        .updateUser(null, this.editUserProfileForm.value)
        .subscribe(
          (response: HttpResponse<User>) => {
            this.showLoading = false;
            const token = response.headers.get(HeaderType.JWT_TOKEN);
            this.authenticationService.saveToken(token);
            this.authenticationService.addUserToLocalCache(response.body);
            this.loggedInUser =
              this.authenticationService.getUserFromLocalCache();
            this.editUserProfileForm.patchValue(this.loggedInUser);
            this.sendNotification(
              NotificationType.SUCCESS,
              'Y have edited your account informations successfully'
            );
          },
          (errorResponse: HttpErrorResponse) => {
            this.showLoading = false;

            this.sendNotification(
              NotificationType.ERROR,
              errorResponse.error.message
            );
          }
        )
    );
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      // if we didn't get any message from the backend, for example in case we didn't start the backend server
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }
}
