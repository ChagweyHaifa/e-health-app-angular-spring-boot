import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, NgForm, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Question } from 'src/app/model/question';
import { Speciality } from 'src/app/model/speciality';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { QuestionService } from 'src/app/service/question.service';

@Component({
  selector: 'app-questions',
  templateUrl: './questions.component.html',
  styleUrls: ['./questions.component.css'],
})
export class QuestionsComponent implements OnInit {
  private subscriptions: Subscription[] = [];
  specialities: Speciality[];
  myQuestionForm: FormGroup;

  selectedSpeciality: Speciality;
  files: File[] = [];

  constructor(
    private formService: FormService,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private questionService: QuestionService
  ) {
    this.myQuestionForm = this.formBuilder.group({
      speciality: ['', Validators.required],
      questionTitle: ['', Validators.required],
      question: ['', Validators.required],
      weight: ['', Validators.required],
      height: ['', Validators.required],
      files: '',
    });
  }

  ngOnInit(): void {
    this.getSpecialities();
  }

  // OnAskAQuestion() {
  //   this.myQuestionForm.patchValue({
  //     speciality: this.selectedSpeciality,
  //   });
  // }

  uploadMyQuestionFormFiles(event: Event) {
    const lengh: number = (<HTMLInputElement>event.target).files.length;
    for (var i = 0; i < lengh; i++) {
      this.files.push((<HTMLInputElement>event.target).files[i]);
    }
    // for (const file of this.files) {
    //   console.log(file);
    // }
  }

  addQuestion() {
    console.log(this.myQuestionForm.value);
    const formData = new FormData();
    formData.append('speciality', this.myQuestionForm.value.speciality);
    formData.append('questionTitle', this.myQuestionForm.value.questionTitle);
    formData.append('question', this.myQuestionForm.value.question);
    formData.append('height', this.myQuestionForm.value.weight);
    for (const file of this.files) {
      formData.append('document', file);
    }
    this.questionService.addQuestion(formData).subscribe(
      (response: Question) => {
        console.log(response);
      },

      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(
          NotificationType.ERROR,
          errorResponse.error.message
        );
      }
    );
  }

  getSpecialities() {
    this.subscriptions.push(
      this.formService.getSpecialities().subscribe(
        (response: Speciality[]) => {
          this.specialities = response;
          this.selectedSpeciality = this.specialities[1];
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

  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }
}
