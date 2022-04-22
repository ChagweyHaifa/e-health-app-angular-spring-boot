import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { CustomHttpRespone } from 'src/app/model/custom-http-response';
import { FileUploadStatus } from 'src/app/model/file-upload-status';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit, OnDestroy {
  private titleSubject = new BehaviorSubject<string>('Users');
  // a listener , an observable that will get notified if titleSubject value has been changed
  public titleAction$ = this.titleSubject.asObservable();

  public users: User[];
  public loggedInUser: User;

  public selectedUser: User;

  public editUser = new User();
  private currentUsername: string;

  fileName: string;
  profileImage: File;
  public refreshing: boolean = false;

  public fileStatus = new FileUploadStatus();

  private subscriptions: Subscription[] = [];
  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getUsers(true);
    this.loggedInUser = this.authenticationService.getUserFromLocalCache();
  }

  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(
              NotificationType.SUCCESS,
              `${response.length} user(s) loaded successfully.`
            );
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.refreshing = false;
        }
      )
    );
  }

  // ****display user info****
  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  // ****add new user****
  public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  onAddNewUser(userForm: NgForm): void {
    // console.log(userForm);
    // console.log(userForm.value);
    const formData = this.userService.createUserFormDate(
      null,
      userForm.value,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.clickButton('new-user-close');
          // make a call to the backend to get the new list of users
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          userForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} added successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.profileImage = null;
        }
      )
    );
  }

  // ****search user****
  public searchUsers(searchTerm: string): void {
    console.log('searchTerm = "' + searchTerm + '"');
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (
        user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
      ) {
        results.push(user);
      }
    }
    console.log('length' + results.length);
    this.users = results;
    // if (results.length === 0) {
    //   this.users = this.userService.getUsersFromLocalCache();
    // }
  }

  // ****edit user****
  // user selection
  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    // grab the current username
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }
  // userEdit form submission
  public onUpdateUser(): void {
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      this.editUser,
      this.profileImage
    );

    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.clickButton('edit-user-close');
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} updated successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.profileImage = null;
        }
      )
    );
  }
  // ****delete user****
  public onDeleteUser(username: string): void {
    this.subscriptions.push(
      this.userService.deleteUser(username).subscribe(
        (response: CustomHttpRespone) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getUsers(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }
  // ****change password****
  public onResetPassword(emailForm: NgForm): void {
    this.refreshing = true;
    const emailAddress = emailForm.value['reset-password-email'];
    this.subscriptions.push(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpRespone) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, error.error.message);
          this.refreshing = false;
        },
        () => emailForm.reset()
      )
    );
  }

  // edit user profile
  // we can pass loggedInUser variable
  onUpdateUserProfile(user: User) {
    this.refreshing = true;
    this.currentUsername =
      this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      user,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          // to update authorities on the template
          // this.loggedInUser = response;
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} updated successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.refreshing = false;
          this.profileImage = null;
        }
      )
    );
  }
  // update profile image
  updateProfileImage() {
    this.clickButton('profile-image-input');
  }

  onUpdateProfileImage() {
    const formData = new FormData();
    formData.append('username', this.loggedInUser.username);
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userService.updateProfileImage(formData).subscribe(
        (event: HttpEvent<any>) => {
          // this.reportUploadProgress(event);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.fileStatus.status = 'done';
        }
      )
    );
  }

  onLogOut(): void {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(
      NotificationType.SUCCESS,
      `You've been successfully logged out`
    );
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN;
    //|| this.getUserRole() === Role.SUPER_ADMIN
  }

  // public get isManager(): boolean {
  //   return this.isAdmin || this.getUserRole() === Role.MANAGER;
  // }

  // public get isAdminOrManager(): boolean {
  //   return this.isAdmin || this.isManager;
  // }

  // private methods
  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }

  public onProfileImageChange(event: Event): void {
    // console.log(event);
    this.profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = this.profileImage.name;
    // console.log(this.profileImage);
    // console.log("filename:" + this.fileName);
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }

  // private reportUploadProgress(event: HttpEvent<any>): void {
  //   switch (event.type) {
  //     case HttpEventType.UploadProgress:
  //       this.fileStatus.percentage = Math.round(
  //         (100 * event.loaded) / event.total
  //       );
  //       this.fileStatus.status = 'progress';
  //       break;
  //     case HttpEventType.Response:
  //       if (event.status === 200) {
  //         this.loggedInUser.profileImageUrl = `${
  //           event.body.profileImageUrl
  //         }?time=${new Date().getTime()}`;
  //         this.sendNotification(
  //           NotificationType.SUCCESS,
  //           `${event.body.firstName}\'s profile image updated successfully`
  //         );
  //         this.fileStatus.status = 'done';
  //         this.getUsers(false);
  //         break;
  //       } else {
  //         this.sendNotification(
  //           NotificationType.ERROR,
  //           `Unable to upload image. Please try again`
  //         );
  //         break;
  //       }
  //     default:
  //       `Finished all processes`;
  //   }
  // }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
